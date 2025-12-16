package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class GetLearnedCountUseCase(private val repository: VocabularyRepository) {
    fun execute(level: String) = repository.getLearnedCount(level)
}



