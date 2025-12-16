package com.fortitude.shamsulkarim.ieltsfordory.domain.media

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.AudioData

interface AudioRepository {
    interface Callback {
        fun onSuccess(data: AudioData)
        fun onError(e: Exception)
    }

    fun downloadAudio(wordName: String, callback: Callback)
}



