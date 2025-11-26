package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

public class SettingsSnapshot {
    public final boolean sound;
    public final boolean pronun;
    public final int imageQuality;
    public final int darkMode;
    public final int wordsPerSession;
    public final int repeatationPerSession;
    public final boolean ieltsActive;
    public final boolean toeflActive;
    public final boolean satActive;
    public final boolean greActive;
    public final boolean isSpanish;
    public final boolean isTrialActive;

    public SettingsSnapshot(boolean sound, boolean pronun, int imageQuality, int darkMode, int wordsPerSession,
            int repeatationPerSession, boolean ieltsActive, boolean toeflActive, boolean satActive, boolean greActive,
            boolean isSpanish, boolean isTrialActive) {
        this.sound = sound;
        this.pronun = pronun;
        this.imageQuality = imageQuality;
        this.darkMode = darkMode;
        this.wordsPerSession = wordsPerSession;
        this.repeatationPerSession = repeatationPerSession;
        this.ieltsActive = ieltsActive;
        this.toeflActive = toeflActive;
        this.satActive = satActive;
        this.greActive = greActive;
        this.isSpanish = isSpanish;
        this.isTrialActive = isTrialActive;
    }
}
