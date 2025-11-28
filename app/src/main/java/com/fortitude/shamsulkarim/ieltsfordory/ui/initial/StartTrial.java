package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.utils.DatabaseChecker;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityStartTrialBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class StartTrial extends AppCompatActivity {

    private ActivityStartTrialBinding binding;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartTrialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uiInitialization();
        codeInitialization();
        goHomeWhenPremium();

        DatabaseChecker databaseChecker = new DatabaseChecker(this);
        if (!databaseChecker.isDatabaseLoaded()) {

            startActivity(new Intent(this, AppLauncher.class));
            finish();

        }
    }

    private void initializeTrialMode() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7); // number of days to add
        Date endDate = c.getTime();

        if (!prefs.contains(AppPreferences.KEY_TRIAL_END_DATE)) {
            prefs.setLong(AppPreferences.KEY_TRIAL_END_DATE, endDate.getTime());
        }
    }

    private void codeInitialization() {
        prefs = AppPreferences.get(this);
    }

    private void uiInitialization() {
        prefs = AppPreferences.get(this);
        binding.startTrialButton.setOnClickListener(v -> {
            initializeTrialMode();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

    }

    private void goHomeWhenPremium() {

        if (prefs.isPremium()) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}