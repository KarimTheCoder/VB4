package com.fortitude.shamsulkarim.ieltsfordory.domain.media

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.ImageData

interface ImageRepository {
    interface Callback {
        fun onSuccess(data: ImageData)
        fun onError(e: Exception)
    }

    fun downloadImage(wordName: String, quality: String, callback: Callback)
}



