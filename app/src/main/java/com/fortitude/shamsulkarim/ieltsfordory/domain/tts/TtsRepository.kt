package com.fortitude.shamsulkarim.ieltsfordory.domain.tts

interface TtsRepository {
    fun isReady(): Boolean
    fun speak(text: String, flush: Boolean)
    fun stop()
    fun shutdown()
}



