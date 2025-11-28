package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class FirebaseAudioRepository implements AudioRepository {
    private final FirebaseStorage storage;

    public FirebaseAudioRepository() {
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public void downloadAudio(String wordName, Callback callback) {
        String lowerCaseWordName = wordName.toLowerCase();
        StorageReference gsReference = storage
                .getReferenceFromUrl(
                        "gs://fir-userauthentication-f751c.appspot.com/audio/" + lowerCaseWordName + ".mp3");

        File localFile;
        try {
            localFile = File.createTempFile("Audio", "mp3");
        } catch (IOException e) {
            callback.onError(e);
            return;
        }

        gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            callback.onAudioReady(localFile);
        }).addOnFailureListener(e -> {
            callback.onError(e);
        });
    }
}
