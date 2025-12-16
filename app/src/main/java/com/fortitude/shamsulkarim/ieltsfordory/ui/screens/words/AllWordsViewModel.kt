package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.AudioData
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadAudioUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.IsTtsReadyUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.SpeakTextUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetVocabularyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for AllWordsScreen
 */
data class AllWordsUiState(
    val words: List<VocabularyWord> = emptyList(),
    val filteredWords: List<VocabularyWord> = emptyList(),
    val selectedLevel: Int = 0, // 0=beginner, 1=intermediate, 2=advance
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val loadingAudioForWord: String? = null // Word currently loading audio
)

/**
 * ViewModel for AllWordsScreen handling word list, filtering, and audio.
 */
class AllWordsViewModel(
    private val getVocabularyUseCase: GetVocabularyUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val isTtsReadyUseCase: IsTtsReadyUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllWordsUiState())
    val uiState: StateFlow<AllWordsUiState> = _uiState.asStateFlow()

    // Audio playback callback
    private var audioCallback: ((String) -> Unit)? = null

    init {
        val savedSelection = appPreferences.getPrevWordSelection()
        _uiState.update { it.copy(selectedLevel = savedSelection) }
        loadWordsForLevel(savedSelection)
    }

    /**
     * Load words for the specified level.
     */
    fun loadWordsForLevel(levelIndex: Int) {
        val levelName = when (levelIndex) {
            0 -> "beginner"
            1 -> "intermediate"
            else -> "advance"
        }

        val words = getVocabularyUseCase.execute(levelName)
        
        _uiState.update { current ->
            current.copy(
                selectedLevel = levelIndex,
                words = words,
                filteredWords = if (current.searchQuery.isBlank()) words 
                    else words.filter { matchesSearch(it, current.searchQuery) },
                isLoading = false
            )
        }

        appPreferences.setPrevWordSelection(levelIndex)
    }

    /**
     * Update search query and filter words.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            val filtered = if (query.isBlank()) {
                current.words
            } else {
                current.words.filter { matchesSearch(it, query) }
            }
            current.copy(
                searchQuery = query,
                filteredWords = filtered
            )
        }
    }

    /**
     * Check if word matches search query.
     */
    private fun matchesSearch(word: VocabularyWord, query: String): Boolean {
        val lowerQuery = query.lowercase()
        return word.word.lowercase().contains(lowerQuery) ||
            word.translation.lowercase().contains(lowerQuery) ||
            (word.pronunciation?.lowercase()?.contains(lowerQuery) == true)
    }

    /**
     * Toggle favorite status for a word.
     */
    fun toggleFavorite(word: VocabularyWord) {
        val newFavoriteState = !word.isFavorite
        updateFavoriteStatusUseCase.execute(word, newFavoriteState)
        
        // Update local state
        _uiState.update { current ->
            val updatedWords = current.words.map { 
                if (it.word == word.word) it.copy(isFavorite = newFavoriteState) else it 
            }
            val updatedFiltered = current.filteredWords.map { 
                if (it.word == word.word) it.copy(isFavorite = newFavoriteState) else it 
            }
            current.copy(words = updatedWords, filteredWords = updatedFiltered)
        }

        // Update favorite count in preferences
        val currentCount = appPreferences.getFavoriteCountProfile()
        if (newFavoriteState) {
            appPreferences.setFavoriteCountProfile(currentCount + 1)
        } else if (currentCount > 0) {
            appPreferences.setFavoriteCountProfile(currentCount - 1)
        }
    }

    /**
     * Speak the word using TTS or download audio.
     */
    fun speakWord(word: VocabularyWord, onAudioReady: (String) -> Unit) {
        val wordName = word.word.lowercase()
        
        val useVoicePronunciation = appPreferences.getPronunState()
        val isConnected = isConnectedUseCase.execute()

        if (isConnected && useVoicePronunciation) {
            // Download and play audio
            _uiState.update { it.copy(loadingAudioForWord = word.word) }
            
            downloadAudioUseCase.execute(wordName, object : AudioRepository.Callback {
                override fun onSuccess(data: AudioData) {
                    _uiState.update { it.copy(loadingAudioForWord = null) }
                    onAudioReady(data.localPath)
                }

                override fun onError(e: Exception) {
                    _uiState.update { it.copy(loadingAudioForWord = null) }
                    // Fallback to TTS
                    speakWithTts(wordName)
                }
            })
        } else {
            // Use TTS directly
            speakWithTts(wordName)
        }
    }

    private fun speakWithTts(word: String) {
        if (isTtsReadyUseCase.execute()) {
            speakTextUseCase.execute(word, true)
        }
    }

    /**
     * Save scroll position for restoration.
     */
    fun saveScrollPosition(position: Int, offset: Int) {
        appPreferences.setWordFirstVisiblePos(position)
        appPreferences.setWordFirstOffsetTop(offset)
    }

    /**
     * Get saved scroll position.
     */
    fun getSavedScrollPosition(): Pair<Int, Int> {
        return Pair(
            appPreferences.getWordFirstVisiblePos(),
            appPreferences.getWordFirstOffsetTop()
        )
    }
}


