package com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository

class UpdateLearnStateUseCase(private val repository: VocabularyRepository) {
    fun execute(source: String, id: String, isLearned: String) = repository.updateLearnState(source, id, isLearned)
}

