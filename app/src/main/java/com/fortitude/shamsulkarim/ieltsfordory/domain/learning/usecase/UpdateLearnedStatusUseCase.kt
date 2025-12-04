package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateLearnedStatusUseCase(private val repository: LearningRepository) {
    fun execute(words: List<Word>) = repository.updateLearnedStatus(words)
}

