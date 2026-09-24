package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for ProfileScreen handling reminder configuration, learning stats, and social links.
 * Backed by Jetpack DataStore and AndroidX WorkManager.
 */
class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context,
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeAlarmSettings()
        loadLearningStats()
        initializeContactUsInfo()
    }

    private fun initializeContactUsInfo() {
        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
        _uiState.update { current ->
            current.copy(
                contactUsEmail = BUG_REPORT_EMAIL,
                contactUsSubject = "$appName Support",
                contactUsBody = "App Name: $appName\nVersion: ${BuildConfig.VERSION_NAME}\nVersion Code: ${BuildConfig.VERSION_CODE}\n\n[Write your message here]"
            )
        }
    }

    private fun loadLearningStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val levels = listOf("beginner", "intermediate", "advanced")
            var total = 0
            var learned = 0

            levels.forEach { level ->
                total += vocabularyRepository.getTotalCount(level)
                learned += vocabularyRepository.getLearnedCount(level)
            }

            val left = total - learned

            _uiState.update { current ->
                current.copy(
                    totalWords = total,
                    learnedWords = learned,
                    wordsLeftToLearn = left
                )
            }
        }
    }

    private fun observeAlarmSettings() {
        viewModelScope.launch {
            userPreferencesRepository.reminderStatusFlow.collect { enabled ->
                _uiState.update { it.copy(alarmEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.reminderHourFlow.collect { hour ->
                _uiState.update { current ->
                    current.copy(
                        alarmHour = hour,
                        formattedAlarmTime = formatTime(hour, current.alarmMinute)
                    )
                }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.reminderMinuteFlow.collect { minute ->
                _uiState.update { current ->
                    current.copy(
                        alarmMinute = minute,
                        formattedAlarmTime = formatTime(current.alarmHour, minute)
                    )
                }
            }
        }
    }

    fun toggleAlarm(enabled: Boolean) {
        viewModelScope.launch {
            val hour = _uiState.value.alarmHour
            val minute = _uiState.value.alarmMinute
            userPreferencesRepository.setReminderSettings(enabled, hour, minute)

            if (enabled) {
                ReminderScheduler.scheduleReminder(context, hour, minute)
            } else {
                ReminderScheduler.cancelReminder(context)
            }
        }
    }

    /**
     * Show time picker dialog.
     */
    fun showTimePicker() {
        if (_uiState.value.alarmEnabled) {
            _uiState.update { it.copy(showTimePicker = true) }
        }
    }

    /**
     * Hide time picker dialog.
     */
    fun hideTimePicker() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun setAlarmTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val enabled = _uiState.value.alarmEnabled
            userPreferencesRepository.setReminderSettings(enabled, hour, minute)
            _uiState.update { it.copy(showTimePicker = false) }

            if (enabled) {
                ReminderScheduler.scheduleReminder(context, hour, minute)
            }
        }
    }

    /**
     * Get the Play Store URL for rating.
     */
    fun getRateAppUrl(): String {
        return "https://play.google.com/store/apps/details?id=${context.packageName}"
    }

    /**
     * Get share text for sharing the app.
     */
    fun getShareUrl(): String {
        return "https://play.google.com/store/apps/details?id=${context.packageName}"
    }

    /**
     * Get bug report email subject.
     */
    fun getBugReportSubject(): String {
        return "VB4 - VN: ${BuildConfig.VERSION_NAME} VC: ${BuildConfig.VERSION_CODE}"
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse("$hour:$minute")
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "06:00 PM"
        }
    }

    companion object {
        const val FACEBOOK_URL = "https://www.facebook.com/FortitudeLearn/"
        const val INSTAGRAM_URL = "https://www.instagram.com/fortitudelearn/"
        const val BUG_REPORT_EMAIL = "fortitudedevs@gmail.com"
    }
}
