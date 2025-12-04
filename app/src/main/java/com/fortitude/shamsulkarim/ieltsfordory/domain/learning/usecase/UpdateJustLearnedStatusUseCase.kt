package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateJustLearnedStatusUseCase(private val repository: LearningRepository) {
    fun execute(level: String, words: List<Word>, mostMistakenIndex: Int) =
        repository.updateJustLearnedStatus(level, words, mostMistakenIndex)
}

