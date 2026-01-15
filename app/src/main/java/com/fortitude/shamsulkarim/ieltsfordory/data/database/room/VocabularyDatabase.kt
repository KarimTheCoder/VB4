package com.fortitude.shamsulkarim.ieltsfordory.data.database.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
 * 
 * Migration Strategy:
 * - Version 1: Initial Room database (migrated from legacy SQLite)
 * - Version 2: Added word selection algorithm fields (mistake_count, correct_count, etc.)
 */
@Database(
    entities = [
        WordProgressEntity::class,
        SessionWordEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun wordProgressDao(): WordProgressDao
    abstract fun sessionWordDao(): SessionWordDao

    companion object {
        private const val TAG = "VocabularyDatabase"
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
                // Add all migrations here
                .addMigrations(*MIGRATIONS)
                // Callback for database creation/opening
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(TAG, "Database created successfully")
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.d(TAG, "Database opened, version: ${db.version}")
                    }
                })
                .build()
        }

        /**
         * Migration from version 1 to 2.
         * Adds fields for the Word Selection Algorithm:
         * - mistake_count: How many times user answered incorrectly
         * - correct_count: How many times user answered correctly
         * - last_seen_date: Timestamp for spaced repetition
         * - next_review_date: When word should be shown again
         * - familiarity_score: Mastery level (0.0 to 1.0)
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.i(TAG, "Starting migration from v1 to v2")
                
                try {
                    db.execSQL("ALTER TABLE word_progress ADD COLUMN mistake_count INTEGER NOT NULL DEFAULT 0")
                    Log.d(TAG, "Added column: mistake_count")
                    
                    db.execSQL("ALTER TABLE word_progress ADD COLUMN correct_count INTEGER NOT NULL DEFAULT 0")
                    Log.d(TAG, "Added column: correct_count")
                    
                    db.execSQL("ALTER TABLE word_progress ADD COLUMN last_seen_date INTEGER DEFAULT NULL")
                    Log.d(TAG, "Added column: last_seen_date")
                    
                    db.execSQL("ALTER TABLE word_progress ADD COLUMN next_review_date INTEGER DEFAULT NULL")
                    Log.d(TAG, "Added column: next_review_date")
                    
                    db.execSQL("ALTER TABLE word_progress ADD COLUMN familiarity_score REAL NOT NULL DEFAULT 0.0")
                    Log.d(TAG, "Added column: familiarity_score")
                    
                    Log.i(TAG, "Migration v1→v2 completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Migration v1→v2 failed", e)
                    throw e
                }
            }
        }

        private val MIGRATIONS: Array<Migration> = arrayOf(
            MIGRATION_1_2
        )
    }
}



