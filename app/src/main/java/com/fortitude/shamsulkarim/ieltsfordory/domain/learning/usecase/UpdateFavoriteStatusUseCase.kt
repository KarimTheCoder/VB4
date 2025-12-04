package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class UpdateFavoriteStatusUseCase(private val repository: LearningRepository) {
    fun execute(word: Word, newStatus: String) = repository.updateFavoriteStatus(word, newStatus)
}

