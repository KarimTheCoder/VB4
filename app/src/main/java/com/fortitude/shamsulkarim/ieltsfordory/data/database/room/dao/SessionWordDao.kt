package com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.SessionWordEntity

/**
 * Data Access Object for session word operations.
 * Manages words during training/learning sessions.
 */
@Dao
interface SessionWordDao {

    @Query("SELECT * FROM session_words WHERE level = :level")
    suspend fun getByLevel(level: String): List<SessionWordEntity>

    @Query("SELECT * FROM session_words WHERE level = :level AND is_most_mistaken = 1 LIMIT 1")
    suspend fun getMostMistaken(level: String): SessionWordEntity?

    @Query("SELECT COUNT(*) FROM session_words WHERE level = :level")
    suspend fun getCountByLevel(level: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SessionWordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SessionWordEntity)

    @Query("DELETE FROM session_words WHERE level = :level")
    suspend fun clearByLevel(level: String)

    @Query("DELETE FROM session_words")
    suspend fun clearAll()

    @Query("UPDATE session_words SET is_most_mistaken = :isMostMistaken WHERE id = :id")
    suspend fun updateMostMistaken(id: Int, isMostMistaken: Boolean)

    @Query("UPDATE session_words SET is_learned = :isLearned WHERE id = :id")
    suspend fun updateLearned(id: Int, isLearned: Boolean)
}
