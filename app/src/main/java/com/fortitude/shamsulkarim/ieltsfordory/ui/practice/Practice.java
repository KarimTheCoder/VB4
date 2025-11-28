package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityNewTrainBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Practice extends AppCompatActivity
        implements View.OnClickListener, TextToSpeech.OnInitListener, NewTrainRecyclerView.TrainAdapterCallback {

    private ActivityNewTrainBinding binding;
    private VocabularyRepository repository;
    private AppPreferences prefs;

    private List<Word> fiveWords;
    private RecyclerView.Adapter adapter;
    private int showCycle = 0;
    private int quizCycle = 0;
    private TextToSpeech tts;
    private int FIVE_WORD_SIZE = 0;
    private boolean IsWrongAnswer = true;
    private int lastMistake = 13;
    private int mistakes = 0;
    private int repeatPerSession = 5;
    private int totalCycle = 0;
    private int languageId;
    private boolean soundState = true;
    private int favoriteWrongs, totalCorrects;
    private int progressCount = 0;
    private boolean alreadyclicked = true;
    private boolean progress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityNewTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimary));

        repository = new VocabularyRepository(this);
        prefs = AppPreferences.get(this);

        initialization();
        addingNewWords();
        showWords(showCycle);

        prefs.setFavoriteWordCount(fiveWords.size());
        languageId = prefs.getInt(AppPreferences.KEY_LANGUAGE, 0);
        soundState = prefs.getSoundState();
        totalCorrects = prefs.getTotalCorrects();

        if (!prefs.contains(AppPreferences.KEY_FAVORITE_WRONGS)) {
            prefs.setFavoriteWrongs(0);
        } else {
            favoriteWrongs = prefs.getFavoriteWrongs();
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Practice.this);
                builder.setTitle("Do you want to leave this session, " + prefs.getUserName() + "?");
                builder.setMessage("Leaving this session will make you lose your progress");
                builder.setIcon(R.drawable.ic_leave);

                // 1. Positive Button (OK)
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Practice.this.startActivity(new Intent(Practice.this, MainActivity.class));
                        Practice.this.finish();
                    }
                });

                // 2. Negative Button (No)
                builder.setNegativeButton(android.R.string.no, null);

                // 3. Show the dialog
                AlertDialog dialog = builder.show();

                // Optional: Color the buttons to match your previous look
                int primaryColor = getColor(R.color.colorPrimary);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor);
            }
        });
    }

    // ----------------------------------------------------------------------------------------------

    public void next(View v) {
        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        Timer T = new Timer();

        if (!progress) {
            progress = true;
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressCount++;
                            binding.trainFab.setProgress(progressCount, true);

                            if (progressCount == 5) {
                                progressCount = 0;
                                alreadyclicked = true;
                                progress = false;
                                cancel();
                            }
                        }
                    });
                }
            }, 1000, 1000);
        }

        if (alreadyclicked) {
            if (showCycle >= FIVE_WORD_SIZE) {
                binding.answerCard1.setVisibility(View.VISIBLE);
                binding.answerCard2.setVisibility(View.VISIBLE);
                binding.answerCard3.setVisibility(View.VISIBLE);
                binding.answerCard4.setVisibility(View.VISIBLE);
                quizWords(quizCycle, v);
            }
            showWords(showCycle);
            alreadyclicked = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.answerCard1 || v == binding.answerCard2 || v == binding.answerCard3
                || v == binding.answerCard4) {
            if (quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
                quizWords(quizCycle, v);
            }
        }
    }

    // ---------- Showing Words

    private void showWords(int showCycle) {
        Handler handler = new Handler();

        if (showCycle < FIVE_WORD_SIZE) {
            binding.trainWord.setText(fiveWords.get(showCycle).getWord());

            if (prefs.getSecondLanguage().equalsIgnoreCase("spanish")) {
                String combineBothLanguage = fiveWords.get(showCycle).getWord() + "\n"
                        + fiveWords.get(showCycle).getWordSL();
                final ForegroundColorSpan lowColor = new ForegroundColorSpan(Color.parseColor("#8c979a"));
                SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);

                // Toast.makeText(this, "Word length: "+fiveWords.get(showCycle).getWordSL(),
                // Toast.LENGTH_LONG).show();
                spanWord.setSpan(lowColor, fiveWords.get(showCycle).getWord().length(),
                        1 + fiveWords.get(showCycle).getWordSL().length() + fiveWords.get(showCycle).getWord().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanWord.setSpan(new RelativeSizeSpan(0.4f), fiveWords.get(showCycle).getWord().length(),
                        1 + fiveWords.get(showCycle).getWordSL().length() + fiveWords.get(showCycle).getWord().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.trainWord.setText(spanWord);

            } else {
                binding.trainWord.setText(fiveWords.get(showCycle).getWord());
            }
            // wordViewMiddle.setText(fiveWords.get(showCycle).getWord());

            if (showCycle == 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DefExamAnimation();
                    }
                }, 200L);
            } else {
                DefExamAnimation();
            }

            adapter = new NewTrainRecyclerView(this, fiveWords.get(showCycle), this);
            binding.trainRecyclerView.setAdapter(adapter);
            this.showCycle++;
            binding.progress1.setProgress(quizCycle + showCycle);
        } else {
            binding.answerCard1.setVisibility(View.VISIBLE);
            binding.answerCard2.setVisibility(View.VISIBLE);
            binding.answerCard3.setVisibility(View.VISIBLE);
            binding.answerCard4.setVisibility(View.VISIBLE);
            quizWords(quizCycle, binding.trainFab);
        }
    }

    // --------- Quizzing

    private void quizWords(int quizCycle, View v) {
        if (quizCycle == 0) {
            answerCardAnimation();
        }
        if (showCycle >= FIVE_WORD_SIZE || quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
            // wordView.setVisibility(View.INVISIBLE);
            binding.answerCard1.setVisibility(View.VISIBLE);
            binding.answerCard2.setVisibility(View.VISIBLE);
            binding.answerCard3.setVisibility(View.VISIBLE);
            binding.answerCard4.setVisibility(View.VISIBLE);
            binding.trainRecyclerView.setVisibility(View.INVISIBLE);
            binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
            binding.trainFab.animate().scaleX(0f).scaleY(0f).setDuration(350L)
                    .setInterpolator(new AnticipateOvershootInterpolator());

            checkingAnswer(v);
            cycleQuiz();
        }
    }

    private void cycleQuiz() {
        if (this.quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
            ArrayList<Word> answers = gettingAnswer();
            if (!IsWrongAnswer) {
                IsWrongAnswer = true;
            } else {
                // wordViewMiddle.setVisibility(View.VISIBLE);
                binding.trainWord.setText(fiveWords.get(this.quizCycle).getWord());
            }

            if (languageId == 0) {
                binding.trainAnswerText1.setText(answers.get(0).getTranslation());
                binding.trainAnswerText2.setText(answers.get(1).getTranslation());
                binding.trainAnswerText3.setText(answers.get(2).getTranslation());
                binding.trainAnswerText4.setText(answers.get(3).getTranslation());
            } else {
                binding.trainAnswerText1.setText(answers.get(0).getExtra());
                binding.trainAnswerText2.setText(answers.get(1).getExtra());
                binding.trainAnswerText3.setText(answers.get(2).getExtra());
                binding.trainAnswerText4.setText(answers.get(3).getExtra());
            }
        }
    }

    private ArrayList<Word> gettingAnswer() {
        ArrayList<Word> answers = new ArrayList<>();
        List<Word> wordsAnswers = new ArrayList<>(fiveWords);
        if (quizCycle == FIVE_WORD_SIZE) {
            quizCycle = 0;
        }

        if (this.quizCycle <= (FIVE_WORD_SIZE * repeatPerSession) - 1) {
            Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(wordsAnswers);
            for (int i = 0; i < 4; i++) {
                answers.add(wordsAnswers.get(i));
            }

            if (!answers.contains(word)) {
                answers.set(0, word);
            }

            Collections.shuffle(answers);
        }
        return answers;
    }

    private void checkingAnswer(final View v) {
        MediaPlayer correctAudio = MediaPlayer.create(this, R.raw.correct);
        MediaPlayer incorrectAudio = MediaPlayer.create(this, R.raw.incorrect);

        if (this.quizCycle < (FIVE_WORD_SIZE * repeatPerSession)) {
            String answer;
            if (languageId == 0) {
                answer = fiveWords.get(quizCycle).getTranslation();
            } else {
                answer = fiveWords.get(quizCycle).getExtra();
            }

            if (v == binding.answerCard1) {
                if (binding.trainAnswerText1.getText().toString().equalsIgnoreCase(answer)) {
                    applyCorrectColor();
                    if (soundState) {
                        correctAudio.start();
                    }
                    totalCorrects++;

                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                } else {
                    applyWrongColor();
                    if (soundState) {
                        incorrectAudio.start();
                    }

                    // wordViewMiddle.setText("");
                    binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();

                    mistakes++;

                    if (lastMistake == quizCycle) {
                        Toast.makeText(this, "wrong answer again", Toast.LENGTH_LONG).show();
                    } else {
                        lastMistake = quizCycle;
                        if (mistakes <= 3) {
                            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_LONG).show();
                        }
                        if (mistakes >= 4) {
                            Toast.makeText(this, "Oh no! wrong answer", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            if (v == binding.answerCard2) {
                if (binding.trainAnswerText2.getText().toString().equalsIgnoreCase(answer)) {
                    applyCorrectColor();
                    if (soundState) {
                        correctAudio.start();
                    }
                    totalCorrects++;

                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                } else {
                    applyWrongColor();
                    if (soundState) {
                        incorrectAudio.start();
                    }
                    // wordViewMiddle.setText("");
                    binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();
                    mistakes++;

                    if (lastMistake == quizCycle) {
                        Toast.makeText(this, "wrong answer again", Toast.LENGTH_SHORT).show();
                    } else {
                        lastMistake = quizCycle;
                        if (mistakes <= 3) {
                            Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
                        }
                        if (mistakes >= 4) {
                            Toast.makeText(this, "Oh no! wrong answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            if (v == binding.answerCard3) {
                if (binding.trainAnswerText3.getText().toString().equalsIgnoreCase(answer)) {
                    applyCorrectColor();
                    if (soundState) {
                        correctAudio.start();
                    }
                    totalCorrects++;

                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                } else {
                    applyWrongColor();
                    if (soundState) {
                        incorrectAudio.start();
                    }
                    // wordViewMiddle.setText("");
                    binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();
                    mistakes++;

                    if (lastMistake == quizCycle) {
                        Toast.makeText(this, "Wrong answer again", Toast.LENGTH_SHORT).show();
                    } else {
                        lastMistake = quizCycle;
                        if (mistakes <= 3) {
                            Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
                        }
                        if (mistakes >= 4) {
                            Toast.makeText(this, "Oh no! wrong answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            if (v == binding.answerCard4) {
                if (binding.trainAnswerText4.getText().toString().equalsIgnoreCase(answer)) {
                    if (soundState) {
                        correctAudio.start();
                    }
                    applyCorrectColor();
                    totalCorrects++;

                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                    this.quizCycle++;
                    totalCycle++;
                    answerCardAnimation2();

                    cycleQuiz();

                } else {
                    applyWrongColor();

                    if (soundState) {
                        incorrectAudio.start();
                    }
                    // wordViewMiddle.setText("");
                    binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();
                    mistakes++;

                    if (lastMistake == quizCycle) {
                        Toast.makeText(this, "wrong answer again?", Toast.LENGTH_SHORT).show();
                    } else {
                        lastMistake = quizCycle;
                        if (mistakes <= 3) {
                            Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
                        }
                        if (mistakes >= 4) {
                            Toast.makeText(this, "Oh no! Wrong answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        if (quizCycle == FIVE_WORD_SIZE) {
            quizCycle = 0;
        }

        binding.progress1.setProgress(totalCycle + showCycle);
        prefs.setMistakeFavorite(mistakes);
        prefs.setFavoriteWrongs(mistakes + favoriteWrongs);
        prefs.setTotalCorrects(totalCorrects);

        if (totalCycle == (FIVE_WORD_SIZE * repeatPerSession)) {
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Todo
                    hideViews();
                    binding.spinAdLoading.setVisibility(View.VISIBLE);
                    // showInterstitialAd();

                    Practice.this.startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
                    Practice.this.finish();
                }
            }, 200L);
        }
    }

    // --------- Initializing

    private void initialization() {
        // topBackground = findViewById(R.id.top_background);
        // recyclerView = findViewById(R.id.train_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.trainRecyclerView.setLayoutManager(layoutManager);
        binding.trainRecyclerView.setHasFixedSize(true);

        // if true this will set showCycle to maximum and skip definition session

        prefs.getWordsPerSession();
        repeatPerSession = prefs.getRepeatationPerSession();

        tts = new TextToSpeech(this, this);

        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        // wordViewMiddle = (TextView)findViewById(R.id.train_word_middle);
        binding.trainFab.setMax(5);

        binding.progress1.setProgressColor(getColor(R.color.colorPrimary));
        binding.progress1.setSecondaryProgressColor(getColor(R.color.colorPrimaryDark));
        binding.progress1.setProgressBackgroundColor(getColor(R.color.primary_text_color_white));
        binding.trainFab.setColorNormal(getColor(R.color.colorPrimary));
        binding.trainFab.setColorPressed(getColor(R.color.colorPrimaryDark));
        fiveWords = new ArrayList<>();

        binding.answerCard1.setAlpha(0f);
        binding.answerCard2.setAlpha(0f);
        binding.answerCard3.setAlpha(0f);
        binding.answerCard4.setAlpha(0f);

        binding.answerCard1.setY(100f);
        binding.answerCard2.setY(100f);
        binding.answerCard3.setY(100f);
        binding.answerCard4.setY(100f);

        binding.answerCard1.setOnClickListener(this);
        binding.answerCard2.setOnClickListener(this);
        binding.answerCard3.setOnClickListener(this);
        binding.answerCard4.setOnClickListener(this);
        binding.trainSpeakerIcon.setOnClickListener(this);

        binding.answerCard1.setVisibility(View.INVISIBLE);
        binding.answerCard2.setVisibility(View.INVISIBLE);
        binding.answerCard3.setVisibility(View.INVISIBLE);
        binding.answerCard4.setVisibility(View.INVISIBLE);

        Sprite doubleBounce = new Wave();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);
        binding.spinKit.setVisibility(View.INVISIBLE);

        Sprite threeBounce = new ThreeBounce();
        binding.spinKit.setIndeterminateDrawable(threeBounce);
        binding.spinAdLoading.setVisibility(View.GONE);

        // wordViewMiddle.setVisibility(View.INVISIBLE);
        // wordView.setTypeface(comfortaRegular);
    }

    private void addingNewWords() {
        fiveWords = new ArrayList<>();
        String practice = prefs.getPracticeMode();

        if (practice.equalsIgnoreCase("favorite")) {
            fiveWords.addAll(repository.getFavoriteWords());
            for (int i = 0; i < fiveWords.size(); i++) {
                fiveWords.get(i).setSeen(false);
            }
            showCycle = fiveWords.size();
        }

        if (practice.equalsIgnoreCase("learned")) {
            getLearnedWords();
            for (int i = 0; i < fiveWords.size(); i++) {
                fiveWords.get(i).setSeen(false);
            }
        }

        prefs.setFavoriteWordCount(fiveWords.size());

        FIVE_WORD_SIZE = fiveWords.size();
        binding.progress1.setMax(FIVE_WORD_SIZE + (FIVE_WORD_SIZE * repeatPerSession));
        binding.progress1.setSecondaryProgress(FIVE_WORD_SIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    // ------- Animation -------------------------------
    private void answerCardAnimation() {
        binding.answerCard1.animate().translationY(0f).alpha(1f).setDuration(500L)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        binding.answerCard2.animate().translationY(0f).alpha(1f).setDuration(500L)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        binding.answerCard3.animate().translationY(0f).alpha(1f).setDuration(500L)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        binding.answerCard4.animate().translationY(0f).alpha(1f).setDuration(500L)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void answerCardAnimation2() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height, 0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                binding.answerCard1.setTranslationY(value / 10);
                binding.answerCard2.setTranslationY(value / 10);
                binding.answerCard3.setTranslationY(value / 10);
                binding.answerCard4.setTranslationY(value / 10);
            }
        });

        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();
    }

    private void wrongAnswerAnimation() {
        IsWrongAnswer = false;

        binding.trainFab.animate().scaleX(1f).scaleY(1f).setDuration(350L)
                .setInterpolator(new AnticipateOvershootInterpolator());

        binding.trainRecyclerView.setVisibility(View.VISIBLE);
        // wordViewMiddle.setText("");
        // wordViewMiddle.setVisibility(View.INVISIBLE);
        binding.answerCard1.setVisibility(View.INVISIBLE);
        binding.answerCard2.setVisibility(View.INVISIBLE);
        binding.answerCard3.setVisibility(View.INVISIBLE);
        binding.answerCard4.setVisibility(View.INVISIBLE);
        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        binding.trainWord.setVisibility(View.VISIBLE);

        binding.trainWord.setText(fiveWords.get(quizCycle).getWord());
        // wordViewMiddle.setText(fiveWords.get(quizCycle).getWord());

        DefExamAnimation();
        adapter = new NewTrainRecyclerView(this, fiveWords.get(quizCycle), this);
        binding.trainRecyclerView.setAdapter(adapter);
    }

    private void DefExamAnimation() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height, 0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                binding.trainRecyclerView.setTranslationY(value / 10);
            }
        });

        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int ttsLang = tts.setLanguage(Locale.US);

            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!");
                Toast.makeText(this,
                        "Please install English Language on your Text-to-Speech engine.\nSend us an email if you need help",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.i("TTS", "Language Supported.");
            }
            Log.i("TTS", "Initialization success.");
        } else {
            Log.e("TTS", "TTS not initialized");
            Toast.makeText(this,
                    "Please install Google Text-to-Speech on your phone. \nSend us an email if you need help",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void getLearnedWords() {
        String level = prefs.getLevel();
        prefs.getInt(AppPreferences.KEY_LANGUAGE, 0);
        prefs.getInt(level, 0);

        if (level.equalsIgnoreCase("beginner")) {
            fiveWords.addAll(repository.getBeginnerLearnedWords());
            // getBeginnerWordData();
        }

        if (level.equalsIgnoreCase("intermediate")) {
            fiveWords.addAll(repository.getIntermediateLearnedWords());
            // getIntermediateWordData();
        }

        if (level.equalsIgnoreCase("advance")) {
            fiveWords.addAll(repository.getAdvanceLearnedWords());
            // getAdvanceWordData();
        }
    }

    // ---------------------------------------

    private String checkTrialStatus() {
        String trialStatus = "ended";
        if (prefs.contains(AppPreferences.KEY_TRIAL_END_DATE)) {
            Date today = Calendar.getInstance().getTime();
            long endMillies = prefs.getLong(AppPreferences.KEY_TRIAL_END_DATE, 0);
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

    private void hideViews() {
        binding.topBackground.setVisibility(View.INVISIBLE);
        binding.trainRecyclerView.setVisibility(View.INVISIBLE);
        binding.answerCard1.setVisibility(View.INVISIBLE);
        binding.answerCard2.setVisibility(View.INVISIBLE);
        binding.answerCard3.setVisibility(View.INVISIBLE);
        binding.answerCard4.setVisibility(View.INVISIBLE);
        binding.progress1.setVisibility(View.INVISIBLE);
        binding.wordCard.setVisibility(View.INVISIBLE);
    }

    private boolean getIsAdShow() {
        boolean isAdShow = false;
        String trialStatus = checkTrialStatus();

        if (!prefs.contains(AppPreferences.KEY_PREMIUM)) {
            if (trialStatus.equalsIgnoreCase("ended")) {
                isAdShow = true;
            }
        }
        return isAdShow;
    }

    @Override
    public void onMethodCallback(String word) {
        if (tts != null) {
            tts.setLanguage(Locale.US);
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "TTS");
        }
        Toast.makeText(this, "Hello there, this is a callback", Toast.LENGTH_LONG).show();
    }

    private void applyWrongColor() {
        binding.topBackground.setBackgroundColor(getColor(R.color.red));
        binding.trainFab.setColorNormal(getColor(R.color.red));
        binding.progress1.setProgressColor(getColor(R.color.red));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.red));
        binding.trainCircle1.setBackground(ContextCompat.getDrawable(this, R.drawable.red_bottom_bar_dot));
        binding.trainCircle2.setBackground(ContextCompat.getDrawable(this, R.drawable.red_bottom_bar_dot));
        binding.trainCircle3.setBackground(ContextCompat.getDrawable(this, R.drawable.red_bottom_bar_dot));
        binding.trainCircle4.setBackground(ContextCompat.getDrawable(this, R.drawable.red_bottom_bar_dot));
    }

    private void applyCorrectColor() {
        binding.topBackground.setBackgroundColor(getColor(R.color.green));
        binding.trainFab.setColorNormal(getColor(R.color.green));
        binding.progress1.setProgressColor(getColor(R.color.green));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.green));
        binding.trainCircle1.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bottom_bar_dot));
        binding.trainCircle2.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bottom_bar_dot));
        binding.trainCircle3.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bottom_bar_dot));
        binding.trainCircle4.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bottom_bar_dot));
    }
}