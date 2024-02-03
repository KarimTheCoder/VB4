package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

public class AppLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setNavigationBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);




        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        applyTheme(sp);


        if(sp.contains("home")) {

            if(!sp.contains("trial_end_date") && !BuildConfig.FLAVOR.equalsIgnoreCase("pro")){

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
