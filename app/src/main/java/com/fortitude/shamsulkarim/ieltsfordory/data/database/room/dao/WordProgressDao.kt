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
}


