package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingViewModel extends AndroidViewModel {
    private final MutableLiveData<SettingsUiState> state = new MutableLiveData<>();
    private final SharedPreferences sp;

    public SettingViewModel(@NonNull Application app) {
        super(app);
        sp = app.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        state.setValue(new SettingsUiState.Loading());
        state.setValue(new SettingsUiState.SettingsLoaded(snapshot()));
    }

    public LiveData<SettingsUiState> getState(){ return state; }

    public void onAuthenticated(String name, String email){ state.setValue(new SettingsUiState.SignedIn(name, email)); }
    public void onSignedOut(){ state.setValue(new SettingsUiState.SignedOut()); }

    public void setSound(boolean v){ sp.edit().putBoolean("soundState", v).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }
    public void setPronun(boolean v){ sp.edit().putBoolean("pronunState", v).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }
    public void setImageQuality(int idx){ sp.edit().putInt("imageQuality", idx).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }
    public void setDarkMode(int idx){ sp.edit().putInt("DarkMode", idx).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }
    public void setWordsPerSessionFromPos(int pos){ sp.edit().putInt("wordsPerSession", mapWps(pos)).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }
    public void setRepeatationPerSessionFromPos(int pos){ sp.edit().putInt("repeatationPerSession", mapRps(pos)).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); }

    public void setIeltsActive(boolean active){ if (ensureAtLeastOne(active, getToeflActive(), getSatActive(), getGreActive())) { sp.edit().putBoolean("isIELTSActive", active).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); } else { state.setValue(new SettingsUiState.Error("At least select one")); } }
    public void setToeflActive(boolean active){ if (ensureAtLeastOne(getIeltsActive(), active, getSatActive(), getGreActive())) { sp.edit().putBoolean("isTOEFLActive", active).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); } else { state.setValue(new SettingsUiState.Error("At least select one")); } }
    public void setSatActive(boolean active){ if (ensureAtLeastOne(getIeltsActive(), getToeflActive(), active, getGreActive())) { sp.edit().putBoolean("isSATActive", active).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); } else { state.setValue(new SettingsUiState.Error("At least select one")); } }
    public void setGreActive(boolean active){ if (ensureAtLeastOne(getIeltsActive(), getToeflActive(), getSatActive(), active)) { sp.edit().putBoolean("isGREActive", active).apply(); state.setValue(new SettingsUiState.SettingsLoaded(snapshot())); } else { state.setValue(new SettingsUiState.Error("At least select one")); } }

    private SettingsSnapshot snapshot(){
        boolean sound = sp.getBoolean("soundState", true);
        boolean pronun = sp.getBoolean("pronunState", true);
        int imageQuality = sp.getInt("imageQuality", 1);
        int darkMode = sp.getInt("DarkMode", 0);
        int wps = sp.getInt("wordsPerSession", 5);
        int rps = sp.getInt("repeatationPerSession", 5);
        boolean ielts = sp.getBoolean("isIELTSActive", true);
        boolean toefl = sp.getBoolean("isTOEFLActive", true);
        boolean sat = sp.getBoolean("isSATActive", true);
        boolean gre = sp.getBoolean("isGREActive", true);
        return new SettingsSnapshot(sound, pronun, imageQuality, darkMode, wps, rps, ielts, toefl, sat, gre);
    }

    private boolean ensureAtLeastOne(boolean i, boolean t, boolean s, boolean g){ return i || t || s || g; }

    private int mapWps(int pos){ switch(pos){ case 0: return 25; case 1: return 20; case 2: return 15; case 3: return 10; case 4: return 5; case 5: return 4; default: return 3; } }
    private int mapRps(int pos){ switch(pos){ case 0: return 25; case 1: return 20; case 2: return 15; case 3: return 10; case 4: return 5; case 5: return 4; default: return 3; } }

    private boolean getIeltsActive(){ return sp.getBoolean("isIELTSActive", true); }
    private boolean getToeflActive(){ return sp.getBoolean("isTOEFLActive", true); }
    private boolean getSatActive(){ return sp.getBoolean("isSATActive", true); }
    private boolean getGreActive(){ return sp.getBoolean("isGREActive", true); }
}

