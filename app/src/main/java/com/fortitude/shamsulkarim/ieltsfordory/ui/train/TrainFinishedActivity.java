package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import com.fortitude.shamsulkarim.ieltsfordory.adapters.TrainFinishedWordRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityTrainFinishedBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainFinishedActivity extends AppCompatActivity {

    private String level;
    private List<Word> learnedWords;
    private Word mostMistakenWord;

    private VocabularyRepository repository;
    private JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;
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

        repository = new VocabularyRepository(this);
        prefs = AppPreferences.get(this);

        initialization();
        setRecyclerView();
    }

    private void initialization() {
        level = prefs.getLevel();
        boolean soundState = prefs.getSoundState();

        justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(this);
        justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(this);
        justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(this);

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
        if (level.equalsIgnoreCase("beginner")) {
            Cursor beginner = justLearnedDatabaseBeginner.getData();
            int newLearnedWordCountInt = 0;

            while (beginner.moveToNext()) {
                newLearnedWordCountInt++;

                Word word = new Word(beginner.getString(2) + "", "", "", "", "", "", "", "",
                        beginner.getString(10) + "", Integer.parseInt(beginner.getString(1)), "",
                        beginner.getString(12));

                if (beginner.getString(13).equalsIgnoreCase("false")) {
                    learnedWords.add(word);
                } else {
                    binding.mostMistakenCard.setVisibility(View.VISIBLE);
                    binding.mostMistakenImage.setVisibility(View.VISIBLE);
                    binding.mostMistakenText.setVisibility(View.VISIBLE);

                    mostMistakenWord = word;
                    binding.trainFinishedWord.setText(mostMistakenWord.getWord());

                    if (mostMistakenWord.isFavorite.equalsIgnoreCase("True")) {
                        binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    }
                }
            }
            binding.trainFinishedNewLearnedWordCountText
                    .setText(getString(R.string.new_words_learned, newLearnedWordCountInt));
        }

        if (level.equalsIgnoreCase("intermediate")) {
            Cursor intermediate = justLearnedDatabaseIntermediate.getData();
            int newLearnedWordCountInt = 0;

            while (intermediate.moveToNext()) {
                newLearnedWordCountInt++;

                Word word = new Word(intermediate.getString(2) + "", "", "", "", "", "", "", "",
                        intermediate.getString(10) + "", Integer.parseInt(intermediate.getString(1)), "True",
                        intermediate.getString(12));

                if (intermediate.getString(13).equalsIgnoreCase("false")) {
                    learnedWords.add(word);
                } else {
                    binding.mostMistakenCard.setVisibility(View.VISIBLE);
                    binding.mostMistakenImage.setVisibility(View.VISIBLE);
                    binding.mostMistakenText.setVisibility(View.VISIBLE);

                    mostMistakenWord = word;
                    binding.trainFinishedWord.setText(mostMistakenWord.getWord());

                    if (mostMistakenWord.isFavorite.equalsIgnoreCase("True")) {
                        binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    }
                }
            }
            binding.trainFinishedNewLearnedWordCountText
                    .setText(getString(R.string.new_words_learned, newLearnedWordCountInt));
        }

        if (level.equalsIgnoreCase("advance")) {
            int newLearnedWordCountInt = 0;
            Cursor advance = justLearnedDatabaseAdvance.getData();

            while (advance.moveToNext()) {
                newLearnedWordCountInt++;

                Word word = new Word(advance.getString(2) + "", "", "", "", "", "", "", "", advance.getString(10) + "",
                        Integer.parseInt(advance.getString(1)), "", advance.getString(12));

                if (advance.getString(13).equalsIgnoreCase("false")) {
                    learnedWords.add(word);
                } else {
                    binding.mostMistakenCard.setVisibility(View.VISIBLE);
                    binding.mostMistakenImage.setVisibility(View.VISIBLE);
                    binding.mostMistakenText.setVisibility(View.VISIBLE);
                    mostMistakenWord = word;
                    binding.trainFinishedWord.setText(mostMistakenWord.getWord());

                    if (mostMistakenWord.isFavorite.equalsIgnoreCase("True")) {
                        binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    }
                }
            }
            binding.trainFinishedNewLearnedWordCountText
                    .setText(getString(R.string.new_words_learned, newLearnedWordCountInt));
        }
    }

    private void setFavorite() {
        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")) {
            if (mostMistakenWord.isFavorite.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsFavorite("false");
                repository.updateIELTSFavoriteState(mostMistakenWord.position + "", "false");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon);
            } else {
                mostMistakenWord.setIsFavorite("true");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateIELTSFavoriteState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")) {
            if (mostMistakenWord.isFavorite.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsFavorite("false");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon);
                repository.updateTOEFLFavoriteState(mostMistakenWord.position + "", "false");
            } else {
                mostMistakenWord.setIsFavorite("true");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateTOEFLFavoriteState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")) {
            if (mostMistakenWord.isFavorite.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsFavorite("false");
                repository.updateSATFavoriteState(mostMistakenWord.position + "", "false");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon);
            } else {
                mostMistakenWord.setIsFavorite("true");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateSATFavoriteState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")) {
            if (mostMistakenWord.isFavorite.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsFavorite("false");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon);
                repository.updateGREFavoriteState(mostMistakenWord.position + "", "false");
            } else {
                mostMistakenWord.setIsFavorite("true");
                binding.trainFinishedFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateGREFavoriteState(mostMistakenWord.position + "", "true");
            }
        }
    }

    private void setUnlearn() {
        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")) {
            if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsLearned("false");
                repository.updateIELTSLearnState(mostMistakenWord.position + "", "false");
                binding.trainFinishedUnlearn.setText("Learn");
            } else {
                mostMistakenWord.setIsLearned("true");
                binding.trainFinishedUnlearn.setText("Unlearn");
                repository.updateIELTSLearnState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")) {
            if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsLearned("false");
                binding.trainFinishedUnlearn.setText("Learn");
                repository.updateTOEFLLearnState(mostMistakenWord.position + "", "false");
            } else {
                mostMistakenWord.setIsLearned("true");
                binding.trainFinishedUnlearn.setText("Unlearn");
                repository.updateTOEFLLearnState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")) {
            if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsLearned("false");
                repository.updateSATLearnState(mostMistakenWord.position + "", "false");
                binding.trainFinishedUnlearn.setText("Learn");
            } else {
                mostMistakenWord.setIsLearned("true");
                binding.trainFinishedUnlearn.setText("Unlearn");
                repository.updateSATLearnState(mostMistakenWord.position + "", "true");
            }
        }

        if (mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")) {
            if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {
                mostMistakenWord.setIsLearned("false");
                binding.trainFinishedUnlearn.setText("Learn");
                repository.updateGRELearnState(mostMistakenWord.position + "", "false");
            } else {
                mostMistakenWord.setIsLearned("true");
                binding.trainFinishedUnlearn.setText("Unlearn");
                repository.updateGRELearnState(mostMistakenWord.position + "", "true");
            }
        }
    }
}
