package com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository

class IsTtsReadyUseCase(private val repository: TtsRepository) {
    fun execute(): Boolean = repository.isReady()
}

