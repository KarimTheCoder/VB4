package com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.AudioData
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class FirebaseAudioRepository : AudioRepository {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun downloadAudio(wordName: String, callback: AudioRepository.Callback) {
        val lowerCaseWordName = wordName.lowercase()
        val ref = storage.getReferenceFromUrl(
            "gs://fir-userauthentication-f751c.appspot.com/audio/$lowerCaseWordName.mp3"
        )

        val localFile: File = try {
            File.createTempFile("Audio", "mp3")
        } catch (e: IOException) {
            callback.onError(e)
            return
        }

        ref.getFile(localFile).addOnSuccessListener {
            callback.onSuccess(AudioData(localFile.absolutePath))
        }.addOnFailureListener { e ->
            callback.onError(e)
        }
    }
}

