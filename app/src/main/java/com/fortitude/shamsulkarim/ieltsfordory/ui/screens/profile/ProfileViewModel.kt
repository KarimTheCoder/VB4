package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.LocalData
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.NotificationScheduler
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * UI state for ProfileScreen
 */
data class ProfileUiState(
    val alarmEnabled: Boolean = false,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 0,
    val formattedAlarmTime: String = "06:00 PM",
    val showTimePicker: Boolean = false
)

/**
 * ViewModel for ProfileScreen handling alarm settings and social links.
 */
class ProfileViewModel(
    private val appPreferences: AppPreferences,
    private val context: Context
) : ViewModel() {

    private val localData = LocalData(context)
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadAlarmSettings()
        initializeDefaultSettings()
    }

    private fun loadAlarmSettings() {
        val hour = localData.get_hour()
        val minute = localData.get_min()
        val enabled = localData.reminderStatus

        _uiState.update { current ->
            current.copy(
                alarmEnabled = enabled,
                alarmHour = hour,
                alarmMinute = minute,
                formattedAlarmTime = formatTime(hour, minute)
            )
        }
    }

    private fun initializeDefaultSettings() {
        // Initialize words per session if not set
        if (!appPreferences.contains(AppPreferences.KEY_WORDS_PER_SESSION)) {
            appPreferences.setWordsPerSession(5)
        }
        
        // Initialize repetition per session if not set
        if (!appPreferences.contains(AppPreferences.KEY_REPEATATION_PER_SESSION)) {
            appPreferences.setRepeatationPerSession(5)
        }
    }

    fun toggleAlarm(enabled: Boolean) {
        localData.setReminderStatus(enabled)
        _uiState.update { it.copy(alarmEnabled = enabled) }
        
        if (enabled) {
            NotificationScheduler.setReminder(context, AlarmReceiver::class.java, _uiState.value.alarmHour, _uiState.value.alarmMinute)
        } else {
            NotificationScheduler.cancelReminder(context, AlarmReceiver::class.java)
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
        localData.set_hour(hour)
        localData.set_min(minute)
        
        _uiState.update { current ->
            current.copy(
                alarmHour = hour,
                alarmMinute = minute,
                formattedAlarmTime = formatTime(hour, minute),
                showTimePicker = false
            )
        }
        
        if (_uiState.value.alarmEnabled) {
            NotificationScheduler.setReminder(context, AlarmReceiver::class.java, hour, minute)
        }
    }

    /**
     * Get the Play Store or App Gallery URL for rating.
     */
    fun getRateAppUrl(): String {
        return when {
            BuildConfig.FLAVOR.equals("free", ignoreCase = true) ->
                "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder"
            BuildConfig.FLAVOR.equals("huawei", ignoreCase = true) ->
                "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895"
            else ->
                "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro"
        }
    }

    /**
     * Get share text for sharing the app.
     */
    fun getShareUrl(): String {
        return when {
            BuildConfig.FLAVOR.equals("free", ignoreCase = true) ->
                "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder"
            BuildConfig.FLAVOR.equals("huawei", ignoreCase = true) ->
                "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895"
            else ->
                "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro"
        }
    }

    /**
     * Get bug report email subject.
     */
    fun getBugReportSubject(): String {
        return "VB4 - FL: ${BuildConfig.FLAVOR} VN: ${BuildConfig.VERSION_NAME} VC: ${BuildConfig.VERSION_CODE}"
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


