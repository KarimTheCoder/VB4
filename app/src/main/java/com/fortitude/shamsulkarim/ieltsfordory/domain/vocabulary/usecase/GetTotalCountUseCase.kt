package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class GetTotalCountUseCase(private val repository: VocabularyRepository) {
    fun execute(level: String) = repository.getTotalCount(level)
}



