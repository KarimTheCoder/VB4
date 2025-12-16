package com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase

import com.fortitude.shamsulkarim.ieltsfordory.domain.media.ImageRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.ImageData
import com.google.firebase.storage.FirebaseStorage

class FirebaseImageRepository : ImageRepository {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun downloadImage(wordName: String, quality: String, callback: ImageRepository.Callback) {
        val path = "gs://fir-userauthentication-f751c.appspot.com/$quality/$wordName.png"
        val ref = storage.getReferenceFromUrl(path)
        ref.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            callback.onSuccess(ImageData(bytes))
        }.addOnFailureListener { e ->
            callback.onError(e)
        }
    }
}



