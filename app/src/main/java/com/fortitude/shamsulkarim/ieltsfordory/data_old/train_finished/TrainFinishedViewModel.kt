package com.fortitude.shamsulkarim.ieltsfordory.data_old.train_finished

import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetJustLearnedSessionDataUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusSingleUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for TrainFinishedScreen
 */
data class TrainFinishedUiState(
    val isLoading: Boolean = true,
    val learnedWords: List<VocabularyWord> = emptyList(),
    val learnedWordsCount: Int = 0,
    val mostMistakenWord: VocabularyWord? = null,
    val isMostMistakenFavorite: Boolean = false,
    val isMostMistakenLearned: Boolean = true,
    val showMostMistakenCard: Boolean = false,
    val soundEnabled: Boolean = true,
    val rateAppUrl: String = ""
)

/**
 * ViewModel for TrainFinishedScreen handling training completion display.
 */
class TrainFinishedViewModel(
    private val getJustLearnedSessionDataUseCase: GetJustLearnedSessionDataUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val updateLearnedStatusSingleUseCase: UpdateLearnedStatusSingleUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainFinishedUiState())
    val uiState: StateFlow<TrainFinishedUiState> = _uiState.asStateFlow()

    init {
        loadSessionData()
    }

    /**
     * Load the just learned session data.
     */
    fun loadSessionData() {
        val level = appPreferences.getLevel() ?: "beginner"
        val soundEnabled = appPreferences.getSoundState()
        
        val sessionData = getJustLearnedSessionDataUseCase.execute(level)
        val learnedWords = sessionData.learnedWords
        val mostMistaken = sessionData.mostMistakenWord

        val rateAppUrl = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder"

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                learnedWords = learnedWords,
                learnedWordsCount = learnedWords.size,
                mostMistakenWord = mostMistaken,
                isMostMistakenFavorite = mostMistaken?.isFavorite ?: false,
                isMostMistakenLearned = mostMistaken?.isLearned ?: true,
                showMostMistakenCard = mostMistaken != null,
                soundEnabled = soundEnabled,
                rateAppUrl = rateAppUrl
            )
        }
    }

    /**
     * Toggle favorite status of most mistaken word.
     */
    fun toggleMostMistakenFavorite() {
        val mostMistaken = _uiState.value.mostMistakenWord ?: return
        val newFavoriteStatus = !_uiState.value.isMostMistakenFavorite
        
        updateFavoriteStatusUseCase.execute(mostMistaken, newFavoriteStatus)
        _uiState.update { it.copy(isMostMistakenFavorite = newFavoriteStatus) }
    }

    /**
     * Toggle learned status of most mistaken word.
     */
    fun toggleMostMistakenLearned() {
        val mostMistaken = _uiState.value.mostMistakenWord ?: return
        val newLearnedStatus = !_uiState.value.isMostMistakenLearned
        
        updateLearnedStatusSingleUseCase.execute(mostMistaken, newLearnedStatus)
        _uiState.update { it.copy(isMostMistakenLearned = newLearnedStatus) }
    }

    /**
     * Check if sound should be played.
     */
    fun shouldPlaySound(): Boolean = _uiState.value.soundEnabled
}


