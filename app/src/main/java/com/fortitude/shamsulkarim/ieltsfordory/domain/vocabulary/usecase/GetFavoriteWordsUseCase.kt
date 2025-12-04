package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class GetFavoriteWordsUseCase(private val repository: VocabularyRepository) {
    fun execute() = repository.getFavoriteWords()
}

