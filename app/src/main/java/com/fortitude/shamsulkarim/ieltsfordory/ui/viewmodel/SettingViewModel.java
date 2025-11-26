package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;

public class SettingViewModel extends AndroidViewModel {
    private final MutableLiveData<SettingsUiState> state = new MutableLiveData<>();
    private final AppPreferences prefs;

    public SettingViewModel(@NonNull Application app) {
        super(app);
        prefs = AppPreferences.get(app);
        state.setValue(new SettingsUiState.Loading());
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public LiveData<SettingsUiState> getState() {
        return state;
    }

    public void onAuthenticated(String name, String email) {
        state.setValue(new SettingsUiState.SignedIn(name, email));
    }

    public void onSignedOut() {
        state.setValue(new SettingsUiState.SignedOut());
    }

    public void setSound(boolean v) {
        prefs.setBool("soundState", v);
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setPronun(boolean v) {
        prefs.setBool("pronunState", v);
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setImageQuality(int idx) {
        prefs.setInt("imageQuality", idx);
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setDarkMode(int idx) {
        prefs.setInt(AppPreferences.KEY_DARK_MODE, idx);
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setWordsPerSessionFromPos(int pos) {
        prefs.setInt(AppPreferences.KEY_WORDS_PER_SESSION, mapWps(pos));
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setRepeatationPerSessionFromPos(int pos) {
        prefs.setInt(AppPreferences.KEY_REPEATATION_PER_SESSION, mapRps(pos));
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public void setIeltsActive(boolean active) {
        if (ensureAtLeastOne(active, getToeflActive(), getSatActive(), getGreActive())) {
            prefs.setBool("isIELTSActive", active);
            state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
        } else {
            state.setValue(new SettingsUiState.Error("At least select one"));
        }
    }

    public void setToeflActive(boolean active) {
        if (ensureAtLeastOne(getIeltsActive(), active, getSatActive(), getGreActive())) {
            prefs.setBool("isTOEFLActive", active);
            state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
        } else {
            state.setValue(new SettingsUiState.Error("At least select one"));
        }
    }

    public void setSatActive(boolean active) {
        if (ensureAtLeastOne(getIeltsActive(), getToeflActive(), active, getGreActive())) {
            prefs.setBool("isSATActive", active);
            state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
        } else {
            state.setValue(new SettingsUiState.Error("At least select one"));
        }
    }

    public void setGreActive(boolean active) {
        if (ensureAtLeastOne(getIeltsActive(), getToeflActive(), getSatActive(), active)) {
            prefs.setBool("isGREActive", active);
            state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
        } else {
            state.setValue(new SettingsUiState.Error("At least select one"));
        }
    }

    public void toggleLanguage() {
        boolean current = prefs.getString("secondlanguage", "english").equalsIgnoreCase("spanish");
        prefs.setString("secondlanguage", current ? "english" : "spanish");
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    private SettingsSnapshot snapshot() {
        boolean sound = prefs.getBool("soundState", true);
        boolean pronun = prefs.getBool("pronunState", true);
        int imageQuality = prefs.getInt("imageQuality", 1);
        int darkMode = prefs.getInt(AppPreferences.KEY_DARK_MODE, 0);
        int wps = prefs.getInt(AppPreferences.KEY_WORDS_PER_SESSION, 5);
        int rps = prefs.getInt(AppPreferences.KEY_REPEATATION_PER_SESSION, 5);
        boolean ielts = prefs.getBool("isIELTSActive", true);
        boolean toefl = prefs.getBool("isTOEFLActive", true);
        boolean sat = prefs.getBool("isSATActive", true);
        boolean gre = prefs.getBool("isGREActive", true);
        boolean isSpanish = prefs.getString("secondlanguage", "english").equalsIgnoreCase("spanish");
        boolean isTrialActive = prefs.isTrialActive();
        return new SettingsSnapshot(sound, pronun, imageQuality, darkMode, wps, rps, ielts, toefl, sat, gre, isSpanish,
                isTrialActive);
    }

    private boolean ensureAtLeastOne(boolean i, boolean t, boolean s, boolean g) {
        return i || t || s || g;
    }

    private int mapWps(int pos) {
        switch (pos) {
            case 0:
                return 25;
            case 1:
                return 20;
            case 2:
                return 15;
            case 3:
                return 10;
            case 4:
                return 5;
            case 5:
                return 4;
            default:
                return 3;
        }
    }

    private int mapRps(int pos) {
        switch (pos) {
            case 0:
                return 25;
            case 1:
                return 20;
            case 2:
                return 15;
            case 3:
                return 10;
            case 4:
                return 5;
            case 5:
                return 4;
            default:
                return 3;
        }
    }

    private boolean getIeltsActive() {
        return prefs.getBool("isIELTSActive", true);
    }

    private boolean getToeflActive() {
        return prefs.getBool("isTOEFLActive", true);
    }

    private boolean getSatActive() {
        return prefs.getBool("isSATActive", true);
    }

    private boolean getGreActive() {
        return prefs.getBool("isGREActive", true);
    }
}
