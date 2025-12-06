package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

class GetFavoriteWordsUseCase(private val repository: VocabularyRepository) {
    fun execute(): List<VocabularyWord> = repository.getFavoriteWords()
}


