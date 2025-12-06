package com.fortitude.shamsulkarim.ieltsfordory.domain.learning

import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData

interface LearningRepository {
    fun getFavLearnedState(userName: String): FavLearnedState
    fun fetchSessionWords(level: String, wordsPerSession: Int): List<VocabularyWord>
    fun getAllUnlearnedWords(level: String): List<VocabularyWord>
    fun updateLearnedStatus(words: List<VocabularyWord>)
    fun updateJustLearnedStatus(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int)
    fun updateFavoriteStatus(word: VocabularyWord, newStatus: Boolean)
    fun updateLearnedStatus(word: VocabularyWord, newStatus: Boolean)
    fun getJustLearnedSessionData(level: String): JustLearnedSessionData
}

