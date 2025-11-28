package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.DatabaseInitializer;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.TaskListener;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

public class SplashScreen extends AppCompatActivity {

    private TextView progressText;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setNavigationBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        progressText = findViewById(R.id.textView20);

        initialize();

        Handler handler = new Handler();
        handler.postDelayed(this::initializeOrMainActivity, 1000L);
    }

    private void initializeOrMainActivity() {
        createDatabase();
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
                progressText.setText("We are preparing the app...");
            }

            @Override
            public void onFailed() {
            }
        });
        dbInitializer.execute();
    }

    private void initialize() {
        prefs = AppPreferences.get(this);
        AppPreferences.migrateLegacy(this);

        if (!prefs.contains(AppPreferences.KEY_WORDS_PER_SESSION)) {
            prefs.setInt(AppPreferences.KEY_WORDS_PER_SESSION, 3);
        }
        if (!prefs.contains(AppPreferences.KEY_REPEATATION_PER_SESSION)) {
            prefs.setInt(AppPreferences.KEY_REPEATATION_PER_SESSION, 3);
        }
        // Default Dark themes
        setupAppTheme();

        ProgressBar progressBar = findViewById(R.id.spin_splash_screen);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        prefs.setString(AppPreferences.KEY_ADV_FAV, "");
        prefs.setString(AppPreferences.KEY_ADV_LEARNED, "0");
        prefs.setString(AppPreferences.KEY_BEG_FAV, "");
        prefs.setString(AppPreferences.KEY_BEG_LEARNED, "0");
        prefs.setString(AppPreferences.KEY_INT_FAV, "");
        prefs.setString(AppPreferences.KEY_INT_LEARNED, "0");

        if (!prefs.contains(AppPreferences.KEY_SKIP)) {
            prefs.setBool(AppPreferences.KEY_SKIP, false);
        }

        if (!prefs.contains(AppPreferences.KEY_FAVORITE_COUNT_PROFILE)) {
            prefs.setInt(AppPreferences.KEY_FAVORITE_COUNT_PROFILE, 0);
        }

        setupDefaultVocabulary();
        setupDefaultLanguage();
    }

    // Default settings
    private void setupAppTheme() {
        if (!prefs.contains(AppPreferences.KEY_DARK_MODE)) {
            prefs.setDarkMode(0);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            int darkMode = prefs.getDarkMode();

            switch (darkMode) {
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case 2:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private void setupDefaultVocabulary() {
        if (!prefs.contains(AppPreferences.KEY_HOME)) {
            prefs.setIELTSActive(true);
            prefs.setTOEFLActive(true);
            prefs.setSATActive(true);
            prefs.setGREActive(true);
        }
    }

    private void setupDefaultLanguage() {
        if (!prefs.contains(AppPreferences.KEY_HOME)) {
            prefs.setSecondLanguage("english");
        }
    }
}
