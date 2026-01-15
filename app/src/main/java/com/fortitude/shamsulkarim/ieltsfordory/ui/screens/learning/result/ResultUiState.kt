package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.result

import androidx.compose.ui.graphics.Color

/**
 * UI state for the Result screen.
 */
data class ResultUiState(
    val isLoading: Boolean = false,
    val grade: String = "A+",
    val gradeProgress: Float = 0.85f,
    val congratsTitle: String = "Well Done!",
    val congratsMessage: String = "You are doing great. Next time focus on improving accuracy",
    val wordResults: List<WordResult> = emptyList()
)

/**
 * Represents the result for a single word.
 */
data class WordResult(
    val id: String = "",
    val word: String = "",
    val meaning: String = "",
    val status: WordResultStatus = WordResultStatus.CORRECT,
    val statusLabel: String? = null,
    val feedbackMessage: String? = null,
    val isExpanded: Boolean = false,
    val performanceIndicators: List<PerformanceIndicator> = emptyList()
)

/**
 * Performance indicator for a word result (speed, accuracy, etc.)
 */
data class PerformanceIndicator(
    val type: IndicatorType,
    val isPositive: Boolean
)

enum class IndicatorType {
    SPEED,
    ACCURACY
}

/**
 * Status of word result.
 */
enum class WordResultStatus(val borderColor: Color) {
    CORRECT(Color(0xFF4052B5)),
    MISTAKEN(Color(0xFFFF6B6B))
}
