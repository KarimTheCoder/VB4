package com.fortitude.shamsulkarim.ieltsfordory.domain.learning

import com.fortitude.shamsulkarim.ieltsfordory.data.sync.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData

/**
 * Repository interface for user learning progress, session history, and SRS metadata.
 * Uses non-blocking Kotlin coroutines.
 */
interface LearningRepository {
    suspend fun getFavLearnedState(userName: String): FavLearnedState
    suspend fun fetchSessionWords(level: String, wordsPerSession: Int): List<VocabularyWord>
    suspend fun getAllUnlearnedWords(level: String): List<VocabularyWord>
    suspend fun updateLearnedStatus(words: List<VocabularyWord>)
    suspend fun updateJustLearnedStatus(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int)
    suspend fun updateFavoriteStatus(word: VocabularyWord, newStatus: Boolean)
    suspend fun updateLearnedStatus(word: VocabularyWord, newStatus: Boolean)
    suspend fun getJustLearnedSessionData(level: String): JustLearnedSessionData

    // ===== Spaced Repetition & Word Selection Methods =====

    /**
     * Record a correct answer for a word (increments correct_count).
     */
    suspend fun recordCorrectAnswer(word: VocabularyWord)

    /**
     * Record a mistake for a word (increments mistake_count).
     */
    suspend fun recordMistake(word: VocabularyWord)

    /**
     * Update the familiarity score for a word (0.0 to 1.0).
     */
    suspend fun updateFamiliarityScore(word: VocabularyWord, score: Float)

    /**
     * Mark a word as skipped (user pressed Skip in HomeScreen).
     */
    suspend fun skipWord(word: VocabularyWord)

    /**
     * Update the last seen timestamp for a word.
     */
    suspend fun updateLastSeen(word: VocabularyWord)

    /**
     * Update the next review date for a word.
     */
    suspend fun updateNextReviewDate(word: VocabularyWord, nextReviewDate: Long)
}
