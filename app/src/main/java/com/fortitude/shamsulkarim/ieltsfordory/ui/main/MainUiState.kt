package com.fortitude.shamsulkarim.ieltsfordory.ui.main

/**
 * UI state for the main screen with bottom navigation.
 */
data class MainUiState(
    val isConnected: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null,
    val isSyncing: Boolean = false,
    val syncError: String? = null
)
