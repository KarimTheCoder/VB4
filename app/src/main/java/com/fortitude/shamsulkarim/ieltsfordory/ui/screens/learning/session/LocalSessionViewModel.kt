package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the learning session screen.
 * Manages the session state and word progression.
 */
class LocalSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        // Load sample session data
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                currentWordIndex = 0,
                totalWords = 5,
                currentWord = SessionWord(
                    id = "1",
                    word = "Get out",
                    status = LearningStatus.FAMILIARIZING,
                    familiarityProgress = 0.25f,
                    meaning = "It can simply mean to leave a place. For example, \"It's time to get out of here.\"",
                    examples = listOf(
                        "It's getting late, we should get out of here before the traffic gets worse.",
                        "Get out! You mean to tell me you met the President?",
                        "He managed to get out of the burning building just in time."
                    ),
                    isFavorite = false
                )
            )
        }
    }

    fun onNextWord() {
        _uiState.update { currentState ->
            val nextIndex = (currentState.currentWordIndex + 1) % currentState.totalWords
            currentState.copy(
                currentWordIndex = nextIndex
            )
        }
    }

    fun onToggleFavorite() {
        _uiState.update { currentState ->
            currentState.copy(
                currentWord = currentState.currentWord.copy(
                    isFavorite = !currentState.currentWord.isFavorite
                )
            )
        }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { currentState ->
            currentState.copy(userNotes = notes)
        }
    }

    fun onReportMistake() {
        // Handle report mistake action
    }
}
