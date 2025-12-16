package com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository

class StopTtsUseCase(private val repository: TtsRepository) {
    fun execute() = repository.stop()
}



