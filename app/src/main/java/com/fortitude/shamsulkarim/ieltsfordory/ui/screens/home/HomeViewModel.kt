package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.home

import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedCountUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetTotalCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

/**
 * UI state for HomeScreen
 */
data class HomeUiState(
    val beginnerPercentage: Int = 0,
    val intermediatePercentage: Int = 0,
    val advancePercentage: Int = 0,
    val trialStatus: TrialStatus = TrialStatus.Hidden,
    val showTrialEndedDialog: Boolean = false,
    val isLoading: Boolean = true
)

/**
 * Trial status variants
 */
sealed class TrialStatus {
    data object Hidden : TrialStatus()
    data object Active : TrialStatus()
    data object Ended : TrialStatus()
    data object Premium : TrialStatus()
}

/**
 * ViewModel for HomeScreen handling progress calculation and trial status.
 */
class HomeViewModel(
    private val getLearnedCountUseCase: GetLearnedCountUseCase,
    private val getTotalCountUseCase: GetTotalCountUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
        checkTrialStatus()
        markHomeVisited()
    }

    /**
     * Load progress percentages for all levels.
     */
    fun loadProgress() {
        val beginnerTotal = getTotalCountUseCase.execute("beginner")
        val beginnerLearned = getLearnedCountUseCase.execute("beginner")
        val beginnerPercentage = if (beginnerTotal > 0) (beginnerLearned * 100) / beginnerTotal else 0

        val intermediateTotal = getTotalCountUseCase.execute("intermediate")
        val intermediateLearned = getLearnedCountUseCase.execute("intermediate")
        val intermediatePercentage = if (intermediateTotal > 0) (intermediateLearned * 100) / intermediateTotal else 0

        val advanceTotal = getTotalCountUseCase.execute("advance")
        val advanceLearned = getLearnedCountUseCase.execute("advance")
        val advancePercentage = if (advanceTotal > 0) (advanceLearned * 100) / advanceTotal else 0

        _uiState.update { current ->
            current.copy(
                beginnerPercentage = beginnerPercentage,
                intermediatePercentage = intermediatePercentage,
                advancePercentage = advancePercentage,
                isLoading = false
            )
        }
    }

    /**
     * Check and update trial status based on app preferences.
     */
    private fun checkTrialStatus() {
        // Skip for pro version
        if (BuildConfig.FLAVOR.equals("pro", ignoreCase = true)) {
            _uiState.update { it.copy(trialStatus = TrialStatus.Hidden) }
            return
        }

        val trialEndDate = appPreferences.getTrialEndDate()
        if (trialEndDate == 0L) {
            _uiState.update { it.copy(trialStatus = TrialStatus.Hidden) }
            return
        }

        val today = Calendar.getInstance().timeInMillis
        val isPremium = appPreferences.isPremium()

        val status = when {
            isPremium -> TrialStatus.Premium
            trialEndDate > today -> TrialStatus.Active
            else -> TrialStatus.Ended
        }

        val shouldShowDialog = status == TrialStatus.Ended && 
            !appPreferences.isHomeFragmentTrialEndShown()

        _uiState.update { current ->
            current.copy(
                trialStatus = status,
                showTrialEndedDialog = shouldShowDialog
            )
        }
    }

    /**
     * Mark home as visited for first-time experience.
     */
    private fun markHomeVisited() {
        if (!appPreferences.isHomeVisited()) {
            appPreferences.setHomeVisited(true)
        }
    }

    /**
     * Set the selected level before navigating to training.
     */
    fun selectLevel(level: String) {
        appPreferences.setLevel(level)
    }

    /**
     * Dismiss the trial ended dialog.
     */
    fun dismissTrialDialog() {
        appPreferences.setHomeFragmentTrialEndShown(true)
        _uiState.update { it.copy(showTrialEndedDialog = false) }
    }

    /**
     * Handle "Continue with basic" action from trial dialog.
     */
    fun continueWithBasic() {
        appPreferences.setDarkMode(0)
        dismissTrialDialog()
    }
}


