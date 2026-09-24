package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateLearnedStatusSingleUseCase(private val repository: LearningRepository) {
    suspend fun execute(word: VocabularyWord, newStatus: Boolean) = repository.updateLearnedStatus(word, newStatus)
}
