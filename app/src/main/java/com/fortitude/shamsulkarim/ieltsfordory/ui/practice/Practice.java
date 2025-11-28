package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityNewTrainBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.utility.tts.TtsController;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Practice extends AppCompatActivity
        implements View.OnClickListener, NewTrainRecyclerView.TrainAdapterCallback {

    private ActivityNewTrainBinding binding;
    private AppPreferences prefs;
    private PracticeViewModel viewModel;

    private RecyclerView.Adapter adapter;
    private TtsController ttsController;
    private boolean soundState = true;

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

        prefs = AppPreferences.get(this);
        viewModel = new ViewModelProvider(this).get(PracticeViewModel.class);

        initialization();

        // Observe State
        viewModel.uiState.observe(this, this::updateUi);

        soundState = prefs.getSoundState();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Practice.this);
                builder.setTitle("Do you want to leave this session, " + prefs.getUserName() + "?");
                builder.setMessage("Leaving this session will make you lose your progress");
                builder.setIcon(R.drawable.ic_leave);

                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Practice.this.startActivity(new Intent(Practice.this, MainActivity.class));
                    Practice.this.finish();
                });

                builder.setNegativeButton(android.R.string.no, null);

                AlertDialog dialog = builder.show();
                int primaryColor = getColor(R.color.colorPrimary);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor);
            }
        });
    }

    private void updateUi(PracticeUiState state) {
        // Update Progress
        binding.trainFab.setProgress(state.progressCount, true);
        binding.progress1.setProgress(state.quizCycle + state.showCycle);

        // Update Word Text and Options
        if (state.currentWord != null) {
            updateWordText(state.currentWord);
        }

        if (!state.currentOptions.isEmpty()) {
            int langId = prefs.getInt(AppPreferences.KEY_LANGUAGE, 0);
            binding.trainAnswerText1.setText(langId == 0 ? state.currentOptions.get(0).getTranslation()
                    : state.currentOptions.get(0).getExtra());
            binding.trainAnswerText2.setText(langId == 0 ? state.currentOptions.get(1).getTranslation()
                    : state.currentOptions.get(1).getExtra());
            binding.trainAnswerText3.setText(langId == 0 ? state.currentOptions.get(2).getTranslation()
                    : state.currentOptions.get(2).getExtra());
            binding.trainAnswerText4.setText(langId == 0 ? state.currentOptions.get(3).getTranslation()
                    : state.currentOptions.get(3).getExtra());
        }

        // Handle Visibility (Show vs Quiz)
        if (state.showCycle < viewModel.getFiveWordSize()) {
            // Showing Words Phase
            binding.trainRecyclerView.setVisibility(View.VISIBLE);
            binding.answerCard1.setVisibility(View.INVISIBLE);
            binding.answerCard2.setVisibility(View.INVISIBLE);
            binding.answerCard3.setVisibility(View.INVISIBLE);
            binding.answerCard4.setVisibility(View.INVISIBLE);

            // Only re-set adapter if word changed?
            // For simplicity, we can set it if it's not set or if we want to ensure it's
            // correct.
            // But creating new adapter every time might be heavy?
            // The original code created new adapter every time showWords was called.
            if (state.currentWord != null) {
                adapter = new NewTrainRecyclerView(this, state.currentWord, this);
                binding.trainRecyclerView.setAdapter(adapter);
            }

            // Trigger animation if needed? Original code did it on showWords call.
            // We can check if showCycle changed?
            // For now, let's assume the state update triggers this.
            DefExamAnimation();

        } else {
            // Quiz Phase
            binding.trainRecyclerView.setVisibility(View.INVISIBLE);
            binding.answerCard1.setVisibility(View.VISIBLE);
            binding.answerCard2.setVisibility(View.VISIBLE);
            binding.answerCard3.setVisibility(View.VISIBLE);
            binding.answerCard4.setVisibility(View.VISIBLE);

            // Animation for quiz start?
            if (state.quizCycle == 0 && state.showCycle == viewModel.getFiveWordSize()) {
                // answerCardAnimation(); // Maybe?
            }
        }

        // Handle Answer Status Side Effects
        if (state.lastAnswerStatus == 1) {
            // Correct
            applyCorrectColor();
            if (soundState) {
                MediaPlayer.create(this, R.raw.correct).start();
            }
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            answerCardAnimation2();
            viewModel.resetAnswerStatus();

        } else if (state.lastAnswerStatus == 2) {
            // Wrong
            applyWrongColor();
            if (soundState) {
                MediaPlayer.create(this, R.raw.incorrect).start();
            }
            wrongAnswerAnimation(state);
            viewModel.resetAnswerStatus();
        }

        // Check for completion
        if (viewModel.getTotalCycle() == (viewModel.getFiveWordSize() * viewModel.getRepeatPerSession())) {
            new Handler().postDelayed(() -> {
                hideViews();
                binding.spinAdLoading.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
                finish();
            }, 200L);
        }
    }

    private void updateWordText(Word word) {
        if (prefs.getSecondLanguage().equalsIgnoreCase("spanish")) {
            String combineBothLanguage = word.getWord() + "\n" + word.getWordSL();
            final ForegroundColorSpan lowColor = new ForegroundColorSpan(Color.parseColor("#8c979a"));
            SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
            spanWord.setSpan(lowColor, word.getWord().length(),
                    1 + word.getWordSL().length() + word.getWord().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanWord.setSpan(new RelativeSizeSpan(0.4f), word.getWord().length(),
                    1 + word.getWordSL().length() + word.getWord().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.trainWord.setText(spanWord);
        } else {
            binding.trainWord.setText(word.getWord());
        }
    }

    // ----------------------------------------------------------------------------------------------

    public void next(View v) {
        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        viewModel.onNextClicked();
    }

    @Override
    public void onClick(View v) {
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
            viewModel.onAnswerSelected(index);
        }
    }

    private void initialization() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.trainRecyclerView.setLayoutManager(layoutManager);
        binding.trainRecyclerView.setHasFixedSize(true);

        ttsController = new TtsController(this);

        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        binding.trainFab.setMax(5);

        binding.progress1.setProgressColor(getColor(R.color.colorPrimary));
        binding.progress1.setSecondaryProgressColor(getColor(R.color.colorPrimaryDark));
        binding.progress1.setProgressBackgroundColor(getColor(R.color.primary_text_color_white));
        binding.trainFab.setColorNormal(getColor(R.color.colorPrimary));
        binding.trainFab.setColorPressed(getColor(R.color.colorPrimaryDark));

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

        int maxProgress = 5 + (5 * 5); // Approximate, should get from ViewModel if possible
        binding.progress1.setMax(maxProgress);
        binding.progress1.setSecondaryProgress(5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsController != null) {
            ttsController.shutdown();
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

    private void wrongAnswerAnimation(PracticeUiState state) {
        binding.trainFab.animate().scaleX(1f).scaleY(1f).setDuration(350L)
                .setInterpolator(new AnticipateOvershootInterpolator());

        binding.trainRecyclerView.setVisibility(View.VISIBLE);
        binding.answerCard1.setVisibility(View.INVISIBLE);
        binding.answerCard2.setVisibility(View.INVISIBLE);
        binding.answerCard3.setVisibility(View.INVISIBLE);
        binding.answerCard4.setVisibility(View.INVISIBLE);
        binding.trainSpeakerIcon.setVisibility(View.INVISIBLE);
        binding.trainWord.setVisibility(View.VISIBLE);

        if (state.currentWord != null) {
            binding.trainWord.setText(state.currentWord.getWord());
            adapter = new NewTrainRecyclerView(this, state.currentWord, this);
            binding.trainRecyclerView.setAdapter(adapter);
        }

        DefExamAnimation();

        if (state.lastMistake == state.quizCycle) {
            Toast.makeText(this, "wrong answer again", Toast.LENGTH_LONG).show();
        } else {
            if (state.mistakes <= 3) {
                Toast.makeText(this, "Wrong Answer", Toast.LENGTH_LONG).show();
            }
            if (state.mistakes >= 4) {
                Toast.makeText(this, "Oh no! wrong answer", Toast.LENGTH_LONG).show();
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
        if (ttsController != null) {
            ttsController.speak(word, true);
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