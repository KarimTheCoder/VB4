package com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity

/**
 * Data Access Object for word progress operations.
 * Provides suspend functions for coroutine-based database access.
 */
@Dao
interface WordProgressDao {

    @Query("SELECT * FROM word_progress WHERE source = :source")
    suspend fun getBySource(source: String): List<WordProgressEntity>

    @Query("SELECT * FROM word_progress WHERE source = :source AND is_favorite = 1")
    suspend fun getFavorites(source: String): List<WordProgressEntity>

    @Query("SELECT * FROM word_progress WHERE source = :source AND is_learned = 1")
    suspend fun getLearned(source: String): List<WordProgressEntity>

    @Query("SELECT * FROM word_progress WHERE source = :source AND is_learned = 0")
    suspend fun getUnlearned(source: String): List<WordProgressEntity>

    @Query("SELECT * FROM word_progress WHERE source = :source AND word_id = :wordId LIMIT 1")
    suspend fun getBySourceAndWordId(source: String, wordId: Int): WordProgressEntity?

    @Query("UPDATE word_progress SET is_favorite = :isFavorite WHERE source = :source AND word_id = :wordId")
    suspend fun updateFavorite(source: String, wordId: Int, isFavorite: Boolean)

    @Query("UPDATE word_progress SET is_learned = :isLearned WHERE source = :source AND word_id = :wordId")
    suspend fun updateLearned(source: String, wordId: Int, isLearned: Boolean)

    @Query("UPDATE word_progress SET is_blacklisted = :isBlacklisted WHERE source = :source AND word_id = :wordId")
    suspend fun updateBlacklisted(source: String, wordId: Int, isBlacklisted: Boolean)

    @Query("UPDATE word_progress SET is_skipped = :isSkipped WHERE source = :source AND word_id = :wordId")
    suspend fun updateSkipped(source: String, wordId: Int, isSkipped: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WordProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(entities: List<WordProgressEntity>)

    @Query("SELECT COUNT(*) FROM word_progress WHERE source = :source")
    suspend fun getCount(source: String): Int

    @Query("SELECT COUNT(*) FROM word_progress WHERE source = :source AND is_learned = 1")
    suspend fun getLearnedCount(source: String): Int

    @Query("SELECT COUNT(*) FROM word_progress WHERE source = :source AND is_favorite = 1")
    suspend fun getFavoriteCount(source: String): Int

    @Query("DELETE FROM word_progress WHERE source = :source")
    suspend fun deleteBySource(source: String)

    // ===== NEW QUERIES for Word Selection Algorithm =====

    /**
     * Get words prioritized for a learning session.
     * Priority: mistaken words first, then by oldest last_seen_date (or never seen).
     * Excludes learned, skipped, and blacklisted words.
     */
    @Query("""
        SELECT * FROM word_progress 
        WHERE source = :source 
        AND is_learned = 0 
        AND is_skipped = 0 
        AND is_blacklisted = 0
        ORDER BY 
            mistake_count DESC,
            CASE WHEN last_seen_date IS NULL THEN 0 ELSE 1 END,
            last_seen_date ASC
        LIMIT :limit
    """)
    suspend fun getWordsForSession(source: String, limit: Int): List<WordProgressEntity>

    /**
     * Increment the mistake count for a word (user answered incorrectly).
     */
    @Query("UPDATE word_progress SET mistake_count = mistake_count + 1 WHERE source = :source AND word_id = :wordId")
    suspend fun incrementMistakeCount(source: String, wordId: Int)

    /**
     * Increment the correct count for a word (user answered correctly).
     */
    @Query("UPDATE word_progress SET correct_count = correct_count + 1 WHERE source = :source AND word_id = :wordId")
    suspend fun incrementCorrectCount(source: String, wordId: Int)

    /**
     * Update the familiarity score and last seen timestamp for a word.
     */
    @Query("""
        UPDATE word_progress 
        SET familiarity_score = :score, last_seen_date = :timestamp 
        WHERE source = :source AND word_id = :wordId
    """)
    suspend fun updateFamiliarity(source: String, wordId: Int, score: Float, timestamp: Long)

    /**
     * Update only the last seen timestamp for a word.
     */
    @Query("UPDATE word_progress SET last_seen_date = :timestamp WHERE source = :source AND word_id = :wordId")
    suspend fun updateLastSeen(source: String, wordId: Int, timestamp: Long)

    /**
     * Set the next review date for spaced repetition.
     */
    @Query("UPDATE word_progress SET next_review_date = :nextReviewDate WHERE source = :source AND word_id = :wordId")
    suspend fun updateNextReviewDate(source: String, wordId: Int, nextReviewDate: Long)

    /**
     * Get words that are due for review (next_review_date <= current time).
     */
    @Query("""
        SELECT * FROM word_progress 
        WHERE source = :source 
        AND is_learned = 1 
        AND next_review_date IS NOT NULL 
        AND next_review_date <= :currentTime
        ORDER BY next_review_date ASC
        LIMIT :limit
    """)
    suspend fun getWordsDueForReview(source: String, currentTime: Long, limit: Int): List<WordProgressEntity>

    /**
     * Get the most mistaken words for review/practice.
     */
    @Query("""
        SELECT * FROM word_progress 
        WHERE source = :source 
        AND mistake_count > 0
        ORDER BY mistake_count DESC
        LIMIT :limit
    """)
    suspend fun getMostMistakenWords(source: String, limit: Int): List<WordProgressEntity>
}


