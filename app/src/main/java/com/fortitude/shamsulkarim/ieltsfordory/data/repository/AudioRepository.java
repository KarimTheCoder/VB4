package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import java.io.File;

public interface AudioRepository {
    interface Callback {
        void onAudioReady(File audioFile);

        void onError(Exception e);
    }

    void downloadAudio(String wordName, Callback callback);
}
