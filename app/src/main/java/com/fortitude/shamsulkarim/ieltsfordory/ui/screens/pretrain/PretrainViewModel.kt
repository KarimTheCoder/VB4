package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.pretrain

import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedCountUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetTotalCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for PretrainScreen
 */
data class PretrainUiState(
    val level: String = "beginner",
    val levelDisplayName: String = "Beginner",
    val learnedCount: Int = 0,
    val totalCount: Int = 0,
    val progressPercentage: Float = 0f,
    val progressText: String = "0/0",
    val isSpanishEnabled: Boolean = false,
    val isTooEasyEnabled: Boolean = true,
    val showPurchaseCard: Boolean = false,
    val isLoading: Boolean = true
)

/**
 * ViewModel for PretrainScreen handling training configuration and progress display.
 */
class PretrainViewModel(
    private val getLearnedCountUseCase: GetLearnedCountUseCase,
    private val getTotalCountUseCase: GetTotalCountUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PretrainUiState())
    val uiState: StateFlow<PretrainUiState> = _uiState.asStateFlow()

    init {
        loadTrainingState()
    }

    /**
     * Load initial training state from preferences.
     */
    fun loadTrainingState() {
        val level = appPreferences.getLevel() ?: "beginner"
        val levelDisplayName = getLevelDisplayName(level)
        
        val learnedCount = getLearnedCountUseCase.execute(level)
        val totalCount = getTotalCountUseCase.execute(level)
        val progressPercentage = if (totalCount > 0) learnedCount.toFloat() / totalCount else 0f
        
        val secondLanguage = appPreferences.getSecondLanguage() ?: "english"
        val isSpanishEnabled = secondLanguage.equals("spanish", ignoreCase = true)
        
        val isIeltsActive = appPreferences.isIELTSActive()
        val isToeflActive = appPreferences.isTOEFLActive()
        val isTooEasyEnabled = !isIeltsActive && !isToeflActive
        
        // Show purchase card for free version only and if not premium
        val showPurchaseCard = !BuildConfig.FLAVOR.equals("pro", ignoreCase = true) 
            && !appPreferences.isPremium()

        _uiState.update { current ->
            current.copy(
                level = level,
                levelDisplayName = levelDisplayName,
                learnedCount = learnedCount,
                totalCount = totalCount,
                progressPercentage = progressPercentage,
                progressText = "$learnedCount/$totalCount",
                isSpanishEnabled = isSpanishEnabled,
                isTooEasyEnabled = isTooEasyEnabled,
                showPurchaseCard = showPurchaseCard,
                isLoading = false
            )
        }
    }

    /**
     * Toggle Spanish language mode.
     */
    fun toggleSpanishMode(enabled: Boolean) {
        val newLanguage = if (enabled) "spanish" else "english"
        appPreferences.setSecondLanguage(newLanguage)
        
        _uiState.update { it.copy(isSpanishEnabled = enabled) }
    }

    /**
     * Toggle "Too Easy" mode (excludes IELTS/TOEFL words when enabled).
     */
    fun toggleTooEasyMode(enabled: Boolean) {
        if (enabled) {
            appPreferences.setIELTSActive(false)
            appPreferences.setTOEFLActive(false)
        } else {
            appPreferences.setIELTSActive(true)
            appPreferences.setTOEFLActive(true)
        }
        
        _uiState.update { it.copy(isTooEasyEnabled = enabled) }
    }

    /**
     * Get display name for level.
     */
    private fun getLevelDisplayName(level: String): String {
        return when (level.lowercase()) {
            "beginner" -> "Beginner"
            "intermediate" -> "Intermediate"
            "advance" -> "Advanced"
            else -> level.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Refresh progress counts (call when returning to screen).
     */
    fun refreshProgress() {
        val level = _uiState.value.level
        val learnedCount = getLearnedCountUseCase.execute(level)
        val totalCount = getTotalCountUseCase.execute(level)
        val progressPercentage = if (totalCount > 0) learnedCount.toFloat() / totalCount else 0f

        _uiState.update { current ->
            current.copy(
                learnedCount = learnedCount,
                totalCount = totalCount,
                progressPercentage = progressPercentage,
                progressText = "$learnedCount/$totalCount"
            )
        }
    }
}


