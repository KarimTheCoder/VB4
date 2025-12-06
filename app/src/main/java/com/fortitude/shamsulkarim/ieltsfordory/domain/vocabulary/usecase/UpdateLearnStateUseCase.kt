package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource

class UpdateLearnStateUseCase(private val repository: VocabularyRepository) {
    fun execute(source: VocabularySource, wordId: Int, isLearned: Boolean) = 
        repository.updateLearnState(source, wordId, isLearned)
}


