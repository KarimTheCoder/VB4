package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class FetchSessionWordsUseCase(private val repository: LearningRepository) {
    fun execute(level: String, wordsPerSession: Int) = repository.fetchSessionWords(level, wordsPerSession)
}

