package com.fortitude.shamsulkarim.ieltsfordory.data_old

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.FetchSessionWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetAllUnlearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateJustLearnedStatusUseCase
import org.koin.java.KoinJavaComponent.get
import java.util.Calendar

/**
 * ViewModel for the NewTrain training screen.
 * Manages the training session state and business logic.
 * 
 * Uses StateFlow with immutable state updates for thread-safe reactive UI.
 */
class TrainViewModel(application: Application) : AndroidViewModel(application) {

    private val fetchSessionWordsUseCase: FetchSessionWordsUseCase = get(FetchSessionWordsUseCase::class.java)
    private val getAllUnlearnedWordsUseCase: GetAllUnlearnedWordsUseCase = get(GetAllUnlearnedWordsUseCase::class.java)
    private val updateLearnedStatusUseCase: UpdateLearnedStatusUseCase = get(UpdateLearnedStatusUseCase::class.java)
    private val updateJustLearnedStatusUseCase: UpdateJustLearnedStatusUseCase = get(UpdateJustLearnedStatusUseCase::class.java)
    
    private val sp = application.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE)

    // Question words pool for generating quiz answers
    private var questionWords: MutableList<VocabularyWord> = mutableListOf()
    
    // Level loaded from preferences
    private var level: String = ""
    
    // Timer reference
    private var countdownTimer: CountDownTimer? = null

    // StateFlow for UI State - single source of truth
    private val _uiState = MutableStateFlow(TrainUiState())
    val uiState: StateFlow<TrainUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        level = sp.getString("level", "NOTHING") ?: "NOTHING"
        val languageId = sp.getInt("language", 0)
        val soundState = sp.getBoolean("soundState", true)
        val secondLanguage = sp.getString("secondlanguage", "english") ?: "english"

        var totalMistakeCount = 0
        var totalCorrects = 0
        
        if (sp.contains("totalWrongCount$level")) {
            totalMistakeCount = sp.getInt("totalWrongCount$level", 0)
            totalCorrects = sp.getInt("totalCorrects", 0)
        } else {
            sp.edit().putInt("totalWrongCount$level", 0).apply()
        }

        val wordsPerSession = sp.getInt("wordsPerSession", 5)
        val repeatPerSession = sp.getInt("repeatationPerSession", 5)

        // Increment noshowads counter
        val noshowads = sp.getInt("noshowads", 0) + 1
        sp.edit().putInt("noshowads", noshowads).apply()
        
        // Initialize state with preferences
        _uiState.update { current ->
            current.copy(
                languageId = languageId,
                soundEnabled = soundState,
                secondLanguage = secondLanguage,
                totalMistakeCount = totalMistakeCount,
                totalCorrects = totalCorrects,
                wordsPerSession = wordsPerSession,
                repeatPerSession = repeatPerSession
            )
        }
    }

    fun initializingWords() {
        if (!sp.contains(level)) {
            sp.edit().putInt(level, 0).apply()
        }

        // Fetch words from repository
        val sessionWords = fetchSessionWordsUseCase.execute(level, _uiState.value.wordsPerSession)

        val actualWordsPerSession = minOf(sessionWords.size, _uiState.value.wordsPerSession)
        
        sp.edit().putInt("fiveWordSize", sessionWords.size).apply()

        // Initialize mistake collector as a map
        val mistakeCollector = sessionWords.indices.associateWith { 0 }
        
        _uiState.update { current ->
            current.copy(
                fiveWords = sessionWords.toList(),
                fiveWordsCopy = emptyList(), // Will be populated when quiz starts
                fiveWordSize = sessionWords.size,
                wordsPerSession = actualWordsPerSession,
                mistakeCollector = mistakeCollector,
                currentWord = sessionWords.firstOrNull(),
                phase = TrainPhase.Learning,
                isLoading = false
            )
        }
    }

    private fun loadQuestionWords() {
        if (questionWords.isEmpty()) {
            questionWords = getAllUnlearnedWordsUseCase.execute(level).toMutableList()
        }
    }

    /**
     * Generates answer options for the current quiz question.
     * Returns 4 options including the correct answer.
     */
    fun generateAnswers(): List<VocabularyWord> {
        val state = _uiState.value
        val quizCycle = state.quizCycle
        
        if (quizCycle >= state.fiveWords.size) return emptyList()
        
        loadQuestionWords()
        
        val correctWord = state.fiveWords[quizCycle]
        val answers = mutableListOf<VocabularyWord>()
        
        questionWords.shuffle()
        answers.addAll(questionWords.take(4))
        
        // Ensure correct answer is included
        if (!answers.contains(correctWord)) {
            answers[0] = correctWord
        }
        
        answers.shuffle()
        
        _uiState.update { it.copy(answers = answers) }
        
        return answers
    }

    /**
     * Checks if the selected answer is correct and updates state accordingly.
     */
    fun checkAnswer(selectedIndex: Int): QuizResult {
        val state = _uiState.value
        val correctAnswer = state.correctAnswer
        
        val selectedAnswer = if (state.languageId == 0) {
            state.answers.getOrNull(selectedIndex)?.translation ?: ""
        } else {
            state.answers.getOrNull(selectedIndex)?.translationSecondLang ?: ""
        }
        
        val isCorrect = selectedAnswer.equals(correctAnswer, ignoreCase = true)
        
        if (isCorrect) {
            onCorrectAnswer()
        } else {
            onWrongAnswer()
        }
        
        return QuizResult(
            isCorrect = isCorrect,
            selectedIndex = selectedIndex,
            correctAnswer = correctAnswer
        )
    }

    private fun onCorrectAnswer() {
        val wasSessionComplete: Boolean
        
        _uiState.update { current ->
            val newQuizCycle = (current.quizCycle + 1) % current.fiveWordSize
            val newTotalCycle = current.totalCycle + 1
            val newWord = current.fiveWords.getOrNull(newQuizCycle)
            
            current.copy(
                quizCycle = newQuizCycle,
                totalCycle = newTotalCycle,
                totalCorrects = current.totalCorrects + 1,
                colorScheme = TrainColorScheme.CORRECT,
                isWrongAnswer = false,
                currentWord = newWord
            )
        }
        
        // Check if session is complete AFTER state update
        wasSessionComplete = _uiState.value.isSessionComplete
        if (wasSessionComplete) {
            onSessionComplete()
        } else {
            // Generate answers for the NEW word (after state was updated)
            generateAnswers()
        }
    }

    private fun onWrongAnswer() {
        _uiState.update { current ->
            val newMistakeCollector = current.mistakeCollector.toMutableMap()
            newMistakeCollector[current.quizCycle] = (newMistakeCollector[current.quizCycle] ?: 0) + 1
            
            current.copy(
                mistakes = current.mistakes + 1,
                totalMistakeCount = current.totalMistakeCount + 1,
                lastMistake = current.quizCycle,
                mistakeCollector = newMistakeCollector,
                colorScheme = TrainColorScheme.WRONG,
                isWrongAnswer = true,
                // Stay in Quiz phase, just show wrong feedback
                // The UI will show the correct answer and user can try again
                isShowingWordReview = true
            )
        }
    }
    
    /**
     * Called after user sees the word review (after wrong answer).
     * Returns to quiz with new answers for the same word.
     */
    fun continueAfterReview() {
        _uiState.update { current ->
            current.copy(
                isShowingWordReview = false,
                colorScheme = TrainColorScheme.DEFAULT
            )
        }
        // Regenerate answers for retry
        generateAnswers()
    }

    private fun onSessionComplete() {
        val state = _uiState.value
        val pos = getMostMistakenWordIndex()
        
        // Update databases
        updateLearnedStatusUseCase.execute(state.fiveWords)
        updateJustLearnedStatusUseCase.execute(level, state.fiveWords, pos)
        
        // Save progress
        saveProgress()
        saveMostMistakenWord(pos)
        
        _uiState.update { it.copy(phase = TrainPhase.Finished) }
    }

    private fun getMostMistakenWordIndex(): Int {
        val mistakeCollector = _uiState.value.mistakeCollector
        var maxMistakes = 0
        var pos = -1
        
        mistakeCollector.forEach { (index, count) ->
            if (count > maxMistakes) {
                pos = index
                maxMistakes = count
            }
        }
        return pos
    }

    private fun saveProgress() {
        val state = _uiState.value
        sp.edit().apply {
            putInt("NTmistakes", state.mistakes)
            putInt("totalWrongCount$level", state.totalMistakeCount)
            putInt("totalCorrects", state.totalCorrects)
            apply()
        }
    }

    private fun saveMostMistakenWord(pos: Int) {
        val state = _uiState.value
        if (pos != -1 && pos < state.fiveWords.size) {
            val word = state.fiveWords[pos]
            val pronunciation = word.pronunciation ?: ""
            val def = word.translation
            val spanish = word.translationSecondLang ?: ""
            val example = word.example2 ?: ""
            sp.edit().putString("MostMistakenWord", "shit+$pronunciation+$def+$spanish+$example").apply()
        } else {
            sp.edit().putString("MostMistakenWord", "no").apply()
        }
    }

    /**
     * Advances to the next word in learning phase.
     */
    fun advanceToNextWord() {
        _uiState.update { current ->
            val newShowCycle = current.showCycle + 1
            val newWord = current.fiveWords.getOrNull(newShowCycle)
            
            current.copy(
                showCycle = newShowCycle,
                currentWord = newWord,
                isSpeakerVisible = true,
                colorScheme = TrainColorScheme.DEFAULT
            )
        }
    }

    /**
     * Called when learning phase is complete, shows vocabulary selection.
     */
    fun showVocabularySelection() {
        _uiState.update { current ->
            current.copy(
                phase = TrainPhase.VocabularySelection,
                fiveWordsCopy = current.fiveWords,
                isVocabularySelectionShown = true
            )
        }
    }

    /**
     * User selected which words to quiz on.
     */
    fun onVocabularySelected(selectedIndices: List<Int>) {
        _uiState.update { current ->
            val selectedWords = if (selectedIndices.isEmpty()) {
                current.fiveWords // Use all if none selected
            } else {
                selectedIndices.mapNotNull { current.fiveWords.getOrNull(it) }
            }
            
            val newMistakeCollector = selectedWords.indices.associateWith { 0 }
            
            current.copy(
                fiveWords = selectedWords,
                fiveWordSize = selectedWords.size,
                mistakeCollector = newMistakeCollector,
                phase = TrainPhase.Quiz(0),
                quizCycle = 0,
                currentWord = selectedWords.firstOrNull()
            )
        }
        
        // Generate first set of answers
        generateAnswers()
    }

    /**
     * Skips vocabulary selection and marks all as learned.
     */
    fun skipVocabularySelection() {
        val state = _uiState.value
        updateJustLearnedStatusUseCase.execute(level, state.fiveWords, -1)
        updateLearnedStatusUseCase.execute(state.fiveWords)
        
        _uiState.update { it.copy(phase = TrainPhase.Finished) }
    }

    fun onNextClicked() {
        val current = _uiState.value

        _uiState.update { it.copy(isSpeakerVisible = false) }

        if (!current.isTimerRunning) {
            startTimer()
        }

        if (current.alreadyClicked) {
            _uiState.update { 
                it.copy(
                    shouldTriggerNextWord = true,
                    alreadyClicked = false,
                    isSpeakerVisible = false
                )
            }
        }
    }

    fun onNextWordTriggered() {
        _uiState.update { it.copy(shouldTriggerNextWord = false) }
    }

    fun resetColorScheme() {
        _uiState.update { it.copy(colorScheme = TrainColorScheme.DEFAULT) }
    }

    private fun startTimer() {
        countdownTimer?.cancel()
        
        _uiState.update { it.copy(isTimerRunning = true, progressCount = 0) }

        countdownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { it.copy(progressCount = it.progressCount + 1) }
            }

            override fun onFinish() {
                _uiState.update { 
                    it.copy(
                        progressCount = 0,
                        alreadyClicked = true,
                        isTimerRunning = false,
                        isSpeakerVisible = true
                    )
                }
            }
        }.start()
    }

    // Helper methods for backward compatibility with Activity
    fun getUserName(): String = sp.getString("userName", "Boo") ?: "Boo"
    
    fun checkTrialStatus(): String {
        if (sp.contains("trial_end_date")) {
            val endMillis = sp.getLong("trial_end_date", 0)
            val todayMillis = Calendar.getInstance().time.time
            return if (endMillis - todayMillis >= 0) "active" else "ended"
        }
        return "ended"
    }

    fun getIsAdShow(): Boolean {
        return !sp.contains("premium") && checkTrialStatus().equals("ended", ignoreCase = true)
    }

    override fun onCleared() {
        super.onCleared()
        countdownTimer?.cancel()
    }
}


