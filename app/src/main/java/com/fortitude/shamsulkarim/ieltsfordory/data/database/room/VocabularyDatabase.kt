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
 * - Future versions: Add migrations to MIGRATIONS array
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
         * Array of all migrations.
         * Add new migrations here when schema changes.
         * 
         * Example for future migration from version 1 to 2:
         * private val MIGRATION_1_2 = object : Migration(1, 2) {
         *     override fun migrate(database: SupportSQLiteDatabase) {
         *         database.execSQL("ALTER TABLE word_progress ADD COLUMN new_column TEXT")
         *     }
         * }
         */
        private val MIGRATIONS: Array<Migration> = arrayOf(
            // Add migrations here as needed, e.g.:
            // MIGRATION_1_2,
            // MIGRATION_2_3,
        )
    }
}

