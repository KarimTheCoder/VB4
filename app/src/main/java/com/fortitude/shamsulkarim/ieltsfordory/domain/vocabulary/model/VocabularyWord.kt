package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model

/**
 * Domain model representing a vocabulary word.
 * Replaces the legacy Word.java model with proper typing.
 */
data class VocabularyWord(
    val id: Int,
    val word: String,
    val translation: String,
    val pronunciation: String? = null,
    val grammar: String? = null,
    val example1: String? = null,
    val example2: String? = null,
    val example3: String? = null,
    val source: VocabularySource,
    val level: String = "beginner", // "beginner", "intermediate", "advanced"
    val isFavorite: Boolean = false,
    val isLearned: Boolean = false,
    val familiarityScore: Double = 0.0,
    // Second language support
    val wordSecondLang: String? = null,
    val translationSecondLang: String? = null,
    val example1SecondLang: String? = null,
    val example2SecondLang: String? = null,
    val example3SecondLang: String? = null
) {
    /**
     * Returns all non-null examples as a list.
     */
    fun getExamples(): List<String> = listOfNotNull(example1, example2, example3)

    /**
     * Returns all non-null second language examples as a list.
     */
    fun getExamplesSecondLang(): List<String> = listOfNotNull(
        example1SecondLang, example2SecondLang, example3SecondLang
    )

    /**
     * Creates a copy with updated favorite status.
     */
    fun withFavorite(favorite: Boolean): VocabularyWord = copy(isFavorite = favorite)

    /**
     * Creates a copy with updated learned status.
     */
    fun withLearned(learned: Boolean): VocabularyWord = copy(isLearned = learned)
}


