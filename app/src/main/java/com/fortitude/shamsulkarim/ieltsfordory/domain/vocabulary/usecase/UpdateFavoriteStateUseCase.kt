package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource

class UpdateFavoriteStateUseCase(private val repository: VocabularyRepository) {
    fun execute(source: VocabularySource, wordId: Int, isFavorite: Boolean) = 
        repository.updateFavorite(source, wordId, isFavorite)
}


