package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import androidx.compose.ui.graphics.Color

/**
 * Represents the UI state for the Home screen.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val infoBannerText: String = "You will learn 3 new words, you can skip any words you already know",
    val words: List<WordItem> = emptyList(),
    val dailyProgress: DailyGoalProgress = DailyGoalProgress()
)

/**
 * Represents the daily learning goals and progress for the dashboard.
 */
data class DailyGoalProgress(
    val totalWordsTarget: Int = 25,
    val totalWordsLearned: Int = 17, // 68%
    val newWordsLearned: Int = 1,
    val newWordsTarget: Int = 3,
    val learnWordsLearned: Int = 2,
    val learnWordsTarget: Int = 3,
    val reviewWordsLearned: Int = 3,
    val reviewWordsTarget: Int = 3
)

/**
 * Represents a single word item with its progress.
 */
data class WordItem(
    val id: String = "",
    val text: String,
    val progress: Float,
    val progressColor: Color,
    val type: String = "New"
)
