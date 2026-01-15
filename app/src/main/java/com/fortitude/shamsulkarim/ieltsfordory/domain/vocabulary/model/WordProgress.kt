package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model

/**
 * Domain entity representing a user's progress on a vocabulary word.
 * This is source-agnostic and used in the domain/use case layer.
 */
data class WordProgress(
    val wordId: Int,
    val source: VocabularySource,
    val isFavorite: Boolean = false,
    val isLearned: Boolean = false,
    val isBlacklisted: Boolean = false,
    val isSkipped: Boolean = false,
    
    // ===== NEW FIELDS for Word Selection Algorithm =====
    
    /** How many times the user answered incorrectly for this word */
    val mistakeCount: Int = 0,
    
    /** How many times the user answered correctly for this word */
    val correctCount: Int = 0,
    
    /** Timestamp (millis) when this word was last shown to the user */
    val lastSeenDate: Long? = null,
    
    /** Timestamp (millis) when this word should be reviewed next (spaced repetition) */
    val nextReviewDate: Long? = null,
    
    /** Familiarity score from 0.0 (unknown) to 1.0 (mastered) */
    val familiarityScore: Float = 0f
) {
    companion object {
        /**
         * Creates a new WordProgress with default (unlearned, not favorited) state.
         */
        fun createDefault(wordId: Int, source: VocabularySource): WordProgress {
            return WordProgress(
                wordId = wordId,
                source = source,
                isFavorite = false,
                isLearned = false,
                isBlacklisted = false,
                isSkipped = false,
                mistakeCount = 0,
                correctCount = 0,
                lastSeenDate = null,
                nextReviewDate = null,
                familiarityScore = 0f
            )
        }
    }
}


