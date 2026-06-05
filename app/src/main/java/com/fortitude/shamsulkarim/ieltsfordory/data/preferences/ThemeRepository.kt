package com.fortitude.shamsulkarim.ieltsfordory.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository to observe theme changes from SharedPreferences.
 */
class ThemeRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE)

    /**
     * Flow that emits the current Dark Mode index:
     * 0 = Light, 1 = Dark, 2 = System
     */
    val themeMode: Flow<Int> = callbackFlow {
        // Emit initial value
        trySend(sharedPreferences.getInt(AppPreferences.KEY_DARK_MODE, 0))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
            if (key == AppPreferences.KEY_DARK_MODE) {
                trySend(sharedPrefs.getInt(AppPreferences.KEY_DARK_MODE, 0))
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}
