package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.utils.DatabaseChecker;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;


public class StartTrial extends AppCompatActivity implements View.OnClickListener {

    //UI
    private MaterialButton startTrialButton;
    // Code
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trial);

        uiInitialization();
        codeInitialization();
        goHomeWhenPremium();

        DatabaseChecker databaseChecker = new DatabaseChecker(this);
        if(!databaseChecker.isDatabaseLoaded()){

            startActivity(new Intent(this, AppLauncher.class));

        }
    }


    private void initializeTrialMode(){


        // End Date
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);  // number of days to add
        Date endDate = c.getTime();

        // Debug
//        String endDateString = DateFormat.getDateInstance().format(endDate);
//        Toast.makeText(this,"End Date: "+endDateString,Toast.LENGTH_SHORT).show();

        // Save End Date
        if(!sp.contains("trial_end_date")){

            sp.edit().putLong("trial_end_date", endDate.getTime()).apply();
           // Toast.makeText(this,"Trial Mode Initialized", Toast.LENGTH_SHORT).show();

        }

    }


    private void codeInitialization(){

        sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

    }
    private void uiInitialization(){

        startTrialButton  = findViewById(R.id.start_trial_button);
        startTrialButton.setOnClickListener(this);

    }

    private void goHomeWhenPremium(){

        if(sp.contains("purchase")){

            initializeTrialMode(); // Why I have used this method here?
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }

    @Override
    public void onClick(View view) {

        if(view == startTrialButton){
            initializeTrialMode();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }
}