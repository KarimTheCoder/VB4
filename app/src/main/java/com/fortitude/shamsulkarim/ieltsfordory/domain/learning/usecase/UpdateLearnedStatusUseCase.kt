package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateLearnedStatusUseCase(private val repository: LearningRepository) {
    fun execute(words: List<VocabularyWord>) = repository.updateLearnedStatus(words)
}




