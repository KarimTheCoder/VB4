package com.fortitude.shamsulkarim.ieltsfordory.data.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.SessionWordDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.SessionWordEntity
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity

/**
 * Main Room database for the Vocabulary Builder app.
 * Consolidates legacy SQLite databases into a single unified database.
 *
 * Tables:
 * - word_progress: User progress for all vocabulary sources (IELTS, TOEFL, SAT, GRE)
 * - session_words: Words in active training sessions
 */
@Database(
    entities = [
        WordProgressEntity::class,
        SessionWordEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun wordProgressDao(): WordProgressDao
    abstract fun sessionWordDao(): SessionWordDao

    companion object {
        private const val DATABASE_NAME = "vocabulary_db"

        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        /**
         * Gets the singleton database instance.
         * Uses double-checked locking for thread safety.
         */
        fun getInstance(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): VocabularyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                VocabularyDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // For development; replace with proper migration for production
                .build()
        }
    }
}
