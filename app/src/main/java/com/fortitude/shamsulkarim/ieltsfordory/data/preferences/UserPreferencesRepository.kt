package com.fortitude.shamsulkarim.ieltsfordory.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(context, AppPreferences.NAME),
            SharedPreferencesMigration(context, "RemindMePref")
        )
    }
)

/**
 * Modern Jetpack DataStore repository consolidating all application preferences,
 * learning session configurations, theme modes, and practice reminder settings.
 *
 * Automatically migrates existing SharedPreferences keys upon first initialization.
 */
open class UserPreferencesRepository(private val context: Context) {

    private val dataStore by lazy { context.userDataStore }

    companion object {
        // Theme
        val KEY_DARK_MODE = intPreferencesKey(AppPreferences.KEY_DARK_MODE) // 0 = Light, 1 = Dark, 2 = System

        // Learning & Sessions
        val KEY_LEVEL = stringPreferencesKey(AppPreferences.KEY_LEVEL)
        val KEY_WORDS_PER_SESSION = intPreferencesKey(AppPreferences.KEY_WORDS_PER_SESSION)
        val KEY_REPEATATION_PER_SESSION = intPreferencesKey(AppPreferences.KEY_REPEATATION_PER_SESSION)
        val KEY_PRACTICE_MODE = stringPreferencesKey(AppPreferences.KEY_PRACTICE_MODE)
        val KEY_TOTAL_CORRECTS = intPreferencesKey(AppPreferences.KEY_TOTAL_CORRECTS)
        val KEY_MOST_MISTAKEN_WORD = stringPreferencesKey(AppPreferences.KEY_MOST_MISTAKEN_WORD)

        // Audio & Media
        val KEY_SOUND_STATE = booleanPreferencesKey(AppPreferences.KEY_SOUND_STATE)
        val KEY_PRONUN_STATE = booleanPreferencesKey(AppPreferences.KEY_PRONUN_STATE)
        val KEY_IMAGE_QUALITY = intPreferencesKey(AppPreferences.KEY_IMAGE_QUALITY)

        // User & Account
        val KEY_USER_NAME = stringPreferencesKey(AppPreferences.KEY_USER_NAME)
        val KEY_IS_SIGNED_IN = booleanPreferencesKey(AppPreferences.KEY_IS_SIGNED_IN)
        val KEY_TRIAL_END_DATE = longPreferencesKey(AppPreferences.KEY_TRIAL_END_DATE)
        val KEY_HOME_VISITED = booleanPreferencesKey(AppPreferences.KEY_HOME)
        val KEY_HOME_FRAGMENT_TRIAL_END = booleanPreferencesKey(AppPreferences.KEY_HOME_FRAGMENT_TRIAL_END)
        val KEY_PREV_SELECTION = intPreferencesKey(AppPreferences.KEY_PREV_SELECTION)
        val KEY_FAVORITE_COUNT_PROFILE = intPreferencesKey(AppPreferences.KEY_FAVORITE_COUNT_PROFILE)

        // Reminders (migrated from RemindMePref & AppPreferences)
        val KEY_REMINDER_STATUS = booleanPreferencesKey("reminderStatus")
        val KEY_REMINDER_HOUR = intPreferencesKey("hour")
        val KEY_REMINDER_MIN = intPreferencesKey("min")
        val KEY_DEFAULT_ALARM = booleanPreferencesKey(AppPreferences.KEY_DEFAULT_ALARM)

        // Exams / Categories
        val KEY_IS_IELTS_ACTIVE = booleanPreferencesKey(AppPreferences.KEY_IS_IELTS_ACTIVE)
        val KEY_IS_TOEFL_ACTIVE = booleanPreferencesKey(AppPreferences.KEY_IS_TOEFL_ACTIVE)
        val KEY_IS_SAT_ACTIVE = booleanPreferencesKey(AppPreferences.KEY_IS_SAT_ACTIVE)
        val KEY_IS_GRE_ACTIVE = booleanPreferencesKey(AppPreferences.KEY_IS_GRE_ACTIVE)
        val KEY_SECOND_LANGUAGE = stringPreferencesKey(AppPreferences.KEY_SECOND_LANGUAGE)
    }

    private val preferencesFlow: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    // --- Observable Flows ---

    val darkModeFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_DARK_MODE] ?: 0
    }

    val selectedLevelFlow: Flow<String> = preferencesFlow.map { prefs ->
        prefs[KEY_LEVEL] ?: "beginner"
    }

    val wordsPerSessionFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_WORDS_PER_SESSION] ?: 5
    }

    val repetitionPerSessionFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_REPEATATION_PER_SESSION] ?: 5
    }

    val soundStateFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_SOUND_STATE] ?: true
    }

    val pronunStateFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_PRONUN_STATE] ?: true
    }

    val imageQualityFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_IMAGE_QUALITY] ?: 1
    }

    val userNameFlow: Flow<String> = preferencesFlow.map { prefs ->
        prefs[KEY_USER_NAME] ?: "Doggo"
    }

    val totalCorrectsFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_TOTAL_CORRECTS] ?: 0
    }

    val reminderStatusFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_REMINDER_STATUS] ?: false
    }

    val reminderHourFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_REMINDER_HOUR] ?: 20
    }

    val reminderMinuteFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_REMINDER_MIN] ?: 0
    }

    val defaultAlarmSetFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_DEFAULT_ALARM] ?: false
    }

    val isIeltsActiveFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_IS_IELTS_ACTIVE] ?: true
    }

    val isToeflActiveFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_IS_TOEFL_ACTIVE] ?: true
    }

    val isSatActiveFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_IS_SAT_ACTIVE] ?: true
    }

    val isGreActiveFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_IS_GRE_ACTIVE] ?: true
    }

    val secondLanguageFlow: Flow<String> = preferencesFlow.map { prefs ->
        prefs[KEY_SECOND_LANGUAGE] ?: "english"
    }

    val prevWordSelectionFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_PREV_SELECTION] ?: 0
    }

    val favoriteCountProfileFlow: Flow<Int> = preferencesFlow.map { prefs ->
        prefs[KEY_FAVORITE_COUNT_PROFILE] ?: 0
    }

    val trialEndDateFlow: Flow<Long> = preferencesFlow.map { prefs ->
        prefs[KEY_TRIAL_END_DATE] ?: 0L
    }

    val homeVisitedFlow: Flow<Boolean> = preferencesFlow.map { prefs ->
        prefs[KEY_HOME_VISITED] ?: false
    }

    // --- Suspend Mutation Methods ---

    open suspend fun setDarkMode(mode: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = mode
        }
    }

    suspend fun setSelectedLevel(level: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LEVEL] = level
        }
    }

    open suspend fun setWordsPerSession(words: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_WORDS_PER_SESSION] = words
        }
    }

    open suspend fun setRepetitionPerSession(repetition: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_REPEATATION_PER_SESSION] = repetition
        }
    }

    suspend fun setSoundState(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_SOUND_STATE] = enabled
        }
    }

    suspend fun setPronunState(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_PRONUN_STATE] = enabled
        }
    }

    suspend fun setImageQuality(quality: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_IMAGE_QUALITY] = quality
        }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
        }
    }

    suspend fun setTotalCorrects(total: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_TOTAL_CORRECTS] = total
        }
    }

    suspend fun incrementTotalCorrects(count: Int) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_TOTAL_CORRECTS] ?: 0
            prefs[KEY_TOTAL_CORRECTS] = current + count
        }
    }

    suspend fun setReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_REMINDER_STATUS] = enabled
            prefs[KEY_REMINDER_HOUR] = hour
            prefs[KEY_REMINDER_MIN] = minute
        }
    }

    suspend fun setDefaultAlarmSet(isSet: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_ALARM] = isSet
        }
    }

    suspend fun setPracticeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_PRACTICE_MODE] = mode
        }
    }

    suspend fun setMostMistakenWord(word: String) {
        dataStore.edit { prefs ->
            prefs[KEY_MOST_MISTAKEN_WORD] = word
        }
    }

    suspend fun setSignedIn(isSignedIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_SIGNED_IN] = isSignedIn
        }
    }

    suspend fun setFilters(ielts: Boolean, toefl: Boolean, sat: Boolean, gre: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_IELTS_ACTIVE] = ielts
            prefs[KEY_IS_TOEFL_ACTIVE] = toefl
            prefs[KEY_IS_SAT_ACTIVE] = sat
            prefs[KEY_IS_GRE_ACTIVE] = gre
        }
    }

    suspend fun setSecondLanguage(lang: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SECOND_LANGUAGE] = lang
        }
    }

    suspend fun setPrevWordSelection(selection: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_PREV_SELECTION] = selection
        }
    }

    suspend fun setFavoriteCountProfile(count: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_FAVORITE_COUNT_PROFILE] = count
        }
    }

    suspend fun setHomeVisited(visited: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_HOME_VISITED] = visited
        }
    }

    // Direct snapshot access when required in suspend blocks
    suspend fun getUserName(): String = userNameFlow.first()
    suspend fun getDarkMode(): Int = darkModeFlow.first()
    suspend fun getWordsPerSession(): Int = wordsPerSessionFlow.first()
    suspend fun getRepetitionPerSession(): Int = repetitionPerSessionFlow.first()
    suspend fun getSoundState(): Boolean = soundStateFlow.first()
    suspend fun getPronunState(): Boolean = pronunStateFlow.first()
    suspend fun getImageQuality(): Int = imageQualityFlow.first()
    suspend fun getTotalCorrects(): Int = totalCorrectsFlow.first()
    suspend fun getSelectedLevel(): String = selectedLevelFlow.first()
    suspend fun getIsIeltsActive(): Boolean = isIeltsActiveFlow.first()
    suspend fun getIsToeflActive(): Boolean = isToeflActiveFlow.first()
    suspend fun getIsSatActive(): Boolean = isSatActiveFlow.first()
    suspend fun getIsGreActive(): Boolean = isGreActiveFlow.first()
    suspend fun getSecondLanguage(): String = secondLanguageFlow.first()
    suspend fun getPrevWordSelection(): Int = prevWordSelectionFlow.first()
    suspend fun getFavoriteCountProfile(): Int = favoriteCountProfileFlow.first()
    suspend fun isHomeVisited(): Boolean = homeVisitedFlow.first()
    suspend fun isTrialActive(): Boolean {
        val endDate = trialEndDateFlow.first()
        if (endDate == 0L) return false
        return System.currentTimeMillis() < endDate
    }
}
