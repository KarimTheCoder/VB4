package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class GetJustLearnedSessionDataUseCase(private val repository: LearningRepository) {
    fun execute(level: String) = repository.getJustLearnedSessionData(level)
}

