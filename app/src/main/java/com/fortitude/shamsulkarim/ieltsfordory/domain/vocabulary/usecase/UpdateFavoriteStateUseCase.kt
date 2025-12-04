package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class UpdateFavoriteStateUseCase(private val repository: VocabularyRepository) {
    fun execute(source: String, id: String, isFavorite: String) = repository.updateFavorite(source, id, isFavorite)
}

