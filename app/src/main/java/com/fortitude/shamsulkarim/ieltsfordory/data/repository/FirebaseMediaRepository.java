package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class FirebaseMediaRepository implements AudioRepository {
    private final FirebaseStorage storage;

    public FirebaseMediaRepository() {
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

    public interface ImageCallback {
        void onImageReady(byte[] bytes);

        void onError(Exception e);
    }

    public void downloadImage(String wordName, String quality, ImageCallback callback) {
        String lowerCaseWordName = wordName.toLowerCase();
        // Quality maps to folder names: High, Medium, Low
        // Assuming quality is passed as "High", "Medium", "Low" or handled by caller.
        // In NewTrainRecyclerView: imageQualityString =
        // imageFolderName[imageQualityNum];
        // imageQualityString = imageQualityString.toLowerCase();
        // storageRef.child(imageQualityString+"/"+wordName+".png")

        // Wait, NewTrainRecyclerView logic:
        // if(word.vocabularyType.equalsIgnoreCase("TOEFL")){ wordName = word.getWord();
        // } else { wordName = word.getWord().toLowerCase(); }
        // storageRef.child(imageQualityString+"/"+wordName+".png")

        // I should pass the full path or construct it here?
        // Better to pass quality and wordName (already processed) or handle processing
        // here?
        // Let's handle processing in Repository if possible, but NewTrainRecyclerView
        // has specific logic for TOEFL.
        // I'll accept the constructed path or components.
        // Let's accept quality (lowercase) and wordName (correct case).

        String path = "gs://fir-userauthentication-f751c.appspot.com/" + quality + "/" + wordName + ".png";
        android.util.Log.d("FirebaseMediaRepository", "Downloading image from: " + path);
        StorageReference gsReference = storage.getReferenceFromUrl(path);

        gsReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            callback.onImageReady(bytes);
        }).addOnFailureListener(e -> {
            android.util.Log.e("FirebaseMediaRepository", "Error downloading image from: " + path, e);
            callback.onError(e);
        });
    }
}
