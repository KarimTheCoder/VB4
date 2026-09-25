package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.WordSelectionConfig
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.SelectSessionWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    private val userPreferencesRepository: UserPreferencesRepository,
    private val selectSessionWordsUseCase: SelectSessionWordsUseCase,
    private val learningRepository: LearningRepository,
    private val sessionWordsRepository: SessionWordsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeVM"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val currentWordsPerSessionState = userPreferencesRepository.wordsPerSessionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 5)
        
    private val currentRepetitionPerSessionState = userPreferencesRepository.repetitionPerSessionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 5)

    // Track words skipped in current session (reset when session starts)
    private val skippedWordIds = mutableListOf<Int>()
    
    // Store selected words for the session
    private var selectedWords = listOf<VocabularyWord>()
    
    // Track if we've loaded words at least once
    private var hasLoadedOnce = false

    // Track the last loaded words per session setting, repetitions, filters, and second language
    private var lastLoadedWordsPerSession = -1
    private var lastLoadedRepeatationPerSession = -1
    private var lastLoadedFilterState = ""
    private var lastLoadedSecondLang = ""

    private suspend fun getFilterState(): String {
        return "${userPreferencesRepository.getIsIeltsActive()}_${userPreferencesRepository.getIsToeflActive()}_${userPreferencesRepository.getIsSatActive()}_${userPreferencesRepository.getIsGreActive()}"
    }

    init {
        Log.d(TAG, "HomeViewModel initialized")
        loadProgress()  // Initial load
        markHomeVisited()

        viewModelScope.launch {
            sessionWordsRepository.refreshTrigger.collect {
                Log.i(TAG, "refreshTrigger received from SessionWordsRepository - resetting session")
                resetSession()
            }
        }
    }
    
    /**
     * Check if a refresh was requested (e.g., from ResultScreen after session completion or Settings change).
     * Call this from HomeScreen's ON_RESUME lifecycle event.
     */
    fun checkForRefresh() {
        viewModelScope.launch {
            val currentWordsPerSession = currentWordsPerSessionState.value
            val currentRepeatationPerSession = currentRepetitionPerSessionState.value
            val currentFilterState = getFilterState()
            val currentSecondLang = userPreferencesRepository.getSecondLanguage()
            val settingsChanged = (lastLoadedWordsPerSession != -1 && lastLoadedWordsPerSession != currentWordsPerSession) ||
                                  (lastLoadedRepeatationPerSession != -1 && lastLoadedRepeatationPerSession != currentRepeatationPerSession) ||
                                  (lastLoadedFilterState.isNotEmpty() && lastLoadedFilterState != currentFilterState) ||
                                  (lastLoadedSecondLang.isNotEmpty() && lastLoadedSecondLang != currentSecondLang)

            if (sessionWordsRepository.consumeRefreshRequest() || settingsChanged) {
                Log.i(TAG, "Refresh requested or settings changed - reloading words")
                resetSession()
            } else {
                Log.d(TAG, "No refresh needed, keeping current words")
            }
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

                val wordsPerSession = userPreferencesRepository.getWordsPerSession()
                val repeatationPerSession = userPreferencesRepository.getRepetitionPerSession()
                val secondLanguage = userPreferencesRepository.getSecondLanguage()
                lastLoadedWordsPerSession = wordsPerSession
                lastLoadedRepeatationPerSession = repeatationPerSession
                lastLoadedFilterState = getFilterState()
                lastLoadedSecondLang = secondLanguage

                val config = WordSelectionConfig(
                    level = level,
                    wordsPerSession = wordsPerSession,
                    skipWordIds = skippedWordIds.toList()
                )
                Log.d(TAG, "Created config: wordsPerSession=${config.wordsPerSession}, skipCount=${config.skipWordIds.size}")

                selectedWords = selectSessionWordsUseCase(config)
                Log.i(TAG, "Received ${selectedWords.size} words from algorithm")

                val wordItems = selectedWords.map { word -> word.toWordItem() }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        words = wordItems
                    )
                }
                Log.d(TAG, "Updated UI state with ${wordItems.size} word items")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading words: ${e.message}", e)
                e.printStackTrace()  // Also print full stack trace
                _uiState.update { 
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Handle skip button click - removes word from session and replaces with a new one.
     * Only the skipped word is replaced; other words stay in their positions.
     */

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
        var isReady = false
        // Need to run this synchronously if possible, or assume settings mismatch handled elsewhere.
        // Actually this is called when clicking "Start".
        // A better approach is to not do suspend here and rely on the last loaded state
        // because settings changes should trigger checkForRefresh.
        
        val currentWordsPerSession = currentWordsPerSessionState.value
        val currentRepeatationPerSession = currentRepetitionPerSessionState.value
        
        // Settings changed will be mostly detected by checkForRefresh.
        // For prepareSession, just checking word count is usually enough.
        if (selectedWords.isEmpty() || selectedWords.size != currentWordsPerSession) {
            Log.w(TAG, "Settings mismatch or no words selected - resetting session before preparing")
            resetSession()
            return false
        }
        
        sessionWordsRepository.setSessionWords(selectedWords)
        Log.i(TAG, "Session prepared with ${selectedWords.size} words")
        return true
    }

    /**
     * Start a fresh session with new words and invoke callback when ready.
     */
    fun startNewSession(onReady: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val wordsPerSession = userPreferencesRepository.getWordsPerSession()
            val repeatationPerSession = userPreferencesRepository.getRepetitionPerSession()
            lastLoadedWordsPerSession = wordsPerSession
            lastLoadedRepeatationPerSession = repeatationPerSession
            lastLoadedFilterState = getFilterState()
            val config = WordSelectionConfig(
                level = "beginner",
                wordsPerSession = wordsPerSession,
                skipWordIds = emptyList()
            )
            try {
                val newWords = selectSessionWordsUseCase(config)
                if (newWords.isNotEmpty()) {
                    selectedWords = newWords
                    sessionWordsRepository.setSessionWords(newWords)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            words = newWords.map { w -> w.toWordItem() }
                        )
                    }
                    onReady()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start new session", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Reset skipped words for a new session.
     */
    fun resetSession() {
        Log.d(TAG, "Resetting session, clearing ${skippedWordIds.size} skipped words")
        skippedWordIds.clear()
        sessionWordsRepository.clearSession()
        loadProgress()
    }

    private fun markHomeVisited() {
        viewModelScope.launch {
            if (!userPreferencesRepository.isHomeVisited()) {
                userPreferencesRepository.setHomeVisited(true)
                Log.d(TAG, "Marked home as visited")
            }
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

        // Progress is based on familiarity score
        val progress = familiarityScore.toFloat()

        return WordItem(
            id = id.toString(),
            text = word,
            progress = progress,
            progressColor = progressColor,
            type = sessionType ?: "New"
        )
    }
}
