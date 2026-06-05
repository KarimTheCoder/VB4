package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    val isPremium: Boolean = false,
    // Sign-in state
    val isSignedIn: Boolean = false,
    val userName: String = "Doggo",
    val userEmail: String = "Sign in to sync your progress",
    val isSignInInProgress: Boolean = false,
    val showSyncDialog: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    // Huawei flavor hides sign-in
    val showSignInSection: Boolean = true
)

/**
 * Compose ViewModel for SettingsScreen using StateFlow.
 */
class SettingsComposeViewModel(
    private val appPreferences: AppPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsComposeUiState())
    val uiState: StateFlow<SettingsComposeUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load all settings from preferences.
     */
    fun loadSettings() {
        val sound = appPreferences.getBool("soundState", true)
        val pronun = appPreferences.getBool("pronunState", true)
        val imageQuality = appPreferences.getInt("imageQuality", 1)
        val darkMode = appPreferences.getInt(AppPreferences.KEY_DARK_MODE, 0)
        val wps = appPreferences.getInt(AppPreferences.KEY_WORDS_PER_SESSION, 5)
        val rps = appPreferences.getInt(AppPreferences.KEY_REPEATATION_PER_SESSION, 5)
        val ielts = appPreferences.getBool("isIELTSActive", true)
        val toefl = appPreferences.getBool("isTOEFLActive", true)
        val sat = appPreferences.getBool("isSATActive", true)
        val gre = appPreferences.getBool("isGREActive", true)
        val isSpanish = (appPreferences.getString("secondlanguage", "english") ?: "english")
            .equals("spanish", ignoreCase = true)
        val isTrialActive = appPreferences.isTrialActive()
        val isPremium = appPreferences.isPremium()
        val showSignIn = !BuildConfig.FLAVOR.equals("huawei", ignoreCase = true)

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
                isPremium = isPremium,
                isSignedIn = isSignedIn,
                userName = userName,
                userEmail = userEmail,
                showSignInSection = showSignIn
            )
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

    // ========== Settings ==========

    fun setSound(enabled: Boolean) {
        appPreferences.setBool("soundState", enabled)
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setPronunciation(enabled: Boolean) {
        appPreferences.setBool("pronunState", enabled)
        _uiState.update { it.copy(pronunciationEnabled = enabled) }
    }

    fun setImageQuality(index: Int) {
        appPreferences.setInt("imageQuality", index)
        _uiState.update { it.copy(imageQualityIndex = index) }
    }

    fun setDarkMode(index: Int) {
        appPreferences.setInt(AppPreferences.KEY_DARK_MODE, index)
        _uiState.update { it.copy(darkModeIndex = index) }
    }

    fun setWordsPerSession(position: Int) {
        val value = mapPositionToValue(position)
        appPreferences.setInt(AppPreferences.KEY_WORDS_PER_SESSION, value)
        _uiState.update { it.copy(wordsPerSession = value) }
    }

    fun setRepetitionsPerSession(position: Int) {
        val value = mapPositionToValue(position)
        appPreferences.setInt(AppPreferences.KEY_REPEATATION_PER_SESSION, value)
        _uiState.update { it.copy(repetitionsPerSession = value) }
    }

    fun setIeltsActive(active: Boolean) {
        if (ensureAtLeastOne(active, _uiState.value.toeflActive, _uiState.value.satActive, _uiState.value.greActive)) {
            appPreferences.setBool("isIELTSActive", active)
            _uiState.update { it.copy(ieltsActive = active) }
        } else {
            showError("At least select one")
        }
    }

    fun setToeflActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, active, _uiState.value.satActive, _uiState.value.greActive)) {
            appPreferences.setBool("isTOEFLActive", active)
            _uiState.update { it.copy(toeflActive = active) }
        } else {
            showError("At least select one")
        }
    }

    fun setSatActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, _uiState.value.toeflActive, active, _uiState.value.greActive)) {
            appPreferences.setBool("isSATActive", active)
            _uiState.update { it.copy(satActive = active) }
        } else {
            showError("At least select one")
        }
    }

    fun setGreActive(active: Boolean) {
        if (ensureAtLeastOne(_uiState.value.ieltsActive, _uiState.value.toeflActive, _uiState.value.satActive, active)) {
            appPreferences.setBool("isGREActive", active)
            _uiState.update { it.copy(greActive = active) }
        } else {
            showError("At least select one")
        }
    }

    fun toggleSpanish() {
        val newValue = !_uiState.value.isSpanishEnabled
        appPreferences.setString("secondlanguage", if (newValue) "spanish" else "english")
        _uiState.update { it.copy(isSpanishEnabled = newValue) }
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

    fun canUseDarkMode(): Boolean {
        val state = _uiState.value
        return state.isPremium || state.isTrialActive || !BuildConfig.FLAVOR.equals("free", ignoreCase = true)
    }

    companion object {
        const val PRIVACY_POLICY_URL = "https://banglish1.wixsite.com/vbprivacypolicy"
    }
}
