package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

class GetLearnedWordsUseCase(private val repository: VocabularyRepository) {
    suspend fun execute(level: String): List<VocabularyWord> = repository.getLearnedWords(level)
}
