package com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.WordProgress

/**
 * Room entity for storing word progress (favorite, learned, blacklisted, skipped states).
 * Consolidates the legacy IELTSWordDatabase, TOEFLWordDatabase, SATWordDatabase, GREWordDatabase.
 */
@Entity(
    tableName = "word_progress",
    indices = [
        Index(value = ["source", "word_id"], unique = true),
        Index(value = ["is_favorite"]),
        Index(value = ["is_learned"])
    ]
)
data class WordProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "word_id")
    val wordId: Int,

    @ColumnInfo(name = "source")
    val source: String,  // "IELTS", "TOEFL", "SAT", "GRE"

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "is_learned")
    val isLearned: Boolean = false,

    @ColumnInfo(name = "is_blacklisted")
    val isBlacklisted: Boolean = false,

    @ColumnInfo(name = "is_skipped")
    val isSkipped: Boolean = false
) {
    /**
     * Converts this entity to the domain model.
     */
    fun toDomain(): WordProgress {
        return WordProgress(
            wordId = wordId,
            source = VocabularySource.valueOf(source),
            isFavorite = isFavorite,
            isLearned = isLearned,
            isBlacklisted = isBlacklisted,
            isSkipped = isSkipped
        )
    }

    companion object {
        /**
         * Creates an entity from the domain model.
         */
        fun fromDomain(domain: WordProgress): WordProgressEntity {
            return WordProgressEntity(
                wordId = domain.wordId,
                source = domain.source.name,
                isFavorite = domain.isFavorite,
                isLearned = domain.isLearned,
                isBlacklisted = domain.isBlacklisted,
                isSkipped = domain.isSkipped
            )
        }

        /**
         * Creates a default entity for a new word (not learned, not favorited).
         */
        fun createDefault(wordId: Int, source: String): WordProgressEntity {
            return WordProgressEntity(
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
