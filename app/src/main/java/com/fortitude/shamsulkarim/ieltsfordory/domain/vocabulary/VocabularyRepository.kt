package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * Repository interface for vocabulary data access.
 * Uses proper domain types for type safety with non-blocking Coroutine suspend functions.
 */
interface VocabularyRepository {
    suspend fun getVocabulary(level: String): List<VocabularyWord>
    suspend fun getFavoriteWords(): List<VocabularyWord>
    suspend fun getLearnedWords(level: String): List<VocabularyWord>
    suspend fun getUnlearnedWords(level: String): List<VocabularyWord>
    
    /** Get ALL unlearned words from all sources (no level filtering) */
    suspend fun getAllUnlearnedWords(): List<VocabularyWord>
    
    suspend fun getLearnedCount(level: String): Int
    suspend fun getTotalCount(level: String): Int
    suspend fun updateFavorite(source: VocabularySource, wordId: Int, isFavorite: Boolean)
    suspend fun updateLearnState(source: VocabularySource, wordId: Int, isLearned: Boolean)
    
    /**
     * Get random words to use as distractors.
     */
    suspend fun getRandomWords(limit: Int, excludeIds: Set<Int>): List<VocabularyWord>
}
