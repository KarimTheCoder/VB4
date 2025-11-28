package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityPracticeFinishedBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel.PracticeFinishedViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PracticeFinished extends AppCompatActivity {

    private ActivityPracticeFinishedBinding binding;
    private PracticeFinishedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityPracticeFinishedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.primary_background_color));

        viewModel = new ViewModelProvider(this).get(PracticeFinishedViewModel.class);

        initialization();
    }

    private void initialization() {
        binding.rateCard.setOnClickListener(v -> rateApp());

        binding.tfHome.setColorNormal(getColor(R.color.primary_text_color_white));
        binding.tfTrainAgain.setText("PRACTICE AGAIN");

        binding.practiceFinishedWordText
                .setText(getString(R.string.words_were_reviewed, viewModel.getFavoriteWordCount()));

        boolean soundState = viewModel.getSoundState();
        MediaPlayer mPlayer2 = MediaPlayer.create(this, R.raw.train_finished);

        if (soundState) {
            mPlayer2.start();
        }

        binding.tfTrainAgain.setOnClickListener(v -> {
            startActivity(new Intent(this, Practice.class));
            finish();
        });

        binding.tfHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void rateApp() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
            Uri appUrl = Uri
                    .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            startActivity(rateApp);
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase("huawei")) {
            Uri appUrl = Uri.parse(
                    "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            startActivity(rateApp);
        } else {
            Uri appUrl = Uri
                    .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            startActivity(rateApp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
