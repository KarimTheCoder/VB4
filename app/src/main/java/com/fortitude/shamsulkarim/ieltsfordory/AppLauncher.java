package com.fortitude.shamsulkarim.ieltsfordory;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class AppLauncher extends AppCompatActivity {




    //in-app-purchase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this,R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);


        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        applyTheme(sp);


        if(sp.contains("home")) {

            if(!sp.contains("trial_end_date")){

                startActivity(new Intent(this, StartTrial.class));

            }else {

                startActivity(new Intent(this, MainActivity.class));
            }


        }else {

            this.startActivity(new Intent(this, SplashScreen.class));
        }
        finish();


    }


    private void applyTheme(SharedPreferences sp){

        int theme = sp.getInt("DarkMode",0);

        switch(theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

            default:
                // code block
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }



    }


}
