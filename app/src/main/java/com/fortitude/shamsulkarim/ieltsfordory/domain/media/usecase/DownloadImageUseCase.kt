package com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.ImageRepository

class DownloadImageUseCase(private val repository: ImageRepository) {
    fun execute(wordName: String, quality: String, callback: ImageRepository.Callback) {
        repository.downloadImage(wordName, quality, callback)
    }
}



