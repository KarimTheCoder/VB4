package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.DatabaseInitializer;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.TaskListener;
import com.fortitude.shamsulkarim.ieltsfordory.data.utils.DatabaseChecker;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;

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
        AppPreferences prefs = AppPreferences.get(this);

        DatabaseChecker db = new DatabaseChecker(this);


        if (db.isDatabaseLoaded()) {

            if (BuildConfig.FLAVOR.equalsIgnoreCase("pro") || prefs.isPremium()) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (!prefs.contains(AppPreferences.KEY_TRIAL_END_DATE)) {
                startActivity(new Intent(this, StartTrial.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

            finish();

        } else {

            this.startActivity(new Intent(this, SplashScreen.class));

            //createDatabase();
            finish();
        }


    }

    private void createDatabase() {
        DatabaseInitializer dbInitializer = new DatabaseInitializer(this, new TaskListener() {
            @Override
            public void onComplete() {

                if (BuildConfig.FLAVOR.equalsIgnoreCase("pro")) {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {

                    startActivity(new Intent(getApplicationContext(), StartTrial.class));
                    finish();
                }


            }

            @Override
            public void onProgress() {

                Toast.makeText(AppLauncher.this, "Please wait a moment", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Toast.makeText(AppLauncher.this, "Database loading failed", Toast.LENGTH_SHORT).show();

            }
        });
        dbInitializer.execute();
    }

    private void applyTheme(SharedPreferences sp) {

        int theme = sp.getInt("DarkMode", 0);

        switch (theme) {
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
