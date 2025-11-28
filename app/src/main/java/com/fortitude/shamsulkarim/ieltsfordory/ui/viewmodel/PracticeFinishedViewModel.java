package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;

public class PracticeFinishedViewModel extends AndroidViewModel {

    private final AppPreferences prefs;

    public PracticeFinishedViewModel(@NonNull Application application) {
        super(application);
        prefs = AppPreferences.get(application);
    }

    public int getFavoriteWordCount() {
        return prefs.getFavoriteWordCount();
    }

    public boolean getSoundState() {
        return prefs.getSoundState();
    }
}
