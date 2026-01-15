package com.fortitude.shamsulkarim.ieltsfordory.data_old

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * Represents the different phases of the training session.
 */
sealed class TrainPhase {
    object Learning : TrainPhase()
    object VocabularySelection : TrainPhase()
    data class Quiz(val questionIndex: Int = 0) : TrainPhase()
    object Finished : TrainPhase()
}

/**
 * Color scheme states for the training UI.
 */
enum class TrainColorScheme {
    DEFAULT,
    CORRECT,
    WRONG
}

/**
 * Comprehensive UI State for the training screen.
 * Encapsulates all mutable state that was previously scattered in ViewModel.
 * 
 * @property phase Current phase of the training session
 * @property currentWord The word currently being displayed
 * @property fiveWords List of words for this training session
 * @property fiveWordsCopy Backup copy of original words for quiz
 * @property answers Current quiz answer options (4 choices)
 * @property showCycle Current word index in the learning phase
 * @property quizCycle Current question index in the quiz phase
 * @property totalCycle Total number of quiz questions answered
 * @property mistakes Number of mistakes in current session
 * @property totalMistakeCount Cumulative mistake count
 * @property totalCorrects Cumulative correct answers
 * @property lastMistake Index of the last word that was answered incorrectly
 * @property mistakeCollector Tracks mistakes per word
 * @property wordsPerSession Number of words to learn per session
 * @property repeatPerSession Number of times to repeat quiz for each word
 * @property fiveWordSize Actual size of the current word list
 * @property isWrongAnswer Flag indicating if last answer was wrong
 * @property progressCount Timer countdown progress (0-5)
 * @property isTimerRunning Whether the countdown timer is active
 * @property alreadyClicked Whether the next button was already clicked
 * @property isVocabularySelectionShown Whether vocabulary selection dialog was shown
 * @property isSpeakerVisible Whether the speaker icon should be visible
 * @property shouldTriggerNextWord One-shot trigger to advance to next word
 * @property colorScheme Current color scheme (default, correct, wrong)
 * @property soundEnabled Whether sound effects are enabled
 * @property languageId Language ID for translations (0 = primary, 1 = secondary)
 * @property secondLanguage The secondary language name (e.g., "spanish")
 * @property isLoading Whether data is being loaded
 */
data class TrainUiState(
    // Phase & Current Display
    val phase: TrainPhase = TrainPhase.Learning,
    val currentWord: VocabularyWord? = null,
    val fiveWords: List<VocabularyWord> = emptyList(),
    val fiveWordsCopy: List<VocabularyWord> = emptyList(),
    val answers: List<VocabularyWord> = emptyList(),
    
    // Cycle Tracking
    val showCycle: Int = 0,
    val quizCycle: Int = 0,
    val totalCycle: Int = 0,
    val fiveWordSize: Int = 0,
    
    // Mistake Tracking
    val mistakes: Int = 0,
    val totalMistakeCount: Int = 0,
    val totalCorrects: Int = 0,
    val lastMistake: Int = -1,
    val mistakeCollector: Map<Int, Int> = emptyMap(),
    val isWrongAnswer: Boolean = true,
    
    // Session Settings
    val wordsPerSession: Int = 5,
    val repeatPerSession: Int = 5,
    
    // Timer State
    val progressCount: Int = 0,
    val isTimerRunning: Boolean = false,
    val alreadyClicked: Boolean = true,
    
    // UI Visibility State
    val isVocabularySelectionShown: Boolean = false,
    val isSpeakerVisible: Boolean = false,
    val shouldTriggerNextWord: Boolean = false,
    val isShowingWordReview: Boolean = false,
    val colorScheme: TrainColorScheme = TrainColorScheme.DEFAULT,
    
    // Preferences
    val soundEnabled: Boolean = true,
    val languageId: Int = 0,
    val secondLanguage: String = "english",
    
    // Loading State
    val isLoading: Boolean = false
) {
    /**
     * Calculates the total progress for the progress bar.
     */
    val totalProgress: Float
        get() = (showCycle + totalCycle).toFloat()
    
    /**
     * Calculates the max progress for the progress bar.
     */
    val maxProgress: Float
        get() = (fiveWordSize + (fiveWords.size * repeatPerSession)).toFloat()
    
    /**
     * Whether we're in the quiz phase.
     */
    val isQuizPhase: Boolean
        get() = phase is TrainPhase.Quiz
    
    /**
     * Whether the training session is complete.
     */
    val isSessionComplete: Boolean
        get() = totalCycle >= (fiveWordSize * repeatPerSession)
    
    /**
     * Gets the current word being quizzed/shown.
     */
    val currentQuizWord: VocabularyWord?
        get() = fiveWords.getOrNull(quizCycle)
    
    /**
     * Gets the correct answer for the current quiz question.
     */
    val correctAnswer: String
        get() = if (languageId == 0) {
            currentQuizWord?.translation ?: ""
        } else {
            currentQuizWord?.translationSecondLang ?: ""
        }
    
    /**
     * Returns if we should show Spanish (secondary language) formatting.
     */
    val showSecondaryLanguage: Boolean
        get() = secondLanguage.equals("spanish", ignoreCase = true)
}

/**
 * Represents a quiz result after the user selects an answer.
 */
data class QuizResult(
    val isCorrect: Boolean,
    val selectedIndex: Int,
    val correctAnswer: String
)


