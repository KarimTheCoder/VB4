package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word

interface VocabularyRepository {
    fun getVocabulary(level: String): List<Word>
    fun getFavoriteWords(): List<Word>
    fun getLearnedWords(level: String): List<Word>
    fun getUnlearnedWords(level: String): List<Word>
    fun getLearnedCount(level: String): Int
    fun getTotalCount(level: String): Int
    fun updateFavorite(source: String, id: String, isFavorite: String)
    fun updateLearnState(source: String, id: String, isLearned: String)
}

