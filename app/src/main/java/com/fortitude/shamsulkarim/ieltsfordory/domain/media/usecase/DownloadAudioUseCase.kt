package com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository

class DownloadAudioUseCase(private val repository: AudioRepository) {
    fun execute(wordName: String, callback: AudioRepository.Callback) {
        repository.downloadAudio(wordName, callback)
    }
}



