package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.TrainFinishedWordRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.LearningProgressRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityTrainFinishedBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainFinishedActivity extends AppCompatActivity {

    private String level;
    private List<Word> learnedWords;
    private Word mostMistakenWord;

    private LearningProgressRepository repository;
    private AppPreferences prefs;
    private ActivityTrainFinishedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityTrainFinishedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.primary_background_color));

        repository = new LearningProgressRepository(this);
        prefs = AppPreferences.get(this);

        initialization();
        setRecyclerView();
    }

    private void initialization() {
        level = prefs.getLevel();
        boolean soundState = prefs.getSoundState();

        learnedWords = new ArrayList<>();

        binding.trainFinishedFavorite.setOnClickListener(v -> setFavorite());
        binding.trainFinishedUnlearn.setOnClickListener(v -> setUnlearn());

        binding.mostMistakenCard.setVisibility(View.INVISIBLE);
        binding.mostMistakenImage.setVisibility(View.INVISIBLE);
        binding.mostMistakenText.setVisibility(View.INVISIBLE);

        MediaPlayer mPlayer2 = MediaPlayer.create(this, R.raw.train_finished);

        if (soundState) {
            mPlayer2.start();
        }

        binding.rateCard.setOnClickListener(v -> {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
                Uri appUrl = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            } else if (BuildConfig.FLAVOR.equalsIgnoreCase("huawei")) {
                Uri appUrl = Uri.parse(
                        "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            } else {
                Uri appUrl = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }
        });

        binding.tfHome.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.tfTrainAgain.setOnClickListener(v -> {
            this.startActivity(new Intent(this, NewTrain.class));
            finish();
        });
    }

    private void setRecyclerView() {
        gettingLearnedWords();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.trainFinishedRecyclerView.setLayoutManager(layoutManager);
        binding.trainFinishedRecyclerView.setHasFixedSize(true);

        RecyclerView.Adapter adapter = new TrainFinishedWordRecyclerView(this, learnedWords);
        binding.trainFinishedRecyclerView.setAdapter(adapter);
    }

    private void gettingLearnedWords() {
        LearningProgressRepository.JustLearnedSessionData data = repository.getJustLearnedWords(level);
        learnedWords.addAll(data.learnedWords);

        if (data.mostMistakenWord != null) {
            binding.mostMistakenCard.setVisibility(View.VISIBLE);
            binding.mostMistakenImage.setVisibility(View.VISIBLE);
            binding.mostMistakenText.setVisibility(View.VISIBLE);

            mostMistakenWord = data.mostMistakenWord;
            binding.trainFinishedWord.setText(mostMistakenWord.getWord());

            if ("True".equalsIgnoreCase(mostMistakenWord.isFavorite)) {
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
            }
        }

        binding.trainFinishedNewLearnedWordCountText
                .setText(getString(R.string.new_words_learned, learnedWords.size()));
    }

    private void setFavorite() {
        if (mostMistakenWord.isFavorite.equalsIgnoreCase("true")) {
            mostMistakenWord.setIsFavorite("false");
            repository.updateFavoriteStatus(mostMistakenWord, "false");
            binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon);
        } else {
            mostMistakenWord.setIsFavorite("true");
            binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
            repository.updateFavoriteStatus(mostMistakenWord, "true");
        }
    }

    private void setUnlearn() {
        if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {
            mostMistakenWord.setIsLearned("false");
            repository.updateLearnedStatus(mostMistakenWord, "false");
            binding.trainFinishedUnlearn.setText("Learn");
        } else {
            mostMistakenWord.setIsLearned("true");
            repository.updateLearnedStatus(mostMistakenWord, "true");
            binding.trainFinishedUnlearn.setText("Unlearn");
        }
    }
}
