package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class GetTotalCountUseCase(private val repository: VocabularyRepository) {
    suspend fun execute(level: String): Int = repository.getTotalCount(level)
}
