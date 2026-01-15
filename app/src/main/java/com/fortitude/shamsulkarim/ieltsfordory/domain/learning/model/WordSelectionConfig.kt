package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model

/**
 * Configuration for the word selection algorithm.
 * Defines parameters for selecting words for a learning session.
 */
data class WordSelectionConfig(
    /** The difficulty level to select words from ("beginner", "intermediate", "advanced") */
    val level: String,
    
    /** Number of words to include in a session (default: 5) */
    val wordsPerSession: Int = 5,
    
    /** Whether to include words that are due for review (spaced repetition) */
    val includeReviewWords: Boolean = true,
    
    /** Word IDs to exclude (e.g., words user skipped in HomeScreen) */
    val skipWordIds: List<Int> = emptyList(),
    
    /** Vocabulary sources to include (null = all active sources) */
    val sources: List<String>? = null
) {
    companion object {
        /** Default configuration for a standard learning session */
        fun default(level: String) = WordSelectionConfig(
            level = level,
            wordsPerSession = 5,
            includeReviewWords = true
        )
        
        /** Configuration for a quick review session */
        fun quickReview(level: String) = WordSelectionConfig(
            level = level,
            wordsPerSession = 3,
            includeReviewWords = true
        )
        
        /** Configuration for an extended learning session */
        fun extended(level: String) = WordSelectionConfig(
            level = level,
            wordsPerSession = 10,
            includeReviewWords = true
        )
    }
}
