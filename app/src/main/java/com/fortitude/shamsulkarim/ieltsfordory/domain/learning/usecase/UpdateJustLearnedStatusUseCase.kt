package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateJustLearnedStatusUseCase(private val repository: LearningRepository) {
    fun execute(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int) =
        repository.updateJustLearnedStatus(level, words, mostMistakenIndex)
}




