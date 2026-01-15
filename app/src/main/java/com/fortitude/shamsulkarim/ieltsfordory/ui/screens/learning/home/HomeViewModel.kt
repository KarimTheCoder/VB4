package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.WordSelectionConfig
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.SelectSessionWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Progress bar colors based on familiarity
private val ProgressBlue = Color(0xFF4052B5)   // Normal progress
private val ProgressPink = Color(0xFFE91E63)   // Low familiarity / mistaken
private val ProgressGreen = Color(0xFF4CAF50)  // High familiarity / mastered

/**
 * ViewModel for the Home screen.
 * Uses the word selection algorithm to fetch and display words for the learning session.
 */
class HomeViewModel(
    private val appPreferences: AppPreferences,
    private val selectSessionWordsUseCase: SelectSessionWordsUseCase,
    private val learningRepository: LearningRepository,
    private val sessionWordsRepository: SessionWordsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeVM"
        private const val WORDS_PER_SESSION = 5
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Track words skipped in current session (reset when session starts)
    private val skippedWordIds = mutableListOf<Int>()
    
    // Store selected words for the session
    private var selectedWords = listOf<VocabularyWord>()
    
    // Track if we've loaded words at least once
    private var hasLoadedOnce = false

    init {
        Log.d(TAG, "HomeViewModel initialized")
        loadProgress()  // Initial load
        markHomeVisited()
    }
    
    /**
     * Check if a refresh was requested (e.g., from ResultScreen after session completion).
     * Call this from HomeScreen's ON_RESUME lifecycle event.
     */
    fun checkForRefresh() {
        if (sessionWordsRepository.consumeRefreshRequest()) {
            Log.i(TAG, "Refresh requested - reloading words")
            resetSession()
        } else {
            Log.d(TAG, "No refresh needed, keeping current words")
        }
    }

    /**
     * Load words for the learning session using the selection algorithm.
     */
    fun loadProgress() {
        Log.d(TAG, "loadProgress() called, skipped words: ${skippedWordIds.size}")

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, words = emptyList()) }
                Log.d(TAG, "Set loading state to true")

                val level = "beginner" // todo: there won't be any levels
                Log.d(TAG, "Selected level: $level")

                val config = WordSelectionConfig(
                    level = level,
                    wordsPerSession = WORDS_PER_SESSION,
                    skipWordIds = skippedWordIds.toList()
                )
                Log.d(TAG, "Created config: wordsPerSession=${config.wordsPerSession}, skipCount=${config.skipWordIds.size}")

                selectedWords = selectSessionWordsUseCase(config)
                Log.i(TAG, "Received ${selectedWords.size} words from algorithm")

                val wordItems = selectedWords.map { word -> word.toWordItem() }
                
                val bannerText = when {
                    selectedWords.isEmpty() -> "No more words to learn at this level!"
                    selectedWords.size < WORDS_PER_SESSION -> "You will learn ${selectedWords.size} new words"
                    else -> "You will learn $WORDS_PER_SESSION new words, you can skip any words you already know"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        words = wordItems,
                        infoBannerText = bannerText
                    )
                }
                Log.d(TAG, "Updated UI state with ${wordItems.size} word items")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading words: ${e.message}", e)
                e.printStackTrace()  // Also print full stack trace
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        infoBannerText = "Error: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    /**
     * Handle skip button click - removes word from session and replaces with a new one.
     * Only the skipped word is replaced; other words stay in their positions.
     */
    fun onSkipWord(wordId: String) {
        val wordIdInt = wordId.toIntOrNull() ?: return
        Log.d(TAG, "onSkipWord() called for wordId: $wordIdInt")

        // Find the word to skip and its position
        val wordIndex = selectedWords.indexOfFirst { it.id == wordIdInt }
        if (wordIndex == -1) {
            Log.w(TAG, "Word with id $wordIdInt not found in selected words")
            return
        }

        val wordToSkip = selectedWords[wordIndex]
        skippedWordIds.add(wordIdInt)
        Log.d(TAG, "Added to skipped list, total skipped: ${skippedWordIds.size}")

        // Mark as skipped in database
        viewModelScope.launch {
            try {
                learningRepository.skipWord(wordToSkip)
                Log.i(TAG, "Word '${wordToSkip.word}' marked as skipped in database")

                // Fetch ONE replacement word
                val level = "beginner" // todo: there won't be any levels
                val allCurrentIds = selectedWords.map { it.id }
                
                val config = WordSelectionConfig(
                    level = level,
                    wordsPerSession = 1,  // Just get one replacement
                    skipWordIds = skippedWordIds + allCurrentIds  // Exclude all current + skipped
                )
                
                val replacementWords = selectSessionWordsUseCase(config)
                
                // Update the list: swap skipped word with replacement (or remove if no replacement)
                val newSelectedWords = selectedWords.toMutableList()
                if (replacementWords.isNotEmpty()) {
                    newSelectedWords[wordIndex] = replacementWords.first()
                    Log.i(TAG, "Replaced '${wordToSkip.word}' with '${replacementWords.first().word}'")
                } else {
                    newSelectedWords.removeAt(wordIndex)
                    Log.w(TAG, "No replacement found, removed '${wordToSkip.word}'")
                }
                selectedWords = newSelectedWords

                // Update UI
                _uiState.update {
                    it.copy(words = selectedWords.map { word -> word.toWordItem() })
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to skip word $wordIdInt", e)
            }
        }
    }

    /**
     * Get the list of selected vocabulary words for starting a session.
     */
    fun getSelectedWords(): List<VocabularyWord> = selectedWords

    /**
     * Prepare the session by storing selected words in the repository.
     * Call this BEFORE navigating to SessionScreen.
     * Returns true if session is ready (has words), false otherwise.
     */
    fun prepareSession(): Boolean {
        if (selectedWords.isEmpty()) {
            Log.w(TAG, "Cannot prepare session: no words selected")
            return false
        }
        
        sessionWordsRepository.setSessionWords(selectedWords)
        Log.i(TAG, "Session prepared with ${selectedWords.size} words")
        return true
    }

    /**
     * Reset skipped words for a new session.
     */
    fun resetSession() {
        Log.d(TAG, "Resetting session, clearing ${skippedWordIds.size} skipped words")
        skippedWordIds.clear()
        loadProgress()
    }

    private fun markHomeVisited() {
        if (!appPreferences.isHomeVisited()) {
            appPreferences.setHomeVisited(true)
            Log.d(TAG, "Marked home as visited")
        }
    }

    /**
     * Convert VocabularyWord to WordItem for UI display.
     */
    private fun VocabularyWord.toWordItem(): WordItem {
        // Determine progress color based on learning state
        val progressColor = when {
            isLearned -> ProgressGreen
            // TODO: Check familiarity score when available from progress
            else -> ProgressBlue
        }

        // Progress is 0 for new words, could be based on familiarity later
        val progress = if (isLearned) 1.0f else 0.0f

        return WordItem(
            id = id.toString(),
            text = word,
            progress = progress,
            progressColor = progressColor
        )
    }
}
