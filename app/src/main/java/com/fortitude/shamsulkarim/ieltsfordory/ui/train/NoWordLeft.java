package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityNoWordLeftBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

public class NoWordLeft extends AppCompatActivity {

    private AppPreferences prefs;
    private String level;
    private ActivityNoWordLeftBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityNoWordLeftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = AppPreferences.get(this);
        level = prefs.getString(AppPreferences.KEY_LEVEL, "beginner");

        initialization();
    }

    private void initialization() {
        Typeface comfortaRegular = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        Typeface comfoftaBold = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");

        binding.congratulation.setTypeface(comfoftaBold);
        binding.completedSection.setTypeface(comfortaRegular);
        binding.soProud.setTypeface(comfortaRegular);

        if (level.equalsIgnoreCase("beginner")) {
            binding.noWordHome.setTextColor(getColor(R.color.beginnerP));
            binding.reset.setTextColor(getColor(R.color.beginnerP));
            binding.congratulation.setTextColor(getColor(R.color.beginnerP));
            binding.noWordImage.setImageResource(R.drawable.beginner_no_words);
        } else if (level.equalsIgnoreCase("intermediate")) {
            binding.noWordHome.setTextColor(getColor(R.color.intermedateP));
            binding.reset.setTextColor(getColor(R.color.intermedateP));
            binding.congratulation.setTextColor(getColor(R.color.intermedateP));
            binding.noWordImage.setImageResource(R.drawable.intermediate_no_words);
        } else if (level.equalsIgnoreCase("advance")) {
            binding.noWordHome.setTextColor(getColor(R.color.advanceP));
            binding.reset.setTextColor(getColor(R.color.advanceP));
            binding.noWordImage.setImageResource(R.drawable.advance_no_words);
            binding.congratulation.setTextColor(getColor(R.color.advanceP));
        }

        binding.noWordHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        });

        binding.reset.setOnClickListener(v -> {
            prefs.setLevelProgress(level, 0);
            Intent intent = new Intent(this, PretrainActivity.class);
            this.startActivity(intent);
            this.finish();
        });
    }
}
