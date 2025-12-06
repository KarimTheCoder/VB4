package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * Repository interface for vocabulary data access.
 * Uses proper domain types for type safety.
 */
interface VocabularyRepository {
    fun getVocabulary(level: String): List<VocabularyWord>
    fun getFavoriteWords(): List<VocabularyWord>
    fun getLearnedWords(level: String): List<VocabularyWord>
    fun getUnlearnedWords(level: String): List<VocabularyWord>
    fun getLearnedCount(level: String): Int
    fun getTotalCount(level: String): Int
    fun updateFavorite(source: VocabularySource, wordId: Int, isFavorite: Boolean)
    fun updateLearnState(source: VocabularySource, wordId: Int, isLearned: Boolean)
}


