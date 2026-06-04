package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile

/**
 * UI state for ProfileScreen
 */
data class ProfileUiState(
    val alarmEnabled: Boolean = false,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 0,
    val formattedAlarmTime: String = "06:00 PM",
    val showTimePicker: Boolean = false,
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val wordsLeftToLearn: Int = 0,
    val contactUsEmail: String = "",
    val contactUsSubject: String = "",
    val contactUsBody: String = ""
)
