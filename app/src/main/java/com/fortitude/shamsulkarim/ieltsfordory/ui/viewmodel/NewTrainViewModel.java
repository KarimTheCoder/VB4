package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.WordRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.TrainUiState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import android.os.CountDownTimer;

public class NewTrainViewModel extends AndroidViewModel {

    private final WordRepository repository;
    private final SharedPreferences sp;

    // State Variables
    public ArrayList<Word> words = new ArrayList<>();
    public ArrayList<Word> fiveWords = new ArrayList<>();
    public ArrayList<Word> fiveWordsCopy = new ArrayList<>();
    public ArrayList<Word> questionWords = new ArrayList<>();

    public int mistakes;
    public int lastMistake = 13;
    public int wordsPerSession;
    public int FIVE_WORD_SIZE;
    public int showCycle;
    public int quizCycle;
    public int languageId;
    public int repeatPerSession;
    public int totalCycle;
    public int totalMistakeCount;
    public int totalCorrects;
    public int cb;
    public boolean soundState;
    public String level;
    public int[] mistakeCollector;
    public boolean IsWrongAnswer = true;

    // LiveData for UI State
    private final MutableLiveData<TrainUiState> _uiState = new MutableLiveData<>(new TrainUiState());

    public NewTrainViewModel(@NonNull Application application) {
        super(application);
        repository = new WordRepository(application);
        sp = application.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        loadPreferences();
    }

    public LiveData<TrainUiState> getUiState() {
        return _uiState;
    }

    private void loadPreferences() {
        level = sp.getString("level", "NOTHING");
        languageId = sp.getInt("language", 0);
        soundState = sp.getBoolean("soundState", true);

        if (!sp.contains("totalWrongCount" + level)) {
            sp.edit().putInt("totalWrongCount" + level, 0).apply();
        } else {
            totalMistakeCount = sp.getInt("totalWrongCount" + level, 0);
            totalCorrects = sp.getInt("totalCorrects", 0);
            cb = sp.getInt("cb", 0);
        }

        wordsPerSession = sp.getInt("wordsPerSession", 5);
        repeatPerSession = sp.getInt("repeatationPerSession", 5);

        // Increment noshowads logic from Activity
        int noshowads = sp.getInt("noshowads", 0);
        noshowads++;
        sp.edit().putInt("noshowads", noshowads).apply();
    }

    public void initializingWords() {
        if (!sp.contains(level)) {
            sp.edit().putInt(level, 0).apply();
        }

        // Use Repository to fetch words
        List<Word> sessionWords = repository.fetchSessionWords(level, wordsPerSession);

        fiveWords.clear();
        fiveWords.addAll(sessionWords);

        // Update wordsPerSession if we got fewer words
        if (fiveWords.size() < wordsPerSession) {
            wordsPerSession = fiveWords.size();
        }

        sp.edit().putInt("fiveWordSize", fiveWords.size()).apply();
        FIVE_WORD_SIZE = fiveWords.size();

        mistakeCollector = new int[fiveWords.size()];
        Arrays.fill(mistakeCollector, 0);
    }

    public void getQuestionWords() {
        questionWords.clear();
        questionWords.addAll(repository.getAllUnlearnedWords(level));
    }

    public ArrayList<Word> gettingAnswer() {
        if (quizCycle == FIVE_WORD_SIZE) {
            quizCycle = 0;
        }

        if (questionWords.isEmpty()) {
            getQuestionWords();
        }

        ArrayList<Word> answers = new ArrayList<>();

        if (this.quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
            Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(questionWords);
            for (int i = 0; i < 4; i++) {
                answers.add(questionWords.get(i));
            }

            if (!answers.contains(word)) {
                answers.set(0, word);
            }

            Collections.shuffle(answers);
        }
        return answers;
    }

    public int getMostMistakenWord(int[] list) {
        int wordIndex = 0;
        int pos = -1;

        for (int i = 0; i < list.length; i++) {
            int current = list[i];
            if (current > wordIndex) {
                pos = i;
                wordIndex = current;
            }
        }
        return pos;
    }

    public void updateLearnedDatabase() {
        repository.updateLearnedStatus(fiveWords);
    }

    public void updateJustlearnedDatabase(int pos) {
        repository.updateJustLearnedStatus(level, fiveWords, pos);
    }

    public void saveProgress() {
        sp.edit().putInt("NTmistakes", mistakes).apply();
        sp.edit().putInt("totalWrongCount" + level, totalMistakeCount).apply();
        sp.edit().putInt("totalCorrects", totalCorrects).apply();
    }

    public void saveMostMistakenWord(int pos) {
        if (pos != -1) {
            String word = fiveWords.get(pos).getPronun();
            String def = fiveWords.get(pos).getTranslation();
            String spanish = fiveWords.get(pos).getExtra();
            String example = fiveWords.get(pos).getExample2();

            sp.edit().putString("MostMistakenWord", "shit" + "+" + word + "+" + def + "+" + spanish + "+" + example)
                    .apply();

        } else {
            sp.edit().putString("MostMistakenWord", "no").apply();
        }
    }

    public String checkTrialStatus() {
        String trialStatus = "ended";
        if (sp.contains("trial_end_date")) {
            Date today = Calendar.getInstance().getTime();
            long endMillies = sp.getLong("trial_end_date", 0);
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            if (leftMillies >= 0) {
                trialStatus = "active";
            } else {
                trialStatus = "ended";
            }
        }
        return trialStatus;
    }

    public boolean getIsAdShow() {
        boolean isAdShow = false;
        String trialStatus = checkTrialStatus();

        if (!sp.contains("premium")) {
            if (trialStatus.equalsIgnoreCase("ended")) {
                isAdShow = true;
            }
        }
        return isAdShow;
    }

    public String getUserName() {
        return sp.getString("userName", "Boo");
    }

    public String getSecondLanguage() {
        return sp.getString("secondlanguage", "english");
    }

    public void onNextClicked() {
        TrainUiState current = _uiState.getValue();
        if (current == null)
            return;

        // Hide speaker icon immediately
        current.isSpeakerVisible = false;
        _uiState.setValue(current);

        if (!current.isTimerRunning) {
            startTimer();
        }

        if (current.alreadyClicked) {
            current.shouldTriggerNextWord = true;
            current.alreadyClicked = false;
            _uiState.setValue(current);
        }
    }

    public void onNextWordTriggered() {
        TrainUiState current = _uiState.getValue();
        if (current == null)
            return;
        current.shouldTriggerNextWord = false;
        _uiState.setValue(current);
    }

    public void setVocabularySelectionShown(boolean shown) {
        TrainUiState current = _uiState.getValue();
        if (current == null)
            return;
        current.isVocabularySelectionShown = shown;
        _uiState.setValue(current);
    }

    private void startTimer() {
        TrainUiState current = _uiState.getValue();
        if (current == null)
            return;

        current.isTimerRunning = true;
        _uiState.setValue(current);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TrainUiState state = _uiState.getValue();
                if (state == null) {
                    cancel();
                    return;
                }

                state.progressCount++;
                _uiState.setValue(state);
            }

            @Override
            public void onFinish() {
                TrainUiState state = _uiState.getValue();
                if (state != null) {
                    state.progressCount = 0;
                    state.alreadyClicked = true;
                    state.isTimerRunning = false;
                    _uiState.setValue(state);
                }
            }
        }.start();
    }
}
