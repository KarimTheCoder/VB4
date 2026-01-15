package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionResult
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionResultRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.WordQuizResult
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.ProcessSessionResultsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the learning session screen.
 * Loads words from SessionWordsRepository and manages the learning/quiz flow.
 */
class SessionViewModel(
    private val sessionWordsRepository: SessionWordsRepository,
    private val sessionResultRepository: SessionResultRepository,
    private val learningRepository: LearningRepository,
    private val processSessionResultsUseCase: ProcessSessionResultsUseCase,
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    // ... existing companion object ...

    companion object {
        private const val TAG = "SessionVM"
        private const val REQUIRED_CORRECT = 1  // Required correct answers to master a word
    }

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    // Store vocabulary words for session
    private var vocabularyWords: List<VocabularyWord> = emptyList()
    
    // Track quiz results for each word
    private val quizResults = mutableMapOf<Int, Boolean>()  // wordId -> wasCorrect
    
    // SRS Quiz Queue: words that still need to be mastered
    private val wordQueue = mutableListOf<SessionWord>()
    
    // Map word ID to VocabularyWord for quiz generation
    private val wordIdToVocab = mutableMapOf<String, VocabularyWord>()

    init {
        loadSession()
    }

    private fun loadSession() {
        vocabularyWords = sessionWordsRepository.getSessionWords()
        
        if (vocabularyWords.isEmpty()) {
            Log.w(TAG, "No session words available!")
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = "No words available for this session"
                )
            }
            return
        }

        Log.i(TAG, "Loaded ${vocabularyWords.size} words for session")
        
        // Convert to SessionWords and build ID mapping
        val sessionWords = vocabularyWords.map { vocab ->
            val sessionWord = vocab.toSessionWord()
            wordIdToVocab[sessionWord.id] = vocab
            sessionWord
        }
        
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                words = sessionWords,
                currentWordIndex = 0,
                totalWords = sessionWords.size,
                currentWord = sessionWords.first(),
                phase = SessionPhase.LEARNING
            )
        }
    }

    /**
     * Move to the next word in the session.
     * In Learning phase: linear progression through words
     * In Quiz phase: advance through SRS queue
     */
    fun onNextWord() {
        val currentState = _uiState.value
        
        if (currentState.phase == SessionPhase.LEARNING) {
            // Learning phase: linear progression
            val currentIndex = currentState.currentWordIndex
            val words = currentState.words
            
            if (currentIndex < words.size - 1) {
                val nextIndex = currentIndex + 1
                _uiState.update { 
                    it.copy(
                        currentWordIndex = nextIndex,
                        currentWord = words[nextIndex]
                    )
                }
                Log.d(TAG, "Learning: Moved to word ${nextIndex + 1}/${words.size}")
            } else {
                // End of learning phase -> start quiz
                startQuizPhase()
            }
        } else if (currentState.phase == SessionPhase.QUIZZING) {
            // Quiz phase: advance through SRS queue
            advanceQuizQueue()
        }
    }
    
    /**
     * Advance to the next word in the SRS queue.
     */
    private fun advanceQuizQueue() {
        if (wordQueue.isEmpty()) {
            // All words mastered!
            completeSession()
            return
        }
        
        // Get next word from queue
        val nextWord = wordQueue.first()
        
        _uiState.update { 
            it.copy(
                currentWord = nextWord,
                remainingWords = wordQueue.size
            )
        }
        
        generateQuizForWord(nextWord)
        Log.d(TAG, "Quiz: Showing word '${nextWord.word}' (${nextWord.correctCount}/$REQUIRED_CORRECT)")
    }
    
    private fun startQuizPhase() {
        Log.i(TAG, "Starting Quiz Phase with SRS")
        
        // Initialize the SRS queue with shuffled words
        wordQueue.clear()
        wordQueue.addAll(_uiState.value.words.shuffled())
        
        val firstWord = wordQueue.firstOrNull() ?: return
        
        _uiState.update { 
            it.copy(
                phase = SessionPhase.QUIZZING,
                currentWordIndex = 0,
                currentWord = firstWord,
                remainingWords = wordQueue.size,
                masteredCount = 0,
                totalQuestions = 0,
                correctAnswers = 0,
                currentStreak = 0,
                bestStreak = 0
            )
        }
        generateQuizForWord(firstWord)
    }
    
    /**
     * Generate quiz for a specific SessionWord (SRS version).
     */
    private fun generateQuizForWord(sessionWord: SessionWord) {
        val vocabWord = wordIdToVocab[sessionWord.id] ?: return
        
        viewModelScope.launch {
            // Fetch distractors (exclude current word)
            val distractors = vocabularyRepository.getRandomWords(2, setOf(vocabWord.id))
            
            // Mix correct word with distractors
            val allOptions = (distractors + vocabWord).shuffled()
            val correctIndex = allOptions.indexOf(vocabWord)
            val optionTexts = allOptions.map { it.translation }
            
            _uiState.update { 
                it.copy(
                    quizOptions = optionTexts,
                    correctOptionIndex = correctIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false
                )
            }
            Log.d(TAG, "Quiz generated for '${sessionWord.word}' (${sessionWord.correctCount}/$REQUIRED_CORRECT)")
        }
    }
    
    private fun generateQuiz(index: Int) {
        val currentWord = vocabularyWords.getOrNull(index) ?: return
        
        viewModelScope.launch {
            // Fetch distractors
            // Exclude current word ID
            val distractors = vocabularyRepository.getRandomWords(2, setOf(currentWord.id))
            
            // Mix correct word with distractors
            val allOptions = (distractors + currentWord).shuffled()
            val correctIndex = allOptions.indexOf(currentWord)
            val optionTexts = allOptions.map { it.translation } // Use translation as answer
            
            _uiState.update { 
                it.copy(
                    quizOptions = optionTexts,
                    correctOptionIndex = correctIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false
                )
            }
            Log.d(TAG, "Quiz generated for '${currentWord.word}'")
        }
    }
    
    /**
     * Handle user selecting an option in the quiz.
     */
    fun onOptionSelected(index: Int) {
        if (_uiState.value.isAnswerRevealed) return // Prevent changing after reveal
        
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }
    
    /**
     * Check the answer and apply SRS mastery logic.
     * - Correct: increment correctCount, master if >= 3, else move to back
     * - Incorrect: reset correctCount to 0, move to back
     */
    fun checkAnswer() {
        val state = _uiState.value
        val selectedIndex = state.selectedOptionIndex
        
        if (selectedIndex == null || state.isAnswerRevealed) return
        
        val isCorrect = selectedIndex == state.correctOptionIndex
        val currentWord = state.currentWord
        
        // Reveal the answer first
        _uiState.update { it.copy(isAnswerRevealed = true) }
        
        // Update stats
        val newTotalQuestions = state.totalQuestions + 1
        val newCorrectAnswers = if (isCorrect) state.correctAnswers + 1 else state.correctAnswers
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newBestStreak = maxOf(state.bestStreak, newStreak)
        
        if (isCorrect) {
            handleCorrectAnswer(currentWord, newTotalQuestions, newCorrectAnswers, newStreak, newBestStreak)
        } else {
            handleIncorrectAnswer(currentWord, newTotalQuestions, newCorrectAnswers, newStreak, newBestStreak)
        }
    }
    
    /**
     * Handle correct answer: increment mastery progress.
     */
    private fun handleCorrectAnswer(
        currentWord: SessionWord,
        totalQuestions: Int,
        correctAnswers: Int,
        streak: Int,
        bestStreak: Int
    ) {
        val newCorrectCount = currentWord.correctCount + 1
        val isMastered = newCorrectCount >= REQUIRED_CORRECT
        
        // Update the word in queue
        val queueIndex = wordQueue.indexOfFirst { it.id == currentWord.id }
        if (queueIndex >= 0) {
            if (isMastered) {
                // Word mastered! Remove from queue
                wordQueue.removeAt(queueIndex)
                Log.i(TAG, "MASTERED: '${currentWord.word}' (${_uiState.value.masteredCount + 1}/${_uiState.value.totalWords})")
                
                // Record in database
                val vocabWord = wordIdToVocab[currentWord.id]
                if (vocabWord != null) {
                    quizResults[vocabWord.id] = true
                    viewModelScope.launch {
                        learningRepository.recordCorrectAnswer(vocabWord)
                    }
                }
            } else {
                // Update correctCount and move to back of queue
                val updatedWord = currentWord.copy(correctCount = newCorrectCount)
                wordQueue.removeAt(queueIndex)
                wordQueue.add(updatedWord)
                Log.d(TAG, "CORRECT: '${currentWord.word}' (${newCorrectCount}/$REQUIRED_CORRECT)")
            }
        }
        
        // Update UI state
        val newMasteredCount = if (isMastered) _uiState.value.masteredCount + 1 else _uiState.value.masteredCount
        _uiState.update { state ->
            state.copy(
                currentWord = currentWord.copy(correctCount = newCorrectCount, isMastered = isMastered),
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                currentStreak = streak,
                bestStreak = bestStreak,
                masteredCount = newMasteredCount,
                remainingWords = wordQueue.size
            )
        }
    }
    
    /**
     * Handle incorrect answer: reset mastery progress.
     */
    private fun handleIncorrectAnswer(
        currentWord: SessionWord,
        totalQuestions: Int,
        correctAnswers: Int,
        streak: Int,
        bestStreak: Int
    ) {
        // Reset correctCount and move to back of queue
        val queueIndex = wordQueue.indexOfFirst { it.id == currentWord.id }
        if (queueIndex >= 0) {
            val resetWord = currentWord.copy(correctCount = 0)
            wordQueue.removeAt(queueIndex)
            wordQueue.add(resetWord)
            Log.d(TAG, "MISTAKE: '${currentWord.word}' - reset to 0/$REQUIRED_CORRECT")
        }
        
        // Record in database
        val vocabWord = wordIdToVocab[currentWord.id]
        if (vocabWord != null) {
            quizResults[vocabWord.id] = false
            viewModelScope.launch {
                learningRepository.recordMistake(vocabWord)
            }
        }
        
        // Update UI state with reset correctCount
        _uiState.update { state ->
            state.copy(
                currentWord = currentWord.copy(correctCount = 0),
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                currentStreak = streak,
                bestStreak = bestStreak,
                remainingWords = wordQueue.size
            )
        }
    }

    /**
     * Record a correct answer for the current word (legacy method for learning phase).
     */
    fun recordCorrectAnswer() {
        val currentWord = vocabularyWords.getOrNull(_uiState.value.currentWordIndex) ?: return
        quizResults[currentWord.id] = true
        Log.d(TAG, "Recorded CORRECT for '${currentWord.word}'")
        
        // Update familiarity in database
        viewModelScope.launch {
            learningRepository.recordCorrectAnswer(currentWord)
        }
    }

    /**
     * Record a mistake for the current word (legacy method for learning phase).
     */
    fun recordMistake() {
        val currentWord = vocabularyWords.getOrNull(_uiState.value.currentWordIndex) ?: return
        quizResults[currentWord.id] = false
        Log.d(TAG, "Recorded MISTAKE for '${currentWord.word}'")
        
        // Update mistake count in database
        viewModelScope.launch {
            learningRepository.recordMistake(currentWord)
        }
    }

    /**
     * Toggle favorite status for the current word.
     */
    fun onToggleFavorite() {
        _uiState.update { currentState ->
            currentState.copy(
                currentWord = currentState.currentWord.copy(
                    isFavorite = !currentState.currentWord.isFavorite
                )
            )
        }
        
        // TODO: Persist favorite change to database
    }

    /**
     * Update user notes for the current word.
     */
    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(userNotes = notes) }
    }

    /**
     * Complete the session and store results for ResultScreen.
     */
    private fun completeSession() {
        Log.i(TAG, "Session complete! Preparing results...")
        
        // Build word results
        val wordResults = vocabularyWords.map { word ->
            WordQuizResult(
                word = word,
                wasCorrect = quizResults[word.id] ?: true  // Default to correct if not tracked
            )
        }
        
        val totalCorrect = wordResults.count { it.wasCorrect }
        val totalMistakes = wordResults.size - totalCorrect
        
        // Calculate grade
        val (grade, progress) = SessionResult.calculateGrade(totalCorrect, wordResults.size)
        val (title, message) = SessionResult.getCongratsMessage(progress)
        
        val result = SessionResult(
            wordResults = wordResults,
            totalCorrect = totalCorrect,
            totalMistakes = totalMistakes,
            grade = grade,
            gradeProgress = progress,
            congratsTitle = title,
            congratsMessage = message
        )
        
        // Store for ResultScreen
        sessionResultRepository.setResult(result)
        
        // Process results (update DB: familiarity, review dates)
        viewModelScope.launch {
            try {
                processSessionResultsUseCase(result)
                Log.d(TAG, "Session results processed and saved to DB")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing session results", e)
            }
        }
        
        // Update UI state
        _uiState.update { 
            it.copy(
                phase = SessionPhase.COMPLETE,
                isSessionComplete = true
            )
        }
        
        Log.i(TAG, "Results stored: $totalCorrect correct, $totalMistakes mistakes, Grade: $grade")
    }

    /**
     * Convert VocabularyWord to SessionWord for UI display.
     */
    private fun VocabularyWord.toSessionWord(): SessionWord {
        return SessionWord(
            id = id.toString(),
            word = word,
            status = LearningStatus.FAMILIARIZING,
            familiarityProgress = familiarityScore.toFloat(),
            meaning = translation,
            examples = listOfNotNull(
                example1?.takeIf { it.isNotEmpty() },
                example2?.takeIf { it.isNotEmpty() },
                example3?.takeIf { it.isNotEmpty() }
            ),
            isFavorite = isFavorite
        )
    }
}
