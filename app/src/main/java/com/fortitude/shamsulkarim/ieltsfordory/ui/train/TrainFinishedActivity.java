package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.TrainFinishedWordRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseIntermediate;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;

public class TrainFinishedActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView  words, mostMistenText, newLearnedWordCount;
    private ImageView mostMistakenImage;
    private FancyButton trainAgain, favoriteButton, unlearnButton;
    private FloatingActionButton home;
    private CardView mostMistakenCard,rate;
    private String level;
    private List<Word> learnedWords;
    private Word mostMistakenWord;

    private VocabularyRepository repository;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_train_finished);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.primary_background_color));

        repository = new VocabularyRepository(this);

        initialization();
        setRecyclerView();



    }







    private void initialization(){

        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        level = sp.getString("level", "beginner");
        boolean soundState = sp.getBoolean("soundState", true);

        justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(this);
        justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(this);
        justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(this);



        // Datalist
        learnedWords = new ArrayList<>();

        //----------------------------------


        words = findViewById(R.id.train_finished_word);
        mostMistakenCard = findViewById(R.id.most_mistaken_card);
        newLearnedWordCount = findViewById(R.id.train_finished_new_learned_word_count_text);



        //-------------------------

        favoriteButton = findViewById(R.id.train_finished_favorite);
        unlearnButton = findViewById(R.id.train_finished_unlearn);


        favoriteButton.setOnClickListener(this);
        unlearnButton.setOnClickListener(this);

        //-----Most mistaken

        mostMistenText = findViewById(R.id.most_mistaken_text);
        mostMistakenImage = findViewById(R.id.most_mistaken_image);
        mostMistakenCard = findViewById(R.id.most_mistaken_card);

        rate = findViewById(R.id.rate_card);


        mostMistakenCard.setVisibility(View.INVISIBLE);
        mostMistakenImage.setVisibility(View.INVISIBLE);
        mostMistenText.setVisibility(View.INVISIBLE);







        MediaPlayer mPlayer2 = MediaPlayer.create(this, R.raw.train_finished);


        if(soundState){
            mPlayer2.start();
        }

        home = findViewById(R.id.tf_home);
        trainAgain = findViewById(R.id.tf_train_again);



        rate.setOnClickListener(this);
        home.setOnClickListener(this);
        trainAgain.setOnClickListener(this);



    }











    private void setRecyclerView(){

        gettingLearnedWords();


        RecyclerView recyclerView = findViewById(R.id.train_finished_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        RecyclerView.Adapter adapter = new TrainFinishedWordRecyclerView(this, learnedWords);
        recyclerView.setAdapter(adapter);


    }

    private void gettingLearnedWords(){





        if(level.equalsIgnoreCase("beginner")){
            Cursor beginner = justLearnedDatabaseBeginner.getData();
            int newLearnedWordCountInt = 0;

            while (beginner.moveToNext()){
                newLearnedWordCountInt++;


                Word word  = new Word(beginner.getString(2)+"","","","","","","","",beginner.getString(10)+"",Integer.parseInt(beginner.getString(1)),"",beginner.getString(12));

                if(beginner.getString(13).equalsIgnoreCase("false")){

                    learnedWords.add(word);
                }else {
                    mostMistakenCard.setVisibility(View.VISIBLE);
                    mostMistakenImage.setVisibility(View.VISIBLE);
                    mostMistenText.setVisibility(View.VISIBLE);

                    mostMistakenWord = word;
                    words.setText(mostMistakenWord.getWord());

                    if(mostMistakenWord.isFavorite.equalsIgnoreCase("True")){

                        favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);

                    }


                }






            }
            newLearnedWordCount.setText(getString(R.string.new_words_learned,newLearnedWordCountInt));

        }

        if(level.equalsIgnoreCase("intermediate")){

            Cursor intermediate = justLearnedDatabaseIntermediate.getData();
            int newLearnedWordCountInt = 0;

            while (intermediate.moveToNext()){

                newLearnedWordCountInt++;

                Word word  = new Word(intermediate.getString(2)+"","","","","","","","",intermediate.getString(10)+"",Integer.parseInt(intermediate.getString(1)),"True",intermediate.getString(12));

                if(intermediate.getString(13).equalsIgnoreCase("false")){

                    learnedWords.add(word);
                }else {

                    mostMistakenCard.setVisibility(View.VISIBLE);
                    mostMistakenImage.setVisibility(View.VISIBLE);
                    mostMistenText.setVisibility(View.VISIBLE);

                    mostMistakenWord = word;
                    words.setText(mostMistakenWord.getWord());

                    if(mostMistakenWord.isFavorite.equalsIgnoreCase("True")){

                        favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);

                    }
                }

            }

            newLearnedWordCount.setText(getString(R.string.new_words_learned,newLearnedWordCountInt));

        }


        if(level.equalsIgnoreCase("advance")){
            int newLearnedWordCountInt = 0;

            Cursor advance = justLearnedDatabaseAdvance.getData();

            while (advance.moveToNext()){
                newLearnedWordCountInt++;

                Word word  = new Word(advance.getString(2)+"","","","","","","","",advance.getString(10)+"",Integer.parseInt(advance.getString(1)),"",advance.getString(12));

                if(advance.getString(13).equalsIgnoreCase("false")){

                    learnedWords.add(word);
                }else {

                    mostMistakenCard.setVisibility(View.VISIBLE);
                    mostMistakenImage.setVisibility(View.VISIBLE);
                    mostMistenText.setVisibility(View.VISIBLE);
                    mostMistakenWord = word;
                    words.setText(mostMistakenWord.getWord());

                    if(mostMistakenWord.isFavorite.equalsIgnoreCase("True")){

                        favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);

                    }

                }
            }

            newLearnedWordCount.setText(getString(R.string.new_words_learned,newLearnedWordCountInt));


        }





    }

    private void setFavorite(){

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                repository.updateIELTSFavoriteState(mostMistakenWord.position+"","false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);

            }else {

                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateIELTSFavoriteState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);
                repository.updateTOEFLFavoriteState(mostMistakenWord.position+"","false");

            }else {
                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);

                repository.updateTOEFLFavoriteState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                repository.updateSATFavoriteState(mostMistakenWord.position+"","false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);

            }else {
                mostMistakenWord.setIsFavorite("true");

                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateSATFavoriteState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);
                repository.updateGREFavoriteState(mostMistakenWord.position+"","false");

            }else {

                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                repository.updateGREFavoriteState(mostMistakenWord.position+"","true");
            }


        }


    }

    private void setUnlearn(){

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                repository.updateIELTSLearnState(mostMistakenWord.position+"","false");
                unlearnButton.setText("Learn");



            }else {

                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                repository.updateIELTSLearnState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                unlearnButton.setText("Learn");
                repository.updateTOEFLLearnState(mostMistakenWord.position+"","false");

            }else {
                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                repository.updateTOEFLLearnState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                repository.updateSATLearnState(mostMistakenWord.position+"","false");
                unlearnButton.setText("Learn");

            }else {
                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                repository.updateSATLearnState(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                unlearnButton.setText("Learn");
                repository.updateGRELearnState(mostMistakenWord.position+"","false");

            }else {

                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                repository.updateGRELearnState(mostMistakenWord.position+"","true");
            }


        }


    }


    @Override
    public void onClick(View v) {


        if( v == home){

            this.startActivity(new Intent(this, MainActivity.class));
            finish();


        }
        if ( v == trainAgain){

            this.startActivity(new Intent(this, NewTrain.class));
            finish();
        }


        if( v == rate){

            if(BuildConfig.FLAVOR.equalsIgnoreCase("free")){

                Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }
            else if(BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){

                Uri appUrl = Uri.parse("https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }


            else {

                Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }


        }

        if( v == favoriteButton){

            setFavorite();



        }

        if( v == unlearnButton){

            setUnlearn();
        }




    }




}
