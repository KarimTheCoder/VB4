package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

/**
 * App entry point. Routes to appropriate screen based on user status.
 * Room database migration runs async in MyApplication.onCreate().
 */
public class AppLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup window decorations
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setNavigationBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        AppPreferences prefs = AppPreferences.get(this);
        applyTheme(prefs);

        // Navigate directly based on user status (no legacy DB check needed)
        if (BuildConfig.FLAVOR.equalsIgnoreCase("pro") || prefs.isPremium()) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (!prefs.contains(AppPreferences.KEY_TRIAL_END_DATE)) {
            startActivity(new Intent(this, StartTrial.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }

    private void applyTheme(AppPreferences prefs) {
        int theme = prefs.getDarkMode();

        switch (theme) {
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
