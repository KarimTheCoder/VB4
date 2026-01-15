package com.fortitude.shamsulkarim.ieltsfordory.domain.learning

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * Repository for storing and retrieving quiz/session results between screens.
 * 
 * Flow: SessionScreen stores results → ResultScreen reads them
 * 
 * This is a singleton that holds the results of the current learning session.
 */
class SessionResultRepository {
    
    private var _result: SessionResult? = null
    
    /**
     * Store the session result after quiz completion.
     * Called by SessionViewModel when user finishes the quiz.
     */
    fun setResult(result: SessionResult) {
        _result = result
    }
    
    /**
     * Get the session result.
     * Called by ResultViewModel to display results.
     */
    fun getResult(): SessionResult? = _result
    
    /**
     * Check if there is a result available.
     */
    fun hasResult(): Boolean = _result != null
    
    /**
     * Clear the result (call when starting a new session).
     */
    fun clearResult() {
        _result = null
    }
}

/**
 * Represents the complete result of a learning session.
 */
data class SessionResult(
    val wordResults: List<WordQuizResult>,
    val totalCorrect: Int,
    val totalMistakes: Int,
    val grade: String,
    val gradeProgress: Float,  // 0.0 to 1.0 for circular progress
    val congratsTitle: String,
    val congratsMessage: String
) {
    companion object {
        /**
         * Calculate grade based on accuracy.
         */
        fun calculateGrade(correct: Int, total: Int): Pair<String, Float> {
            if (total == 0) return Pair("N/A", 0f)
            
            val accuracy = correct.toFloat() / total
            val grade = when {
                accuracy >= 0.95 -> "A+"
                accuracy >= 0.90 -> "A"
                accuracy >= 0.85 -> "A-"
                accuracy >= 0.80 -> "B+"
                accuracy >= 0.75 -> "B"
                accuracy >= 0.70 -> "B-"
                accuracy >= 0.65 -> "C+"
                accuracy >= 0.60 -> "C"
                accuracy >= 0.55 -> "C-"
                accuracy >= 0.50 -> "D"
                else -> "F"
            }
            return Pair(grade, accuracy)
        }
        
        /**
         * Get congratulations message based on accuracy.
         */
        fun getCongratsMessage(accuracy: Float): Pair<String, String> {
            return when {
                accuracy >= 0.90 -> Pair("Excellent!", "Outstanding performance! You've mastered these words.")
                accuracy >= 0.75 -> Pair("Well Done!", "Great job! Keep practicing to perfect your skills.")
                accuracy >= 0.60 -> Pair("Good Effort!", "You're making progress. Focus on the mistaken words.")
                accuracy >= 0.40 -> Pair("Keep Trying!", "Practice makes perfect. Review the words you missed.")
                else -> Pair("Don't Give Up!", "Learning takes time. Try reviewing the words again.")
            }
        }
    }
}

/**
 * Result for a single word in the quiz.
 */
data class WordQuizResult(
    val word: VocabularyWord,
    val wasCorrect: Boolean,
    val responseTimeMs: Long? = null,  // Optional: time taken to answer
    val attempts: Int = 1              // How many attempts before correct
)
