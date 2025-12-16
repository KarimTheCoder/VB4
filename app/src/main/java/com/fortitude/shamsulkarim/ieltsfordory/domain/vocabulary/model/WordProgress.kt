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
    val isSkipped: Boolean = false
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
                isSkipped = false
            )
        }
    }
}


