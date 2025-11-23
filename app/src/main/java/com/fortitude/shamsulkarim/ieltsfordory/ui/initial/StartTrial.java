package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.utils.DatabaseChecker;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;

public class StartTrial extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton startTrialButton;
    // Code
    private SharedPreferences sp;
    private AppPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trial);

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
        c.add(Calendar.DATE, 7);  // number of days to add
        Date endDate = c.getTime();

        if (!prefs.contains(AppPreferences.KEY_TRIAL_END_DATE)) {
            prefs.setLong(AppPreferences.KEY_TRIAL_END_DATE, endDate.getTime());
        }
    }

    private void codeInitialization() {
        sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        prefs = com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences.get(this);
    }

    private void uiInitialization() {
        prefs = AppPreferences.get(this);
        startTrialButton = findViewById(R.id.start_trial_button);
        startTrialButton.setOnClickListener(this);

    }

    private void goHomeWhenPremium() {

        if (prefs.isPremium()) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }

    @Override
    public void onClick(View view) {

        if (view == startTrialButton) {
            initializeTrialMode();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }
}