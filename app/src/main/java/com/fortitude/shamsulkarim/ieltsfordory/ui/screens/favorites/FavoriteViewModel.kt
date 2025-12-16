package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.favorites

import androidx.lifecycle.ViewModel
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.AudioData
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadAudioUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.IsTtsReadyUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.SpeakTextUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetFavoriteWordsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for FavoriteScreen
 */
data class FavoriteUiState(
    val words: List<VocabularyWord> = emptyList(),
    val filteredWords: List<VocabularyWord> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val loadingAudioForWord: String? = null,
    val canStartPractice: Boolean = false // true if >= 5 words
)

/**
 * ViewModel for FavoriteScreen handling favorite words list and practice navigation.
 */
class FavoriteViewModel(
    private val getFavoriteWordsUseCase: GetFavoriteWordsUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val isTtsReadyUseCase: IsTtsReadyUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteWords()
    }

    /**
     * Load all favorite words.
     */
    fun loadFavoriteWords() {
        val words = getFavoriteWordsUseCase.execute()
        val canPractice = words.size >= 5

        _uiState.update { current ->
            current.copy(
                words = words,
                filteredWords = if (current.searchQuery.isBlank()) words
                else words.filter { matchesSearch(it, current.searchQuery) },
                isLoading = false,
                canStartPractice = canPractice
            )
        }
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

    private fun matchesSearch(word: VocabularyWord, query: String): Boolean {
        val lowerQuery = query.lowercase()
        return word.word.lowercase().contains(lowerQuery) ||
                word.translation.lowercase().contains(lowerQuery) ||
                (word.pronunciation?.lowercase()?.contains(lowerQuery) == true)
    }

    /**
     * Remove word from favorites.
     */
    fun removeFromFavorites(word: VocabularyWord) {
        updateFavoriteStatusUseCase.execute(word, false)

        _uiState.update { current ->
            val updatedWords = current.words.filter { it.word != word.word }
            val updatedFiltered = current.filteredWords.filter { it.word != word.word }
            current.copy(
                words = updatedWords,
                filteredWords = updatedFiltered,
                canStartPractice = updatedWords.size >= 5
            )
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
            _uiState.update { it.copy(loadingAudioForWord = word.word) }

            downloadAudioUseCase.execute(wordName, object : AudioRepository.Callback {
                override fun onSuccess(data: AudioData) {
                    _uiState.update { it.copy(loadingAudioForWord = null) }
                    onAudioReady(data.localPath)
                }

                override fun onError(e: Exception) {
                    _uiState.update { it.copy(loadingAudioForWord = null) }
                    speakWithTts(wordName)
                }
            })
        } else {
            speakWithTts(wordName)
        }
    }

    private fun speakWithTts(word: String) {
        if (isTtsReadyUseCase.execute()) {
            speakTextUseCase.execute(word, true)
        }
    }

    /**
     * Set up practice mode for favorites.
     */
    fun startPractice() {
        appPreferences.setPracticeMode("favorite")
    }

    /**
     * Save scroll position for restoration.
     */
    fun saveScrollPosition(position: Int) {
        appPreferences.setFavoriteScrollPos(position)
    }

    /**
     * Get saved scroll position.
     */
    fun getSavedScrollPosition(): Int {
        return appPreferences.getFavoriteScrollPos()
    }
}


