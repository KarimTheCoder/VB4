package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import androidx.compose.ui.graphics.Color

/**
 * Represents the UI state for the Home screen.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val infoBannerText: String = "You will learn 3 new words, you can skip any words you already know",
    val words: List<WordItem> = emptyList()
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
