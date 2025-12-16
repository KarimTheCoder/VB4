package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model

/**
 * Domain entity representing a word in a learning/training session.
 * Contains full word details needed for the training UI.
 */
data class SessionWord(
    val id: Int,
    val wordDatabasePos: String,
    val word: String,
    val translation: String,
    val secondTranslation: String? = null,
    val pronunciation: String? = null,
    val grammar: String? = null,
    val example1: String? = null,
    val example2: String? = null,
    val example3: String? = null,
    val vocabularyType: String? = null,
    val level: DifficultyLevel,
    val isLearned: Boolean = false,
    val isFavorite: Boolean = false,
    val isMostMistaken: Boolean = false
) {
    /**
     * Returns all non-null examples as a list.
     */
    fun getExamples(): List<String> {
        return listOfNotNull(example1, example2, example3)
    }

    /**
     * Creates a copy with updated learning status.
     */
    fun markAsLearned(): SessionWord = copy(isLearned = true)

    /**
     * Creates a copy marked as most mistaken.
     */
    fun markAsMostMistaken(): SessionWord = copy(isMostMistaken = true)
}


