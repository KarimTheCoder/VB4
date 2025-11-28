package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

public class TrainUiState {
    public boolean alreadyClicked = true;
    public boolean isTimerRunning = false; // formerly 'progress'
    public int progressCount = 0;
    public boolean isVocabularySelectionShown = false; // formerly 'isWhichvocbularyToText'
    public boolean isSpeakerVisible = false;
    public boolean shouldTriggerNextWord = false;
}
