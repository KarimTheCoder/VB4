package com.fortitude.shamsulkarim.ieltsfordory.domain.learning

import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData

interface LearningRepository {
    fun getFavLearnedState(userName: String): FavLearnedState
    fun fetchSessionWords(level: String, wordsPerSession: Int): List<Word>
    fun getAllUnlearnedWords(level: String): List<Word>
    fun updateLearnedStatus(words: List<Word>)
    fun updateJustLearnedStatus(level: String, words: List<Word>, mostMistakenIndex: Int)
    fun updateFavoriteStatus(word: Word, newStatus: String)
    fun updateLearnedStatus(word: Word, newStatus: String)
    fun getJustLearnedSessionData(level: String): JustLearnedSessionData
}

