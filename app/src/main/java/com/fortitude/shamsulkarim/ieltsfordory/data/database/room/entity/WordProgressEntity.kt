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
    val isSkipped: Boolean = false,

    // ===== NEW FIELDS for Word Selection Algorithm =====
    
    /** How many times the user answered incorrectly for this word */
    @ColumnInfo(name = "mistake_count")
    val mistakeCount: Int = 0,

    /** How many times the user answered correctly for this word */
    @ColumnInfo(name = "correct_count")
    val correctCount: Int = 0,

    /** Timestamp (millis) when this word was last shown to the user */
    @ColumnInfo(name = "last_seen_date")
    val lastSeenDate: Long? = null,

    /** Timestamp (millis) when this word should be reviewed next (spaced repetition) */
    @ColumnInfo(name = "next_review_date")
    val nextReviewDate: Long? = null,

    /** Familiarity score from 0.0 (unknown) to 1.0 (mastered) */
    @ColumnInfo(name = "familiarity_score")
    val familiarityScore: Float = 0f
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
            isSkipped = isSkipped,
            mistakeCount = mistakeCount,
            correctCount = correctCount,
            lastSeenDate = lastSeenDate,
            nextReviewDate = nextReviewDate,
            familiarityScore = familiarityScore
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
                isSkipped = domain.isSkipped,
                mistakeCount = domain.mistakeCount,
                correctCount = domain.correctCount,
                lastSeenDate = domain.lastSeenDate,
                nextReviewDate = domain.nextReviewDate,
                familiarityScore = domain.familiarityScore
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
                isSkipped = false,
                mistakeCount = 0,
                correctCount = 0,
                lastSeenDate = null,
                nextReviewDate = null,
                familiarityScore = 0f
            )
        }
    }
}


