package com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository

class SpeakTextUseCase(private val repository: TtsRepository) {
    fun execute(text: String, flush: Boolean) = repository.speak(text, flush)
}



