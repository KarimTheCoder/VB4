package com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.DifficultyLevel
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.SessionWord

/**
 * Room entity for storing session words during training.
 * Consolidates the legacy JustLearnedDatabaseBeginner, JustLearnedDatabaseIntermediate, 
 * JustLearnedDatabaseAdvance into a single table with a level column.
 */
@Entity(
    tableName = "session_words",
    indices = [Index(value = ["level"])]
)
data class SessionWordEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "word_database_pos")
    val wordDatabasePos: String,

    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "translation")
    val translation: String,

    @ColumnInfo(name = "second_translation")
    val secondTranslation: String? = null,

    @ColumnInfo(name = "pronunciation")
    val pronunciation: String? = null,

    @ColumnInfo(name = "grammar")
    val grammar: String? = null,

    @ColumnInfo(name = "example1")
    val example1: String? = null,

    @ColumnInfo(name = "example2")
    val example2: String? = null,

    @ColumnInfo(name = "example3")
    val example3: String? = null,

    @ColumnInfo(name = "vocabulary_type")
    val vocabularyType: String? = null,

    @ColumnInfo(name = "level")
    val level: String,  // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    @ColumnInfo(name = "is_learned")
    val isLearned: Boolean = false,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "is_most_mistaken")
    val isMostMistaken: Boolean = false
) {
    /**
     * Converts this entity to the domain model.
     */
    fun toDomain(): SessionWord {
        return SessionWord(
            id = id,
            wordDatabasePos = wordDatabasePos,
            word = word,
            translation = translation,
            secondTranslation = secondTranslation,
            pronunciation = pronunciation,
            grammar = grammar,
            example1 = example1,
            example2 = example2,
            example3 = example3,
            vocabularyType = vocabularyType,
            level = DifficultyLevel.fromString(level),
            isLearned = isLearned,
            isFavorite = isFavorite,
            isMostMistaken = isMostMistaken
        )
    }

    companion object {
        /**
         * Creates an entity from the domain model.
         */
        fun fromDomain(domain: SessionWord): SessionWordEntity {
            return SessionWordEntity(
                id = domain.id,
                wordDatabasePos = domain.wordDatabasePos,
                word = domain.word,
                translation = domain.translation,
                secondTranslation = domain.secondTranslation,
                pronunciation = domain.pronunciation,
                grammar = domain.grammar,
                example1 = domain.example1,
                example2 = domain.example2,
                example3 = domain.example3,
                vocabularyType = domain.vocabularyType,
                level = domain.level.name,
                isLearned = domain.isLearned,
                isFavorite = domain.isFavorite,
                isMostMistaken = domain.isMostMistaken
            )
        }
    }
}
