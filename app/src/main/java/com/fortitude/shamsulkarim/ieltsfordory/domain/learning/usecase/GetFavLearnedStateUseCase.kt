package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository

class GetFavLearnedStateUseCase(private val repository: LearningRepository) {
    suspend fun execute(userName: String) = repository.getFavLearnedState(userName)
}
