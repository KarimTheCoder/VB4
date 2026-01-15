package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.result

import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionResultRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.WordQuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Result screen.
 * Displays results from the completed learning session.
 */
class ResultViewModel(
    private val sessionResultRepository: SessionResultRepository,
    private val sessionWordsRepository: SessionWordsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        loadResults()
        // Request HomeScreen to refresh words when user navigates back
        sessionWordsRepository.requestHomeRefresh()
    }

    private fun loadResults() {
        val result = sessionResultRepository.getResult()
        
        if (result != null) {
            val wordResults = result.wordResults.map { it.toWordResult() }
            
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    grade = result.grade,
                    gradeProgress = result.gradeProgress,
                    congratsTitle = result.congratsTitle,
                    congratsMessage = result.congratsMessage,
                    wordResults = wordResults
                )
            }
        } else {
            // No result found (should typically not happen if flow is correct)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun WordQuizResult.toWordResult(): WordResult {
        return WordResult(
            id = word.id.toString(),
            word = word.word,
            meaning = word.translation,
            status = if (wasCorrect) WordResultStatus.CORRECT else WordResultStatus.MISTAKEN,
            statusLabel = if (!wasCorrect) "Needs practice" else null,
            isExpanded = !wasCorrect, // Auto-expand mistakes
            feedbackMessage = if (!wasCorrect) "You marked this as 'Need Practice'" else null,
            performanceIndicators = listOf(
                PerformanceIndicator(
                    type = IndicatorType.ACCURACY,
                    isPositive = wasCorrect
                )
            )
        )
    }

    fun onToggleWordExpanded(wordId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                wordResults = currentState.wordResults.map { result ->
                    if (result.id == wordId) {
                        result.copy(isExpanded = !result.isExpanded)
                    } else {
                        result
                    }
                }
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // Optional: clear result when leaving screen, though navigation logic usually handles flow
        // sessionResultRepository.clearResult()
    }
}
