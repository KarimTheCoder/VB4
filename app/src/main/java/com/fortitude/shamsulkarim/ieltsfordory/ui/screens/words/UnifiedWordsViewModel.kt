package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words

import android.media.MediaPlayer
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
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetVocabularyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class WordTab(val title: String) {
    ALL("All Words"),
    LEARNED("Learned"),
    FAVORITES("Favorites")
}

data class UnifiedWordsUiState(
    val selectedTab: WordTab = WordTab.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val loadingAudioForWord: String? = null,
    
    // Per-tab word lists
    val allWords: List<VocabularyWord> = emptyList(),
    val learnedWords: List<VocabularyWord> = emptyList(),
    val favoriteWords: List<VocabularyWord> = emptyList(),
    
    // Filtered lists based on search
    val filteredAllWords: List<VocabularyWord> = emptyList(),
    val filteredLearnedWords: List<VocabularyWord> = emptyList(),
    val filteredFavoriteWords: List<VocabularyWord> = emptyList(),
    
    // Practice FAB state
    val canStartLearnedPractice: Boolean = false,
    val canStartFavoritePractice: Boolean = false
) {
    val currentWords: List<VocabularyWord>
        get() = when (selectedTab) {
            WordTab.ALL -> filteredAllWords
            WordTab.LEARNED -> filteredLearnedWords
            WordTab.FAVORITES -> filteredFavoriteWords
        }
    
    val showPracticeFab: Boolean
        get() = when (selectedTab) {
            WordTab.ALL -> false
            WordTab.LEARNED -> filteredLearnedWords.isNotEmpty()
            WordTab.FAVORITES -> filteredFavoriteWords.isNotEmpty()
        }
    
    val canStartPractice: Boolean
        get() = when (selectedTab) {
            WordTab.ALL -> false
            WordTab.LEARNED -> canStartLearnedPractice
            WordTab.FAVORITES -> canStartFavoritePractice
        }
    
    val emptyStateMessage: String
        get() = when (selectedTab) {
            WordTab.ALL -> if (searchQuery.isNotEmpty()) 
                "No words found matching \"$searchQuery\"" 
                else "No words available"
            WordTab.LEARNED -> if (searchQuery.isNotEmpty())
                "No learned words matching \"$searchQuery\""
                else "You haven't learned any words yet"
            WordTab.FAVORITES -> if (searchQuery.isNotEmpty())
                "No favorites matching \"$searchQuery\""
                else "No favorite words yet.\nTap the heart icon on any word to add it here."
        }
}

class UnifiedWordsViewModel(
    private val getVocabularyUseCase: GetVocabularyUseCase,
    private val getLearnedWordsUseCase: GetLearnedWordsUseCase,
    private val getFavoriteWordsUseCase: GetFavoriteWordsUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val isTtsReadyUseCase: IsTtsReadyUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnifiedWordsUiState())
    val uiState: StateFlow<UnifiedWordsUiState> = _uiState.asStateFlow()
    
    private var mediaPlayer: MediaPlayer? = null

    init {
        loadAllData()
    }

    private fun loadAllData() {
        val levelName = getLevelName()
        
        val allWords = getVocabularyUseCase.execute(levelName)
        val learnedWords = getLearnedWordsUseCase.execute(levelName)
        val favoriteWords = getFavoriteWordsUseCase.execute()
        
        _uiState.update { current ->
            current.copy(
                allWords = allWords,
                learnedWords = learnedWords,
                favoriteWords = favoriteWords,
                filteredAllWords = allWords,
                filteredLearnedWords = learnedWords,
                filteredFavoriteWords = favoriteWords,
                isLoading = false,
                canStartLearnedPractice = learnedWords.size >= 5,
                canStartFavoritePractice = favoriteWords.size >= 5
            )
        }
    }
    
    private fun getLevelName(): String {
        val levelIndex = appPreferences.getPrevWordSelection()
        return when (levelIndex) {
            0 -> "beginner"
            1 -> "intermediate"
            else -> "advance"
        }
    }

    fun selectTab(tab: WordTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredAllWords = filterWords(current.allWords, query),
                filteredLearnedWords = filterWords(current.learnedWords, query),
                filteredFavoriteWords = filterWords(current.favoriteWords, query)
            )
        }
    }
    
    private fun filterWords(words: List<VocabularyWord>, query: String): List<VocabularyWord> {
        if (query.isBlank()) return words
        val lowerQuery = query.lowercase()
        return words.filter { word ->
            word.word.lowercase().contains(lowerQuery) ||
            word.translation.lowercase().contains(lowerQuery) ||
            (word.pronunciation?.lowercase()?.contains(lowerQuery) == true)
        }
    }

    fun toggleFavorite(word: VocabularyWord) {
        val newFavoriteState = !word.isFavorite
        updateFavoriteStatusUseCase.execute(word, newFavoriteState)
        
        _uiState.update { current ->
            val updateWord = { w: VocabularyWord ->
                if (w.word == word.word) w.copy(isFavorite = newFavoriteState) else w
            }
            
            val updatedAllWords = current.allWords.map(updateWord)
            val updatedLearnedWords = current.learnedWords.map(updateWord)
            val updatedFavoriteWords = if (newFavoriteState) {
                current.favoriteWords + word.copy(isFavorite = true)
            } else {
                current.favoriteWords.filter { it.word != word.word }
            }
            
            current.copy(
                allWords = updatedAllWords,
                learnedWords = updatedLearnedWords,
                favoriteWords = updatedFavoriteWords,
                filteredAllWords = filterWords(updatedAllWords, current.searchQuery),
                filteredLearnedWords = filterWords(updatedLearnedWords, current.searchQuery),
                filteredFavoriteWords = filterWords(updatedFavoriteWords, current.searchQuery),
                canStartFavoritePractice = updatedFavoriteWords.size >= 5
            )
        }
        
        // Update favorite count in preferences
        val currentCount = appPreferences.getFavoriteCountProfile()
        if (newFavoriteState) {
            appPreferences.setFavoriteCountProfile(currentCount + 1)
        } else if (currentCount > 0) {
            appPreferences.setFavoriteCountProfile(currentCount - 1)
        }
    }

    fun speakWord(word: VocabularyWord) {
        val wordName = word.word.lowercase()
        val useVoicePronunciation = appPreferences.getPronunState()
        val isConnected = isConnectedUseCase.execute()

        if (isConnected && useVoicePronunciation) {
            _uiState.update { it.copy(loadingAudioForWord = word.word) }
            
            downloadAudioUseCase.execute(wordName, object : AudioRepository.Callback {
                override fun onSuccess(data: AudioData) {
                    _uiState.update { it.copy(loadingAudioForWord = null) }
                    playAudio(data.localPath)
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
    
    private fun playAudio(path: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    private fun speakWithTts(word: String) {
        if (isTtsReadyUseCase.execute()) {
            speakTextUseCase.execute(word, true)
        }
    }

    fun startPractice() {
        when (_uiState.value.selectedTab) {
            WordTab.LEARNED -> {
                appPreferences.setPracticeMode("learned")
                appPreferences.setLevel(getLevelName())
            }
            WordTab.FAVORITES -> {
                appPreferences.setPracticeMode("favorite")
            }
            else -> { /* No practice for All Words tab */ }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
