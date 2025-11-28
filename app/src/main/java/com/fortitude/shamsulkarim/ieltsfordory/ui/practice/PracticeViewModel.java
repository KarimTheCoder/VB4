package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PracticeViewModel extends AndroidViewModel {

    private final MutableLiveData<PracticeUiState> _uiState = new MutableLiveData<>(new PracticeUiState());
    public final LiveData<PracticeUiState> uiState = _uiState;

    private final VocabularyRepository repository;
    private final AppPreferences prefs;
    private final List<Word> fiveWords = new ArrayList<>();

    private int FIVE_WORD_SIZE = 0;
    private int repeatPerSession = 5;
    private int totalCycle = 0;
    private int languageId;
    private int favoriteWrongs, totalCorrects;

    private Timer timer;

    public PracticeViewModel(@NonNull Application application) {
        super(application);
        repository = new VocabularyRepository(application);
        prefs = AppPreferences.get(application);
        loadData();
    }

    private void loadData() {
        prefs.getWordsPerSession();
        repeatPerSession = prefs.getRepeatationPerSession();
        languageId = prefs.getInt(AppPreferences.KEY_LANGUAGE, 0);
        totalCorrects = prefs.getTotalCorrects();

        if (!prefs.contains(AppPreferences.KEY_FAVORITE_WRONGS)) {
            prefs.setFavoriteWrongs(0);
            favoriteWrongs = 0;
        } else {
            favoriteWrongs = prefs.getFavoriteWrongs();
        }

        addingNewWords();

        updateState(currentState -> {
            currentState.showCycle = 0;
            currentState.quizCycle = 0;
            currentState.mistakes = 0;
            currentState.lastMistake = 13;
            currentState.IsWrongAnswer = true;
            currentState.isTimerRunning = false;
            currentState.alreadyclicked = true;
            currentState.progressCount = 0;
            if (!fiveWords.isEmpty()) {
                currentState.currentWord = fiveWords.get(0);
            }
        });

        // If we start with quiz (e.g. favorite mode), generate options immediately
        if (_uiState.getValue().showCycle >= FIVE_WORD_SIZE) {
            generateOptions();
        }
    }

    private void addingNewWords() {
        fiveWords.clear();
        String practice = prefs.getPracticeMode();

        if (practice.equalsIgnoreCase("favorite")) {
            fiveWords.addAll(repository.getFavoriteWords());
            for (int i = 0; i < fiveWords.size(); i++) {
                fiveWords.get(i).setSeen(false);
            }
            int startCycle = fiveWords.size();
            updateState(s -> s.showCycle = startCycle);
        }

        if (practice.equalsIgnoreCase("learned")) {
            getLearnedWords();
            for (int i = 0; i < fiveWords.size(); i++) {
                fiveWords.get(i).setSeen(false);
            }
        }

        prefs.setFavoriteWordCount(fiveWords.size());
        FIVE_WORD_SIZE = fiveWords.size();
    }

    private void getLearnedWords() {
        String level = prefs.getLevel();
        if (level.equalsIgnoreCase("beginner")) {
            fiveWords.addAll(repository.getBeginnerLearnedWords());
        } else if (level.equalsIgnoreCase("intermediate")) {
            fiveWords.addAll(repository.getIntermediateLearnedWords());
        } else if (level.equalsIgnoreCase("advance")) {
            fiveWords.addAll(repository.getAdvanceLearnedWords());
        }
    }

    public void onNextClicked() {
        PracticeUiState current = _uiState.getValue();
        if (current == null)
            return;

        if (!current.isTimerRunning) {
            startTimer();
        }

        if (current.alreadyclicked) {
            updateState(s -> {
                s.alreadyclicked = false;
                if (s.showCycle < FIVE_WORD_SIZE) {
                    s.showCycle++;
                    if (s.showCycle < FIVE_WORD_SIZE) {
                        s.currentWord = fiveWords.get(s.showCycle);
                    }
                }
            });

            // If we just finished showing words, prepare for quiz
            if (_uiState.getValue().showCycle >= FIVE_WORD_SIZE) {
                generateOptions();
            }
        }
    }

    private void startTimer() {
        updateState(s -> s.isTimerRunning = true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    PracticeUiState current = _uiState.getValue();
                    if (current == null)
                        return;

                    int newProgress = current.progressCount + 1;
                    if (newProgress == 5) {
                        updateState(s -> {
                            s.progressCount = 0;
                            s.alreadyclicked = true;
                            s.isTimerRunning = false;
                        });
                        timer.cancel();
                    } else {
                        updateState(s -> s.progressCount = newProgress);
                    }
                });
            }
        }, 1000, 1000);
    }

    public void onAnswerSelected(int cardIndex) {
        PracticeUiState current = _uiState.getValue();
        if (current == null)
            return;

        if (current.quizCycle > (FIVE_WORD_SIZE * repeatPerSession) - 1)
            return;

        // If options are not generated or index out of bounds, return
        if (current.currentOptions.isEmpty() || cardIndex < 0 || cardIndex >= current.currentOptions.size())
            return;

        Word selectedWord = current.currentOptions.get(cardIndex);
        Word correctWord = fiveWords.get(current.quizCycle);

        String answer = (languageId == 0) ? correctWord.getTranslation() : correctWord.getExtra();
        String selected = (languageId == 0) ? selectedWord.getTranslation() : selectedWord.getExtra();

        if (selected.equalsIgnoreCase(answer)) {
            totalCorrects++;
            totalCycle++;
            updateState(s -> {
                s.lastAnswerStatus = 1; // Correct
                s.quizCycle++;
            });
            generateOptions();
        } else {
            updateState(s -> {
                s.lastAnswerStatus = 2; // Wrong
                s.mistakes++;
                if (s.lastMistake != s.quizCycle) {
                    s.lastMistake = s.quizCycle;
                }
                s.IsWrongAnswer = false;
            });
        }

        prefs.setMistakeFavorite(current.mistakes);
        prefs.setFavoriteWrongs(current.mistakes + favoriteWrongs);
        prefs.setTotalCorrects(totalCorrects);
    }

    public void resetAnswerStatus() {
        updateState(s -> s.lastAnswerStatus = 0);
    }

    private void generateOptions() {
        PracticeUiState current = _uiState.getValue();
        if (current == null)
            return;

        // Reset quiz cycle if needed (from original logic)
        if (current.quizCycle == FIVE_WORD_SIZE) {
            // current.quizCycle = 0; // Original logic had this check but it's confusing.
            // If quizCycle == FIVE_WORD_SIZE, it might mean we wrapped around?
            // Let's assume quizCycle grows monotonically until limit.
            // Actually, original code: if (quizCycle == FIVE_WORD_SIZE) quizCycle = 0;
            // This implies it loops through the 5 words multiple times?
            // Yes, repeatPerSession = 5. So it loops 5 times.
        }

        // We need to handle the looping of quizCycle index for accessing fiveWords
        int wordIndex = current.quizCycle % FIVE_WORD_SIZE;

        if (current.quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
            Word word = fiveWords.get(wordIndex);
            List<Word> options = new ArrayList<>(fiveWords);
            Collections.shuffle(options);

            List<Word> finalOptions = new ArrayList<>();
            for (int i = 0; i < 4 && i < options.size(); i++) {
                finalOptions.add(options.get(i));
            }

            if (!finalOptions.contains(word)) {
                finalOptions.set(0, word);
            }
            Collections.shuffle(finalOptions);

            updateState(s -> {
                s.currentOptions = finalOptions;
                s.currentWord = word;
            });
        }
    }

    private void updateState(StateUpdater updater) {
        PracticeUiState current = _uiState.getValue();
        if (current != null) {
            updater.update(current);
            _uiState.setValue(current);
        }
    }

    interface StateUpdater {
        void update(PracticeUiState state);
    }

    public int getFiveWordSize() {
        return FIVE_WORD_SIZE;
    }

    public int getRepeatPerSession() {
        return repeatPerSession;
    }

    public int getTotalCycle() {
        return totalCycle;
    }
}
