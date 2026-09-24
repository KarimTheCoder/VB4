package com.fortitude.shamsulkarim.ieltsfordory.data.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Repository to observe theme changes from Jetpack DataStore (UserPreferencesRepository).
 */
class ThemeRepository(private val userPreferencesRepository: UserPreferencesRepository) {

    /**
     * Flow that emits the current Dark Mode index:
     * 0 = Light, 1 = Dark, 2 = System
     */
    val themeMode: Flow<Int> = userPreferencesRepository.darkModeFlow
}
