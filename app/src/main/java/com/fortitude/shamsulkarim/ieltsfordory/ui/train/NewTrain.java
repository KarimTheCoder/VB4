package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.AudioRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.FirebaseMediaRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityNewTrainBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.NewTrainViewModel;
import com.fortitude.shamsulkarim.ieltsfordory.utility.tts.TtsController;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NewTrain extends AppCompatActivity
        implements View.OnClickListener, NewTrainRecyclerView.TrainAdapterCallback {

    private ActivityNewTrainBinding binding;
    private NewTrainViewModel viewModel;

    public String[] items;
    public boolean[] checkedItems;
    public File localFile = null;
    public String audioPath = null;

    private TtsController ttsController;
    private NewTrainRecyclerView adapter;
    private AudioRepository audioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityNewTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NewTrainViewModel.class);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimary));

        ttsController = new TtsController(this);
        audioRepository = new FirebaseMediaRepository();

        initialization();
        viewModel.initializingWords();

        viewModel.getUiState().observe(this, state -> {
            binding.trainFab.setProgress(state.progressCount, true);

            if (state.isSpeakerVisible) {
                binding.trainSpeakerIcon.setVisibility(View.VISIBLE);
            } else {
                binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
            }

            if (state.shouldTriggerNextWord) {
                toNextWord(binding.trainFab);
                viewModel.onNextWordTriggered();
            }
        });

        showWords(viewModel.showCycle);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NewTrain.this);
                builder.setTitle("Do you want to leave this session, " + viewModel.getUserName() + "?");
                builder.setMessage("Leaving this session will make you lose your progress");
                builder.setIcon(R.drawable.ic_leave);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    NewTrain.this.startActivity(new Intent(NewTrain.this, MainActivity.class));
                    NewTrain.this.finish();
                });
                builder.setNegativeButton(android.R.string.no, null);
                AlertDialog dialog = builder.show();
                int primaryColor = getColor(R.color.colorPrimary);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsController != null) {
            ttsController.shutdown();
        }
    }

    public void next(View v) {
        viewModel.onNextClicked();
    }

    public void toNextWord(View view) {
        if (viewModel.showCycle >= viewModel.FIVE_WORD_SIZE) {

            // hide all the views after the learning stage
            // ------------------------------

            if (viewModel.getUiState().getValue() != null
                    && !viewModel.getUiState().getValue().isVocabularySelectionShown) {
                whichVocabularyToTest(view);
                viewModel.setVocabularySelectionShown(true);
            } else {

                binding.answerCard1.setVisibility(View.VISIBLE);
                binding.answerCard2.setVisibility(View.VISIBLE);
                binding.answerCard3.setVisibility(View.VISIBLE);
                binding.answerCard4.setVisibility(View.VISIBLE);
                binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);

                if (!viewModel.fiveWords.isEmpty()) {

                    if (viewModel.getSecondLanguage().equalsIgnoreCase("spanish")) {
                        String combineBothLanguage = viewModel.fiveWords.get(viewModel.quizCycle).getWord() + "\n"
                                + viewModel.fiveWords.get(viewModel.quizCycle).getWordSL();
                        final ForegroundColorSpan lowColor = new ForegroundColorSpan(
                                getColor(R.color.secondary_text_color));
                        SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
                        spanWord.setSpan(lowColor, viewModel.fiveWords.get(viewModel.quizCycle).getWord().length(),
                                1 + viewModel.fiveWords.get(viewModel.quizCycle).getWordSL().length()
                                        + viewModel.fiveWords.get(viewModel.quizCycle).getWord().length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanWord.setSpan(new RelativeSizeSpan(0.4f),
                                viewModel.fiveWords.get(viewModel.quizCycle).getWord().length(),
                                1 + viewModel.fiveWords.get(viewModel.quizCycle).getWordSL().length()
                                        + viewModel.fiveWords.get(viewModel.quizCycle).getWord().length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        binding.trainWord.setText(spanWord);

                    } else {
                        binding.trainWord.setText(viewModel.fiveWords.get(viewModel.quizCycle).getWord());
                    }

                }

                quizWords(viewModel.quizCycle, view);
            }

            // ------------------------------

        }

        showWords(viewModel.showCycle);

    }

    @Override
    public void onClick(View v) {

        if (v == binding.trainSpeakerIcon) {

            String word = binding.trainWord.getText().toString();
            if (viewModel.showCycle < viewModel.FIVE_WORD_SIZE + 1) {

                word = binding.trainWord.getText().toString();

                downloadAudio(word);

                if (viewModel.showCycle == viewModel.FIVE_WORD_SIZE) {
                    viewModel.showCycle++;
                }

            }

            downloadAudio(word);

        }

        if (v == binding.answerCard1 || v == binding.answerCard2 || v == binding.answerCard3
                || v == binding.answerCard4) {

            if (viewModel.quizCycle <= (viewModel.FIVE_WORD_SIZE * viewModel.repeatPerSession) - 1) {
                quizWords(viewModel.quizCycle, v);
            }

        }

    }

    private void showWords(int showCycle) {

        Handler handler = new Handler();

        if (showCycle < viewModel.FIVE_WORD_SIZE) {

            try {

                if (viewModel.getSecondLanguage().equalsIgnoreCase("spanish")) {

                    String combineBothLanguage = viewModel.fiveWords.get(showCycle).getWord() + "\n"
                            + viewModel.fiveWords.get(showCycle).getWordSL();
                    final ForegroundColorSpan lowColor = new ForegroundColorSpan(
                            getColor(R.color.secondary_text_color));
                    SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
                    spanWord.setSpan(lowColor, viewModel.fiveWords.get(showCycle).getWord().length(),
                            1 + viewModel.fiveWords.get(showCycle).getWordSL().length()
                                    + viewModel.fiveWords.get(showCycle).getWord().length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanWord.setSpan(new RelativeSizeSpan(0.6f), viewModel.fiveWords.get(showCycle).getWord().length(),
                            1 + viewModel.fiveWords.get(showCycle).getWordSL().length()
                                    + viewModel.fiveWords.get(showCycle).getWord().length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    binding.trainWord.setText(spanWord);
                } else {

                    binding.trainWord.setText(viewModel.fiveWords.get(showCycle).getWord());
                }

            } catch (NullPointerException i) {
                Log.i("Error", "Quiz Cycle: " + viewModel.quizCycle + " ShowCycle: " + showCycle);

            }

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

            adapter = new NewTrainRecyclerView(this, viewModel.fiveWords.get(showCycle), this);
            binding.trainRecyclerView.setAdapter(adapter);
            viewModel.showCycle++;
            binding.progress1.setProgress(viewModel.quizCycle + viewModel.showCycle);
        }

    }

    private void quizWords(int quizCycle, View v) {

        if (quizCycle == 0) {

            answerCardAnimation();
        }
        if (viewModel.showCycle >= viewModel.FIVE_WORD_SIZE
                || quizCycle <= (viewModel.FIVE_WORD_SIZE * viewModel.repeatPerSession) - 1) {

            // hideViews();
            binding.answerCard1.setVisibility(View.VISIBLE);
            binding.answerCard2.setVisibility(View.VISIBLE);
            binding.answerCard3.setVisibility(View.VISIBLE);
            binding.answerCard4.setVisibility(View.VISIBLE);
            binding.trainRecyclerView.setVisibility(View.INVISIBLE);
            binding.trainFab.animate().scaleX(0f).scaleY(0f).setDuration(350L)
                    .setInterpolator(new AnticipateOvershootInterpolator());

            checkingAnswer(v);
            cycleQuiz();
        }

    }

    private void initialization() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.trainRecyclerView.setLayoutManager(layoutManager);
        binding.trainRecyclerView.setHasFixedSize(true);

        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);

        Sprite doubleBounce = new Wave();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);
        binding.spinKit.setVisibility(View.INVISIBLE);

        Sprite threeBounce = new ThreeBounce();
        binding.spinAdLoading.setIndeterminateDrawable(threeBounce);
        binding.spinAdLoading.setVisibility(View.GONE);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();

        @ColorInt
        int colorPrimarySurface = typedValue.data;

        binding.trainFab.setMax(5);

        binding.progress1.setProgressColor(getColor(R.color.colorPrimary));
        binding.progress1.setSecondaryProgressColor(getColor(R.color.colorPrimaryDark));
        binding.progress1.setProgressBackgroundColor(getColor(R.color.primary_text_color_white));

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

    }

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

    private void cycleQuiz() {

        if (viewModel.quizCycle <= (viewModel.FIVE_WORD_SIZE * viewModel.repeatPerSession) - 1) {

            ArrayList<Word> answers = viewModel.gettingAnswer();
            if (!viewModel.IsWrongAnswer) {
                viewModel.IsWrongAnswer = true;

            }

            if (viewModel.languageId == 0) {

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

    private void checkingAnswer(final View v) {
        if (viewModel.quizCycle < (viewModel.FIVE_WORD_SIZE * viewModel.repeatPerSession)) {
            int index = -1;
            if (v == binding.answerCard1)
                index = 0;
            else if (v == binding.answerCard2)
                index = 1;
            else if (v == binding.answerCard3)
                index = 2;
            else if (v == binding.answerCard4)
                index = 3;

            if (index != -1) {
                handleUserSelection(index, v);
            }
        }

        binding.progress1.setProgress(viewModel.totalCycle + viewModel.showCycle);

        if (viewModel.quizCycle == viewModel.FIVE_WORD_SIZE) {
            viewModel.quizCycle = 0;
        }

        binding.trainWord.setText(viewModel.fiveWords.get(viewModel.quizCycle).getWord());

        if (viewModel.totalCycle == (viewModel.FIVE_WORD_SIZE * viewModel.repeatPerSession)) {

            int pos = viewModel.getMostMistakenWord(viewModel.mistakeCollector);
            viewModel.fiveWords.clear();
            viewModel.fiveWords.addAll(viewModel.fiveWordsCopy);
            viewModel.updateLearnedDatabase();
            viewModel.updateJustlearnedDatabase(pos);

            viewModel.saveProgress();
            viewModel.saveMostMistakenWord(pos);

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    hideViews();
                    binding.spinAdLoading.setVisibility(View.VISIBLE);
                    showInterstitialAd();

                    NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                    NewTrain.this.finish();
                }
            }, 200L);

        }

    }

    private void handleUserSelection(int index, View cardView) {
        MediaPlayer correctAudio = MediaPlayer.create(this, R.raw.correct);
        MediaPlayer incorrectAudio = MediaPlayer.create(this, R.raw.incorrect);

        String answer;
        if (viewModel.languageId == 0) {
            answer = viewModel.fiveWords.get(viewModel.quizCycle).getTranslation();
        } else {
            answer = viewModel.fiveWords.get(viewModel.quizCycle).getExtra();
        }

        android.widget.TextView selectedTextView = null;
        switch (index) {
            case 0:
                selectedTextView = binding.trainAnswerText1;
                break;
            case 1:
                selectedTextView = binding.trainAnswerText2;
                break;
            case 2:
                selectedTextView = binding.trainAnswerText3;
                break;
            case 3:
                selectedTextView = binding.trainAnswerText4;
                break;
        }

        if (selectedTextView != null) {
            if (selectedTextView.getText().toString().equalsIgnoreCase(answer)) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

                applyCorrectColor();
                viewModel.quizCycle++;
                viewModel.totalCycle++;

                if (viewModel.soundState) {
                    correctAudio.start();
                }
                viewModel.totalCorrects++;

                answerCardAnimation2();

                if (index == 3) {
                    cycleQuiz();
                }

            } else {
                applyWrongColor();

                if (viewModel.soundState) {
                    incorrectAudio.start();
                }

                viewModel.mistakeCollector[viewModel.quizCycle] = viewModel.mistakeCollector[viewModel.quizCycle] + 1;

                wrongAnswerAnimation();

                viewModel.mistakes++;
                viewModel.totalMistakeCount++;

                if (viewModel.lastMistake == viewModel.quizCycle) {
                    String[] msgs = { "wrong answer again", "wrong answer again", "Wrong answer again",
                            "wrong answer again?" };
                    Toast.makeText(this, msgs[index], Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.lastMistake = viewModel.quizCycle;
                    if (viewModel.mistakes <= 3) {
                        String[] msgs = { "Wrong answer", "Wrong answer!", "Wrong answer!", "Wrong answer" };
                        Toast.makeText(this, msgs[index], Toast.LENGTH_SHORT).show();
                    }
                    if (viewModel.mistakes >= 4) {
                        String[] msgs = { "Oh no! wrong answer", "Oh no! wrong answer", "Oh no! wrong answer",
                                "Oh no! Wrong answer" };
                        Toast.makeText(this, msgs[index], Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void DefExamAnimation() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height, 0);

        va.addUpdateListener(valueAnimator -> {

            float value = (float) valueAnimator.getAnimatedValue();
            binding.trainRecyclerView.setTranslationY(value / 10);
        });

        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();

    }

    private void answerCardAnimation2() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height, 0);

        va.addUpdateListener(valueAnimator -> {

            float value = (float) valueAnimator.getAnimatedValue();
            binding.answerCard1.setTranslationY(value / 10);
            binding.answerCard2.setTranslationY(value / 10);
            binding.answerCard3.setTranslationY(value / 10);
            binding.answerCard4.setTranslationY(value / 10);
        });

        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();

    }

    private void wrongAnswerAnimation() {
        viewModel.IsWrongAnswer = false;

        binding.trainFab.animate().scaleX(1f).scaleY(1f).setDuration(350L)
                .setInterpolator(new AnticipateOvershootInterpolator());

        binding.trainRecyclerView.setVisibility(View.VISIBLE);

        binding.answerCard1.setVisibility(View.INVISIBLE);
        binding.answerCard2.setVisibility(View.INVISIBLE);
        binding.answerCard3.setVisibility(View.INVISIBLE);
        binding.answerCard4.setVisibility(View.INVISIBLE);
        binding.trainWord.setVisibility(View.VISIBLE);
        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);

        binding.trainWord.setText(viewModel.fiveWords.get(viewModel.quizCycle).getWord());

        DefExamAnimation();
        adapter = new NewTrainRecyclerView(this, viewModel.fiveWords.get(viewModel.quizCycle), this);
        binding.trainRecyclerView.setAdapter(adapter);

    }

    private void showInterstitialAd() {

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

    private void whichVocabularyToTest(View v) {

        final View view = v;
        viewModel.fiveWordsCopy.addAll(viewModel.fiveWords);

        final ArrayList<Word> userSelectedWords = new ArrayList<>(viewModel.fiveWords);
        items = new String[viewModel.fiveWords.size()];
        checkedItems = new boolean[viewModel.fiveWords.size()];

        for (int i = 0; i < viewModel.fiveWords.size(); i++) {

            items[i] = viewModel.fiveWords.get(i).getWord();
            checkedItems[i] = false;

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Which vocabularies do you want to test?");

        // this will checked the items when user open the dialog
        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("Done", (dialog, which) -> {

            viewModel.fiveWords.clear();

            for (int i = 0; i < checkedItems.length; i++) {

                if (checkedItems[i]) {
                    viewModel.fiveWords.add(userSelectedWords.get(i));
                }
            }

            if (!viewModel.fiveWords.isEmpty()) {

                binding.progress1.setMax(
                        userSelectedWords.size() + (viewModel.fiveWords.size() * viewModel.repeatPerSession));
                binding.trainWord.setText(viewModel.fiveWords.get(viewModel.quizCycle).getWord());
                quizWords(viewModel.quizCycle, view);
            } else {

                viewModel.fiveWords.addAll(userSelectedWords);
                viewModel.updateJustlearnedDatabase(-1);
                viewModel.updateLearnedDatabase();

                Handler handler = new Handler();

                handler.postDelayed(() -> {

                    NewTrain.this
                            .startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                    NewTrain.this.finish();
                }, 200L);
            }

            viewModel.FIVE_WORD_SIZE = viewModel.fiveWords.size();

            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void downloadAudio(String wordName) {

        binding.spinKit.setVisibility(View.VISIBLE);
        binding.trainSpeakerIcon.setEnabled(false);

        audioRepository.downloadAudio(wordName, new AudioRepository.Callback() {
            @Override
            public void onAudioReady(File audioFile) {
                binding.spinKit.setVisibility(View.INVISIBLE);
                audioPath = audioFile.getAbsolutePath();

                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(audioPath);
                    mp.prepare();
                    mp.start();
                    // Keep disabled while playing
                    binding.trainSpeakerIcon.setEnabled(false);
                    mp.setOnCompletionListener(mp1 -> binding.trainSpeakerIcon.setEnabled(true));
                } catch (IOException e) {
                    e.printStackTrace();
                    binding.trainSpeakerIcon.setEnabled(true);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                binding.spinKit.setVisibility(View.INVISIBLE);
                binding.trainSpeakerIcon.setEnabled(true);
            }
        });

    }

    @Override
    public void onMethodCallback(String word) {
        if (ttsController != null) {
            ttsController.speak(word, true);
        }
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