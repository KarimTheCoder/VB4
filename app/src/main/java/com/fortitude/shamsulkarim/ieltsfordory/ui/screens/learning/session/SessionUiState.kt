package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

/**
 * UI state for the learning session screen.
 */
data class SessionUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentWordIndex: Int = 0,
    val totalWords: Int = 0,
    val currentWord: SessionWord = SessionWord(),
    val words: List<SessionWord> = emptyList(),
    val userNotes: String = "",
    val phase: SessionPhase = SessionPhase.LEARNING,
    val isSessionComplete: Boolean = false,
    
    // Quiz State
    val quizOptions: List<String> = emptyList(), // The 4 options to display
    val correctOptionIndex: Int = -1,            // Index of the correct answer
    val selectedOptionIndex: Int? = null,        // User's selected index
    val isAnswerRevealed: Boolean = false,       // True if feedback is shown
    
    // Session Stats (for SRS & Mastery Rounds)
    val totalQuestions: Int = 0,                 // Total questions answered
    val correctAnswers: Int = 0,                 // Total correct answers
    val currentStreak: Int = 0,                  // Current consecutive correct answers
    val bestStreak: Int = 0,                     // Best streak in this session
    val masteredCount: Int = 0,                  // Words fully mastered (3 correct)
    val remainingWords: Int = 0                  // Words still in quiz queue
)

/**
 * Phase of the learning session.
 */
enum class SessionPhase {
    LEARNING,   // User is reviewing/learning words
    QUIZZING,   // User is being quizzed on words
    COMPLETE    // Session is finished
}

/**
 * Represents a word being learned in the session.
 */
data class SessionWord(
    val id: String = "",
    val word: String = "",
    val status: LearningStatus = LearningStatus.FAMILIARIZING,
    val familiarityProgress: Float = 0f,
    val meaning: String = "",
    val examples: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    // Mastery tracking for quiz phase
    val correctCount: Int = 0,           // Current correct streak for this word
    val requiredCorrect: Int = 3,        // Required correct answers to master
    val isMastered: Boolean = false      // True when correctCount >= requiredCorrect
)

/**
 * Learning status for a word.
 */
enum class LearningStatus(val displayText: String) {
    FAMILIARIZING("Now familiarizing"),
    LEARNING("Learning"),
    REVIEWING("Reviewing"),
    MASTERED("Mastered")
}
