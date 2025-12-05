package com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository

class ShutdownTtsUseCase(private val repository: TtsRepository) {
    fun execute() = repository.shutdown()
}

