package com.fortitude.shamsulkarim.ieltsfordory.data.database.migration

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.VocabularyDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.SessionWordEntity
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Helper class to migrate data from legacy SQLite databases to Room.
 * 
 * Legacy databases:
 * - IELTSWordDatabase.db, TOEFLWordDatabase.db, SATWordDatabase.db, GREWordDatabase.db
 * - JustLearnedDatabaseBeginner.db, JustLearnedDatabaseIntermediate.db, JustLearnedDatabaseAdvance.db
 */
class LegacyMigrationHelper(
    private val context: Context,
    private val database: VocabularyDatabase
) {
    companion object {
        private const val TAG = "LegacyMigration"
        private const val MIGRATION_PREFS = "room_migration_prefs"
        private const val KEY_MIGRATED = "legacy_data_migrated"
        private const val KEY_MIGRATION_VERSION = "migration_version"
        private const val CURRENT_MIGRATION_VERSION = 1

        // Legacy word progress databases
        private val WORD_DATABASES = mapOf(
            "IELTS" to "IELTSWordDatabase.db",
            "TOEFL" to "TOEFLWordDatabase.db",
            "SAT" to "SATWordDatabase.db",
            "GRE" to "GREWordDatabase.db"
        )

        // Legacy session databases
        private val SESSION_DATABASES = mapOf(
            "BEGINNER" to "JustLearnedDatabaseBeginner.db",
            "INTERMEDIATE" to "JustLearnedDatabaseIntermediate.db",
            "ADVANCED" to "JustLearnedDatabaseAdvance.db"
        )

        // Legacy table/column names
        private const val WORD_TABLE = "beginner_table"
        private const val SESSION_TABLE = "JustLearnedDatabase_table"
    }

    private val prefs = context.getSharedPreferences(MIGRATION_PREFS, Context.MODE_PRIVATE)

    /**
     * Checks if migration is needed and performs it if so.
     * This should be called on app startup before any database operations.
     */
    suspend fun migrateIfNeeded(): MigrationResult = withContext(Dispatchers.IO) {
        val alreadyMigrated = prefs.getBoolean(KEY_MIGRATED, false)
        val migratedVersion = prefs.getInt(KEY_MIGRATION_VERSION, 0)

        if (alreadyMigrated && migratedVersion >= CURRENT_MIGRATION_VERSION) {
            Log.d(TAG, "Migration already completed (version $migratedVersion)")
            return@withContext MigrationResult.AlreadyMigrated
        }

        try {
            Log.d(TAG, "Starting legacy database migration...")
            
            // Migrate word progress databases
            var wordsMigrated = 0
            WORD_DATABASES.forEach { (source, dbName) ->
                wordsMigrated += migrateWordProgressDatabase(source, dbName)
            }

            // Migrate session databases
            var sessionsMigrated = 0
            SESSION_DATABASES.forEach { (level, dbName) ->
                sessionsMigrated += migrateSessionDatabase(level, dbName)
            }

            // Mark migration as complete
            prefs.edit()
                .putBoolean(KEY_MIGRATED, true)
                .putInt(KEY_MIGRATION_VERSION, CURRENT_MIGRATION_VERSION)
                .apply()

            Log.d(TAG, "Migration complete: $wordsMigrated words, $sessionsMigrated sessions")
            MigrationResult.Success(wordsMigrated, sessionsMigrated)
        } catch (e: Exception) {
            Log.e(TAG, "Migration failed", e)
            MigrationResult.Failed(e.message ?: "Unknown error")
        }
    }

    /**
     * Migrates word progress data from a legacy SQLite database.
     */
    private suspend fun migrateWordProgressDatabase(source: String, dbName: String): Int {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) {
            Log.d(TAG, "Database $dbName does not exist, skipping")
            return 0
        }

        var count = 0
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)
            
            // Check if the table exists
            if (!tableExists(db, WORD_TABLE)) {
                Log.d(TAG, "Table $WORD_TABLE does not exist in $dbName, skipping")
                return 0
            }
            
            cursor = db.rawQuery("SELECT * FROM $WORD_TABLE", null)

            val entities = mutableListOf<WordProgressEntity>()
            
            // Column indices (based on legacy schema)
            val idIndex = cursor.getColumnIndex("ID")
            val wordIndex = cursor.getColumnIndex("WORD")
            val favIndex = cursor.getColumnIndex("FAV")
            val learnedIndex = cursor.getColumnIndex("LEARNED")
            val blacklistIndex = cursor.getColumnIndex("BLACKLIST")
            val skipIndex = cursor.getColumnIndex("SKIP")

            while (cursor.moveToNext()) {
                val wordId = if (wordIndex >= 0) {
                    cursor.getString(wordIndex)?.toIntOrNull() ?: cursor.getInt(idIndex) - 1
                } else {
                    cursor.getInt(idIndex) - 1
                }

                val entity = WordProgressEntity(
                    wordId = wordId,
                    source = source,
                    isFavorite = parseBoolean(cursor.getStringOrNull(favIndex)),
                    isLearned = parseBoolean(cursor.getStringOrNull(learnedIndex)),
                    isBlacklisted = parseBoolean(cursor.getStringOrNull(blacklistIndex)),
                    isSkipped = parseBoolean(cursor.getStringOrNull(skipIndex))
                )
                entities.add(entity)
                count++
            }

            // Insert all entities into Room
            if (entities.isNotEmpty()) {
                database.wordProgressDao().insertAll(entities)
            }
            Log.d(TAG, "Migrated $count records from $dbName")

        } catch (e: Exception) {
            Log.w(TAG, "Error migrating $dbName: ${e.message}", e)
            // Don't throw - just skip this database
        } finally {
            cursor?.close()
            db?.close()
        }

        return count
    }

    /**
     * Checks if a table exists in the database.
     */
    private fun tableExists(db: SQLiteDatabase, tableName: String): Boolean {
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                arrayOf(tableName)
            )
            cursor.count > 0
        } catch (e: Exception) {
            false
        } finally {
            cursor?.close()
        }
    }

    /**
     * Migrates session word data from a legacy SQLite database.
     */
    private suspend fun migrateSessionDatabase(level: String, dbName: String): Int {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) {
            Log.d(TAG, "Database $dbName does not exist, skipping")
            return 0
        }

        var count = 0
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)
            
            // Check if the table exists
            if (!tableExists(db, SESSION_TABLE)) {
                Log.d(TAG, "Table $SESSION_TABLE does not exist in $dbName, skipping")
                return 0
            }
            
            cursor = db.rawQuery("SELECT * FROM $SESSION_TABLE", null)

            val entities = mutableListOf<SessionWordEntity>()

            // Column indices
            val idIndex = cursor.getColumnIndex("ID")
            val wordDbPosIndex = cursor.getColumnIndex("WORDDATABASEPOS")
            val wordIndex = cursor.getColumnIndex("WORD")
            val translationIndex = cursor.getColumnIndex("TRANSLATION")
            val secondTranslationIndex = cursor.getColumnIndex("SECONDTRANSLATION")
            val pronunIndex = cursor.getColumnIndex("PRONUN")
            val grammarIndex = cursor.getColumnIndex("GRAMMAR")
            val example1Index = cursor.getColumnIndex("EXAMPLE1")
            val example2Index = cursor.getColumnIndex("EXAMPLE2")
            val example3Index = cursor.getColumnIndex("EXAMPLE3")
            val vocabTypeIndex = cursor.getColumnIndex("VOCABULARYTYPE")
            val learnedIndex = cursor.getColumnIndex("LEARNED")
            val favIndex = cursor.getColumnIndex("FAV")
            val mostMistakenIndex = cursor.getColumnIndex("MOSTMISTAKEN")

            while (cursor.moveToNext()) {
                val entity = SessionWordEntity(
                    id = cursor.getInt(idIndex),
                    wordDatabasePos = cursor.getStringOrNull(wordDbPosIndex) ?: "",
                    word = cursor.getStringOrNull(wordIndex) ?: "",
                    translation = cursor.getStringOrNull(translationIndex) ?: "",
                    secondTranslation = cursor.getStringOrNull(secondTranslationIndex),
                    pronunciation = cursor.getStringOrNull(pronunIndex),
                    grammar = cursor.getStringOrNull(grammarIndex),
                    example1 = cursor.getStringOrNull(example1Index),
                    example2 = cursor.getStringOrNull(example2Index),
                    example3 = cursor.getStringOrNull(example3Index),
                    vocabularyType = cursor.getStringOrNull(vocabTypeIndex),
                    level = level,
                    isLearned = parseBoolean(cursor.getStringOrNull(learnedIndex)),
                    isFavorite = parseBoolean(cursor.getStringOrNull(favIndex)),
                    isMostMistaken = parseBoolean(cursor.getStringOrNull(mostMistakenIndex))
                )
                entities.add(entity)
                count++
            }

            // Insert all entities into Room
            if (entities.isNotEmpty()) {
                database.sessionWordDao().insertAll(entities)
            }
            Log.d(TAG, "Migrated $count records from $dbName")

        } catch (e: Exception) {
            Log.w(TAG, "Error migrating $dbName: ${e.message}", e)
            // Don't throw - just skip this database
        } finally {
            cursor?.close()
            db?.close()
        }

        return count
    }

    /**
     * Deletes all legacy database files after successful migration.
     * Call this only after verifying the migration was successful.
     */
    fun deleteLegacyDatabases() {
        val allDatabases = WORD_DATABASES.values + SESSION_DATABASES.values
        
        allDatabases.forEach { dbName ->
            try {
                val dbFile = context.getDatabasePath(dbName)
                if (dbFile.exists()) {
                    dbFile.delete()
                    Log.d(TAG, "Deleted legacy database: $dbName")
                }
                // Also delete journal and wal files
                File(dbFile.path + "-journal").delete()
                File(dbFile.path + "-wal").delete()
                File(dbFile.path + "-shm").delete()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete $dbName", e)
            }
        }
    }

    /**
     * Resets migration status, useful for testing.
     */
    fun resetMigrationStatus() {
        prefs.edit()
            .remove(KEY_MIGRATED)
            .remove(KEY_MIGRATION_VERSION)
            .apply()
    }

    /**
     * Checks if legacy databases exist.
     */
    fun hasLegacyDatabases(): Boolean {
        return WORD_DATABASES.values.any { dbName ->
            context.getDatabasePath(dbName).exists()
        }
    }

    // Helper extensions
    private fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (columnIndex >= 0 && !isNull(columnIndex)) getString(columnIndex) else null
    }

    private fun parseBoolean(value: String?): Boolean {
        return value?.equals("true", ignoreCase = true) == true || 
               value?.equals("True", ignoreCase = true) == true
    }
}

/**
 * Result of the migration operation.
 */
sealed class MigrationResult {
    object AlreadyMigrated : MigrationResult()
    data class Success(val wordsMigrated: Int, val sessionsMigrated: Int) : MigrationResult()
    data class Failed(val error: String) : MigrationResult()
}
