package com.fortitude.shamsulkarim.ieltsfordory.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.GetCurrentUserUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.IsUserAuthenticatedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.UpdateUserDataUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetFavLearnedStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for MainActivity, managing:
 * - Connectivity state
 * - Authentication state
 * - Firebase sync on app stop
 */
class MainViewModel(
    private val isConnectedUseCase: IsConnectedUseCase,
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val getFavLearnedStateUseCase: GetFavLearnedStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    /**
     * Refresh connectivity and authentication state.
     * Called on app resume or when needed.
     */
    fun refreshState() {
        val isConnected = isConnectedUseCase.execute()
        val isAuthenticated = isUserAuthenticatedUseCase.execute()
        val userId = if (isAuthenticated) {
            try {
                getCurrentUserUseCase.execute()?.uid
            } catch (e: Exception) {
                null
            }
        } else null

        _uiState.update { current ->
            current.copy(
                isConnected = isConnected,
                isAuthenticated = isAuthenticated,
                currentUserId = userId
            )
        }
    }

    /**
     * Sync user data to Firebase.
     * Called when app stops if user is authenticated and connected.
     */
    fun syncToFirebase(userName: String) {
        val state = _uiState.value
        if (!state.isAuthenticated || !state.isConnected) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSyncing = true, syncError = null) }
            try {
                val userId = state.currentUserId ?: return@launch
                val favLearnedState = getFavLearnedStateUseCase.execute(userName)
                updateUserDataUseCase.execute(userId, favLearnedState, null)
                _uiState.update { it.copy(isSyncing = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isSyncing = false, syncError = e.message ?: "Sync failed") 
                }
            }
        }
    }

    /**
     * Initialize default SharedPreferences if not already set.
     * This preserves the legacy behavior from MainActivity.
     */
    fun initializeDefaultPreferences(context: Context) {
        val sp = context.getSharedPreferences(
            "com.example.shamsulkarim.vocabulary", 
            Context.MODE_PRIVATE
        )
        if (!sp.contains("soundState")) {
            sp.edit()
                .putBoolean("soundState", true)
                .putInt("totalCorrects", 0)
                .apply()
        }
    }

    /**
     * Get the username from SharedPreferences.
     */
    fun getUserName(context: Context): String {
        val sp = context.getSharedPreferences(
            "com.example.shamsulkarim.vocabulary",
            Context.MODE_PRIVATE
        )
        return sp.getString("userName", "Boo") ?: "Boo"
    }
}


