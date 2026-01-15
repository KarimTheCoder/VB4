package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import android.util.Log
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionResult
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * UseCase to process session results and update word progress (familiarity & spaced repetition).
 */
class ProcessSessionResultsUseCase(
    private val learningRepository: LearningRepository
) {
    companion object {
        private const val TAG = "ProcessSessionResults"
        private const val DAY_IN_MS = 24 * 60 * 60 * 1000L
    }

    suspend operator fun invoke(sessionResult: SessionResult) {
        Log.i(TAG, "Processing session results for ${sessionResult.wordResults.size} words")
        
        sessionResult.wordResults.forEach { wordResult ->
            val word = wordResult.word
            val wasCorrect = wordResult.wasCorrect
            
            // 1. Calculate new familiarity score
            val currentScore = word.familiarityScore
            val newScore = if (wasCorrect) {
                (currentScore + 0.1).coerceAtMost(1.0)
            } else {
                (currentScore - 0.2).coerceAtLeast(0.0)
            }
            
            // 2. Update familiarity in DB
            if (newScore != currentScore) {
                learningRepository.updateFamiliarityScore(word, newScore.toFloat())
            }
            
            // 3. Calculate next review date based on NEW score
            // Spaced repetition: higher familiarity = longer interval
            val daysUntilReview = when {
                newScore >= 0.9 -> 30  // Mastered
                newScore >= 0.7 -> 14  // Familiar
                newScore >= 0.5 -> 7   // Learning
                newScore >= 0.3 -> 3   // New
                else -> 1              // Struggling (review tomorrow)
            }
            
            val nextReviewDate = System.currentTimeMillis() + (daysUntilReview * DAY_IN_MS)
            
            // 4. Update review date in DB
            learningRepository.updateNextReviewDate(word, nextReviewDate)
            
            // 5. If mastered (score >= 0.8), mark as learned
            if (newScore >= 0.8 && !word.isLearned) {
                learningRepository.updateLearnedStatus(word, true)
            }
        }
        
        Log.i(TAG, "Session processing complete")
    }
}
