package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.sync.SyncManager
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for SettingsScreen (Compose version)
 */
data class SettingsComposeUiState(
    val isLoading: Boolean = true,
    val soundEnabled: Boolean = true,
    val pronunciationEnabled: Boolean = true,
    val imageQualityIndex: Int = 1,
    val darkModeIndex: Int = 0, // 0 = Light, 1 = Dark, 2 = System
    val wordsPerSession: Int = 5,
    val repetitionsPerSession: Int = 5,
    val ieltsActive: Boolean = true,
    val toeflActive: Boolean = true,
    val satActive: Boolean = true,
    val greActive: Boolean = true,
    val isSpanishEnabled: Boolean = false,
    val isTrialActive: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    // Sign-in state
    val isSignedIn: Boolean = false,
    val userName: String = "Doggo",
    val userEmail: String = "Sign in to sync your progress",
    val isSignInInProgress: Boolean = false,
    val showSyncDialog: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val showSignInSection: Boolean = true
)

/**
 * Snapshot of persisted settings used to detect unsaved changes.
 */
data class SettingsSnapshot(
    val soundEnabled: Boolean = true,
    val pronunciationEnabled: Boolean = true,
    val imageQualityIndex: Int = 1,
    val darkModeIndex: Int = 0,
    val wordsPerSession: Int = 5,
    val repetitionsPerSession: Int = 5,
    val ieltsActive: Boolean = true,
    val toeflActive: Boolean = true,
    val satActive: Boolean = true,
    val greActive: Boolean = true,
    val isSpanishEnabled: Boolean = false
)

/**
 * Compose ViewModel for SettingsScreen using StateFlow.
 */
class SettingsComposeViewModel(
    private val appPreferences: AppPreferences,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val syncManager: SyncManager,
    private val sessionWordsRepository: SessionWordsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsComposeUiState())
    val uiState: StateFlow<SettingsComposeUiState> = _uiState.asStateFlow()

    private var savedSnapshot = SettingsSnapshot()

    init {
        loadSettings()
    }

    private fun checkHasChanges(state: SettingsComposeUiState): Boolean {
        return state.soundEnabled != savedSnapshot.soundEnabled ||
            state.pronunciationEnabled != savedSnapshot.pronunciationEnabled ||
            state.imageQualityIndex != savedSnapshot.imageQualityIndex ||
            state.darkModeIndex != savedSnapshot.darkModeIndex ||
            state.wordsPerSession != savedSnapshot.wordsPerSession ||
            state.repetitionsPerSession != savedSnapshot.repetitionsPerSession ||
            state.ieltsActive != savedSnapshot.ieltsActive ||
            state.toeflActive != savedSnapshot.toeflActive ||
            state.satActive != savedSnapshot.satActive ||
            state.greActive != savedSnapshot.greActive ||
            state.isSpanishEnabled != savedSnapshot.isSpanishEnabled
    }

    /**
     * Load all settings from preferences.
     */
    fun loadSettings() {
        viewModelScope.launch {
            val sound = userPreferencesRepository.getSoundState()
            val pronun = userPreferencesRepository.getPronunState()
            val imageQuality = userPreferencesRepository.getImageQuality()
            val darkMode = userPreferencesRepository.getDarkMode()
            val wps = userPreferencesRepository.getWordsPerSession()
            val rps = userPreferencesRepository.getRepetitionPerSession()
            val ielts = userPreferencesRepository.getIsIeltsActive()
            val toefl = userPreferencesRepository.getIsToeflActive()
            val sat = userPreferencesRepository.getIsSatActive()
            val gre = userPreferencesRepository.getIsGreActive()
            val isSpanish = userPreferencesRepository.getSecondLanguage().equals("spanish", ignoreCase = true)
            val isTrialActive = appPreferences.isTrialActive()

            // Sync sign-in state with Firebase (source of truth) rather than just prefs
            val firebaseUser = authRepository.getCurrentUser()
            val isSignedIn = firebaseUser != null
            val userName = firebaseUser?.displayName ?: "Doggo"
            val userEmail = firebaseUser?.email ?: "Sign in to sync your progress"

            // Keep prefs in sync with actual Firebase state
            appPreferences.setSignedIn(isSignedIn)
            if (isSignedIn) {
                appPreferences.setUserName(userName)
            }

            savedSnapshot = SettingsSnapshot(
                soundEnabled = sound,
                pronunciationEnabled = pronun,
                imageQualityIndex = imageQuality,
                darkModeIndex = darkMode,
                wordsPerSession = wps,
                repetitionsPerSession = rps,
                ieltsActive = ielts,
                toeflActive = toefl,
                satActive = sat,
                greActive = gre,
                isSpanishEnabled = isSpanish
            )

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    soundEnabled = sound,
                    pronunciationEnabled = pronun,
                    imageQualityIndex = imageQuality,
                    darkModeIndex = darkMode,
                    wordsPerSession = wps,
                    repetitionsPerSession = rps,
                    ieltsActive = ielts,
                    toeflActive = toefl,
                    satActive = sat,
                    greActive = gre,
                    isSpanishEnabled = isSpanish,
                    isTrialActive = isTrialActive,
                    hasUnsavedChanges = false,
                    isSignedIn = isSignedIn,
                    userName = userName,
                    userEmail = userEmail,
                    showSignInSection = true
                )
            }
        }
    }

    // ========== Auth ==========

    fun signIn(activity: Activity) {
        _uiState.update { it.copy(isSignInInProgress = true) }

        authRepository.signIn(activity, object : AuthRepository.AuthCallback {
            override fun onSuccess(user: FirebaseUser) {
                val name = user.displayName ?: "User"
                val email = user.email ?: ""
                appPreferences.setSignedIn(true)
                appPreferences.setUserName(name)
                syncManager.startSync(user.uid, null)
                _uiState.update { current ->
                    current.copy(
                        isSignedIn = true,
                        userName = name,
                        userEmail = email,
                        isSignInInProgress = false,
                        toastMessage = "Successfully signed in"
                    )
                }
            }

            override fun onFailure(e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isSignInInProgress = false,
                        toastMessage = "Sign-in failed: ${e.localizedMessage}"
                    )
                }
            }
        })
    }

    fun signOut() {
        authRepository.signOut()
        appPreferences.setSignedIn(false)
        _uiState.update { current ->
            current.copy(
                isSignedIn = false,
                userName = "Doggo",
                userEmail = "Sign in to sync your progress",
                toastMessage = "Sign-out complete!"
            )
        }
    }

    // ========== Settings (Draft editing) ==========

    fun setSound(enabled: Boolean) {
        _uiState.update { current ->
            val updated = current.copy(soundEnabled = enabled)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setPronunciation(enabled: Boolean) {
        _uiState.update { current ->
            val updated = current.copy(pronunciationEnabled = enabled)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setImageQuality(index: Int) {
        _uiState.update { current ->
            val updated = current.copy(imageQualityIndex = index)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setDarkMode(index: Int) {
        _uiState.update { current ->
            val updated = current.copy(darkModeIndex = index)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setWordsPerSession(value: Int) {
        _uiState.update { current ->
            val updated = current.copy(wordsPerSession = value)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setRepetitionsPerSession(value: Int) {
        _uiState.update { current ->
            val updated = current.copy(repetitionsPerSession = value)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    fun setIeltsActive(active: Boolean) {
        if (ensureAtLeastOne(active, _uiState.value.toeflActive, _uiState.value.satActive, _uiState.value.greActive)) {
            _uiState.update { current ->
                val updated = current.copy(ieltsActive = active)
                updated.copy(hasUnsavedChanges = checkHasChanges(updated))
            }
        } else {
            showError("At least select one")
        }
    }

    fun setToeflActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, active, _uiState.value.satActive, _uiState.value.greActive)) {
            _uiState.update { current ->
                val updated = current.copy(toeflActive = active)
                updated.copy(hasUnsavedChanges = checkHasChanges(updated))
            }
        } else {
            showError("At least select one")
        }
    }

    fun setSatActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, _uiState.value.toeflActive, active, _uiState.value.greActive)) {
            _uiState.update { current ->
                val updated = current.copy(satActive = active)
                updated.copy(hasUnsavedChanges = checkHasChanges(updated))
            }
        } else {
            showError("At least select one")
        }
    }

    fun setGreActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, _uiState.value.toeflActive, _uiState.value.satActive, active)) {
            _uiState.update { current ->
                val updated = current.copy(greActive = active)
                updated.copy(hasUnsavedChanges = checkHasChanges(updated))
            }
        } else {
            showError("At least select one")
        }
    }

    fun toggleSpanish() {
        val newValue = !_uiState.value.isSpanishEnabled
        _uiState.update { current ->
            val updated = current.copy(isSpanishEnabled = newValue)
            updated.copy(hasUnsavedChanges = checkHasChanges(updated))
        }
    }

    // ========== Persistence & Discard ==========

    fun saveSettings(onSaved: () -> Unit = {}) {
        val current = _uiState.value

        // Persist to DataStore
        viewModelScope.launch {
            userPreferencesRepository.setSoundState(current.soundEnabled)
            userPreferencesRepository.setPronunState(current.pronunciationEnabled)
            userPreferencesRepository.setImageQuality(current.imageQualityIndex)
            userPreferencesRepository.setDarkMode(current.darkModeIndex)
            userPreferencesRepository.setWordsPerSession(current.wordsPerSession)
            userPreferencesRepository.setRepetitionPerSession(current.repetitionsPerSession)
            userPreferencesRepository.setFilters(current.ieltsActive, current.toeflActive, current.satActive, current.greActive)
            userPreferencesRepository.setSecondLanguage(if (current.isSpanishEnabled) "spanish" else "english")
        }

        // Notify learning session if word/session configuration changed
        val sessionWordsChanged = current.wordsPerSession != savedSnapshot.wordsPerSession ||
            current.repetitionsPerSession != savedSnapshot.repetitionsPerSession ||
            current.ieltsActive != savedSnapshot.ieltsActive ||
            current.toeflActive != savedSnapshot.toeflActive ||
            current.satActive != savedSnapshot.satActive ||
            current.greActive != savedSnapshot.greActive ||
            current.isSpanishEnabled != savedSnapshot.isSpanishEnabled

        if (sessionWordsChanged) {
            sessionWordsRepository.requestHomeRefresh()
            sessionWordsRepository.clearSession()
        }

        // Update baseline snapshot
        savedSnapshot = SettingsSnapshot(
            soundEnabled = current.soundEnabled,
            pronunciationEnabled = current.pronunciationEnabled,
            imageQualityIndex = current.imageQualityIndex,
            darkModeIndex = current.darkModeIndex,
            wordsPerSession = current.wordsPerSession,
            repetitionsPerSession = current.repetitionsPerSession,
            ieltsActive = current.ieltsActive,
            toeflActive = current.toeflActive,
            satActive = current.satActive,
            greActive = current.greActive,
            isSpanishEnabled = current.isSpanishEnabled
        )

        _uiState.update { it.copy(hasUnsavedChanges = false, toastMessage = "Settings saved") }
        onSaved()
    }

    fun discardChanges() {
        _uiState.update { current ->
            current.copy(
                soundEnabled = savedSnapshot.soundEnabled,
                pronunciationEnabled = savedSnapshot.pronunciationEnabled,
                imageQualityIndex = savedSnapshot.imageQualityIndex,
                darkModeIndex = savedSnapshot.darkModeIndex,
                wordsPerSession = savedSnapshot.wordsPerSession,
                repetitionsPerSession = savedSnapshot.repetitionsPerSession,
                ieltsActive = savedSnapshot.ieltsActive,
                toeflActive = savedSnapshot.toeflActive,
                satActive = savedSnapshot.satActive,
                greActive = savedSnapshot.greActive,
                isSpanishEnabled = savedSnapshot.isSpanishEnabled,
                hasUnsavedChanges = false
            )
        }
    }

    // ========== Toast / Error ==========

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    // ========== Helpers ==========

    private fun ensureAtLeastOne(i: Boolean, t: Boolean, s: Boolean, g: Boolean): Boolean {
        return i || t || s || g
    }

    private fun mapPositionToValue(position: Int): Int {
        return when (position) {
            0 -> 25
            1 -> 20
            2 -> 15
            3 -> 10
            4 -> 5
            5 -> 4
            else -> 3
        }
    }

    fun valueToPosition(value: Int): Int {
        return when (value) {
            25 -> 0
            20 -> 1
            15 -> 2
            10 -> 3
            5 -> 4
            4 -> 5
            else -> 6
        }
    }



    companion object {
        const val PRIVACY_POLICY_URL = "https://banglish1.wixsite.com/vbprivacypolicy"
    }
}
