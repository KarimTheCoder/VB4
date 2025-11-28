package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import java.util.ArrayList;
import java.util.List;

public class PracticeUiState {
    public int showCycle = 0;
    public int quizCycle = 0;
    public int mistakes = 0;
    public int lastMistake = 13;
    public boolean IsWrongAnswer = true;
    public boolean isTimerRunning = false;
    public boolean alreadyclicked = true;
    public int progressCount = 0;

    // New fields for ViewModel communication
    public int lastAnswerStatus = 0; // 0: None, 1: Correct, 2: Wrong
    public List<Word> currentOptions = new ArrayList<>();
    public Word currentWord;
}
