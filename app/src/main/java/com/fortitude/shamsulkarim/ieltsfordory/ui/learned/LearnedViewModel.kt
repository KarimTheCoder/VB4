package com.fortitude.shamsulkarim.ieltsfordory.ui.learned

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
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedWordsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for LearnedScreen
 */
data class LearnedUiState(
    val words: List<VocabularyWord> = emptyList(),
    val filteredWords: List<VocabularyWord> = emptyList(),
    val selectedLevel: Int = 0, // 0=beginner, 1=intermediate, 2=advance
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val loadingAudioForWord: String? = null,
    val showPracticeFab: Boolean = false,
    val canStartPractice: Boolean = false // true if >= 5 words
)

/**
 * ViewModel for LearnedScreen handling learned words list and practice navigation.
 */
class LearnedViewModel(
    private val getLearnedWordsUseCase: GetLearnedWordsUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val isTtsReadyUseCase: IsTtsReadyUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnedUiState())
    val uiState: StateFlow<LearnedUiState> = _uiState.asStateFlow()

    init {
        val savedSelection = appPreferences.getPrevLearnedSelection()
        _uiState.update { it.copy(selectedLevel = savedSelection) }
        loadWordsForLevel(savedSelection)
    }

    /**
     * Load learned words for the specified level.
     */
    fun loadWordsForLevel(levelIndex: Int) {
        val levelName = when (levelIndex) {
            0 -> "beginner"
            1 -> "intermediate"
            else -> "advance"
        }

        val words = getLearnedWordsUseCase.execute(levelName)
        val canPractice = words.size >= 5

        _uiState.update { current ->
            current.copy(
                selectedLevel = levelIndex,
                words = words,
                filteredWords = if (current.searchQuery.isBlank()) words
                else words.filter { matchesSearch(it, current.searchQuery) },
                isLoading = false,
                showPracticeFab = words.isNotEmpty(),
                canStartPractice = canPractice
            )
        }

        appPreferences.setPrevLearnedSelection(levelIndex)
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
     * Toggle favorite status for a word.
     */
    fun toggleFavorite(word: VocabularyWord) {
        val newFavoriteState = !word.isFavorite
        updateFavoriteStatusUseCase.execute(word, newFavoriteState)

        _uiState.update { current ->
            val updatedWords = current.words.map {
                if (it.word == word.word) it.copy(isFavorite = newFavoriteState) else it
            }
            val updatedFiltered = current.filteredWords.map {
                if (it.word == word.word) it.copy(isFavorite = newFavoriteState) else it
            }
            current.copy(words = updatedWords, filteredWords = updatedFiltered)
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
     * Set up practice mode and return the current level name.
     */
    fun startPractice(levelIndex: Int): String {
        val levelName = when (levelIndex) {
            0 -> "beginner"
            1 -> "intermediate"
            else -> "advance"
        }
        appPreferences.setPracticeMode("learned")
        appPreferences.setLevel(levelName)
        return levelName
    }

    /**
     * Set level for navigation to PretrainActivity.
     */
    fun setLevelForTraining() {
        val levelName = when (_uiState.value.selectedLevel) {
            0 -> "beginner"
            1 -> "intermediate"
            else -> "advance"
        }
        appPreferences.setLevel(levelName)
    }
}
