package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.button.MaterialButton;

public class PracticeFinished extends AppCompatActivity implements View.OnClickListener{

    private MaterialButton practiceAgain;
    private FloatingActionButton home;
    private CardView rateCard;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_practice_finished);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.primary_background_color));

        initialization();
        getCounts();
        getMostMistakenWord();

        sp.getInt("cb", 0);



    }

    private void getCounts(){

        sp.getInt("mistakeFavorite", 0);
        sp.getInt("favoriteWordCount", 0);
        sp.getInt("repeatationPerSession", 5);





    }
    private void initialization(){

        sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        sp.getInt("language", 0);
        rateCard = findViewById(R.id.rate_card);
        rateCard.setOnClickListener(this);

        home = findViewById(R.id.tf_home);
        home.setColorNormal(getColor(R.color.primary_text_color_white));
        practiceAgain = findViewById(R.id.tf_train_again);
        practiceAgain.setText("PRACTICE AGAIN");
        TextView wordCountText = findViewById(R.id.practice_finished_word_text);
        wordCountText.setText(getString(R.string.words_were_reviewed,sp.getInt("favoriteWordCount",0)));

        boolean soundState = sp.getBoolean("soundState",true);
        MediaPlayer mPlayer2= MediaPlayer.create(this, R.raw.train_finished);

        if(soundState){

            mPlayer2.start();
        }


        practiceAgain.setOnClickListener(this);
        home.setOnClickListener(this);




    }
    private void getMostMistakenWord(){
        sp.getString("MostMistakenWord","no");

    }

    @Override
    public void onClick(View v) {

        if(v == home){


            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

        if( v == practiceAgain ){

            this.startActivity(new Intent(this, Practice.class));
            this.finish();
        }



        if( v == rateCard){

            if(BuildConfig.FLAVOR.equalsIgnoreCase("free")){

                Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }else if(BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){

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

    }




}
