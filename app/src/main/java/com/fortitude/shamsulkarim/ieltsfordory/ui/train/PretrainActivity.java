package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityStartTrainingBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.settings.SettingActivity;

import java.util.Objects;

public class PretrainActivity extends AppCompatActivity {

    private VocabularyRepository repository;
    private AppPreferences prefs;
    private ActivityStartTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityStartTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new VocabularyRepository(this);
        prefs = AppPreferences.get(this);

        initUIElement();
        setActivityTitle();
        checkSpanishState();
        checkTooEasyState();
    }

    private void setActivityTitle() {
        String level = prefs.getLevel();

        if (level.equalsIgnoreCase("beginner")) {
            binding.startTrainingLevelTextview.setText(getString(R.string.beginner));
            setBeginnerLearnedwordsLengthTextView();
        } else if (level.equalsIgnoreCase("intermediate")) {
            binding.startTrainingLevelTextview.setText(getString(R.string.intermediate));
            setIntermediateLearnedwordsLengthTextView();
        } else if (level.equalsIgnoreCase("advance")) {
            setAdvanceLearnedwordsLengthTextView();
            binding.startTrainingLevelTextview.setText(getString(R.string.advance));
        }
    }

    private void initUIElement() {
        if (prefs.isPremium()) {
            binding.purchaseCard.setVisibility(View.GONE);
        }

        if (BuildConfig.FLAVOR.equals("pro")) {
            binding.purchaseCard.setVisibility(View.GONE);
        } else {
            binding.purchaseCard.setVisibility(View.VISIBLE);
        }

        binding.purchaseButton.setOnClickListener(
                v -> Toast.makeText(this, "Currently upgrade is unavailable.", Toast.LENGTH_SHORT).show());

        binding.spanishSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (view == binding.spanishSwitch) {
                if (Objects.requireNonNull(prefs.getSecondLanguage()).equalsIgnoreCase("spanish")) {
                    prefs.setSecondLanguage("english");
                } else {
                    prefs.setSecondLanguage("spanish");
                }
            }
        });

        binding.tooEasySwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (view == binding.tooEasySwitch) {
                changeEasyWord(isChecked);
            }
        });

        binding.noWordHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewTrain.class);
            this.startActivity(intent);
            this.finish();
        });

        binding.startTrainingSettingsButton
                .setOnClickListener(v -> this.startActivity(new Intent(this, SettingActivity.class)));

        binding.startTrainingProgress.setProgressColor(getColor(R.color.colorPrimary));
        binding.startTrainingProgress.setProgressBackgroundColor(getColor(R.color.third_background_color));
    }

    // ------------------------------------------------------

    private void setBeginnerLearnedwordsLengthTextView() {
        int learnedCount = repository.getBeginnerLearnedCount();
        int totalBeginnerCount = repository.getTotalBeginnerCount();

        binding.startTrainingProgress.setMax(totalBeginnerCount);
        binding.startTrainingProgress.setProgress(learnedCount);
        binding.progressCountTextview
                .setText(getString(R.string.pretrain_progress_text, learnedCount, totalBeginnerCount));
    }

    private void setIntermediateLearnedwordsLengthTextView() {
        int i = repository.getIntermediateLearnedCount();
        int size = repository.getTotalIntermediateCount();

        binding.startTrainingProgress.setMax(size);
        binding.startTrainingProgress.setProgress(i);
        binding.progressCountTextview.setText(getString(R.string.pretrain_progress_text, i, size));
    }

    private void setAdvanceLearnedwordsLengthTextView() {
        int i = repository.getAdvanceLearnedCount();
        int size = repository.getTotalAdvanceCount();

        binding.startTrainingProgress.setMax(size);
        binding.startTrainingProgress.setProgress(i);
        binding.progressCountTextview.setText(getString(R.string.pretrain_progress_text, i, size));
    }

    private void checkSpanishState() {
        String secondLang = prefs.getSecondLanguage();
        assert secondLang != null;
        boolean spanishSwitchState;
        spanishSwitchState = secondLang.equalsIgnoreCase("spanish");
        binding.spanishSwitch.setChecked(spanishSwitchState);
    }

    private void checkTooEasyState() {
        boolean ieltsState = prefs.isIELTSActive();
        boolean toeflState = prefs.isTOEFLActive();
        binding.tooEasySwitch.setChecked(!ieltsState && !toeflState);
    }

    private void changeEasyWord(boolean switchState) {
        if (switchState) {
            prefs.setIELTSActive(false);
            prefs.setTOEFLActive(false);
        } else {
            prefs.setIELTSActive(true);
            prefs.setTOEFLActive(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_training_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.profile_menu_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
