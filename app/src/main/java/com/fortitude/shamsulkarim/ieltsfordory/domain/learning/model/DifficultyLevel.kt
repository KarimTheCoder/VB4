package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model

/**
 * Difficulty levels for vocabulary learning sessions.
 */
enum class DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    companion object {
        fun fromString(level: String): DifficultyLevel {
            return when (level.uppercase()) {
                "BEGINNER" -> BEGINNER
                "INTERMEDIATE" -> INTERMEDIATE
                "ADVANCED", "ADVANCE" -> ADVANCED
                else -> BEGINNER
            }
        }
    }
}
