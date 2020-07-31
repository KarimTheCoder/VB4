package com.fortitude.shamsulkarim.ieltsfordory.Practice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.crashlytics.android.Crashlytics;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;


import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class PracticeFinished extends AppCompatActivity implements View.OnClickListener{

    private FancyButton practiceAgain;
    private FloatingActionButton home;
    private TextView wordCountText;
    private int totalFavCount = 0;
    private int mistakes = 0;
    private int repeat;
    private CardView rateCard;
    private SharedPreferences sp;
    private int languageId;
    private int cb = 0;
    private PublisherInterstitialAd mPublisherInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_finished);

        // This code reports to Crashlytics of connection
        Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);
        Crashlytics.setBool("Connection Status",connected);

        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-7815894766256601/7917485135xxx");

        if(BuildConfig.FLAVOR.equalsIgnoreCase("free") || BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){
            mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());
        }




        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //  if( cb != 1){

                mPublisherInterstitialAd.show();
                // }

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });

        initialization();
        getCounts();
        getMostMistakenWord();

        cb = sp.getInt("cb", 0);



    }

    private void getCounts(){

        mistakes = sp.getInt("mistakeFavorite",0);
        totalFavCount = sp.getInt("favoriteWordCount",0);
        repeat = sp.getInt("repeatationPerSession",5);





    }
    private void initialization(){

        sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);;
        languageId = sp.getInt("language",0);
        rateCard = findViewById(R.id.rate_card);
        rateCard.setOnClickListener(this);

        home = (FloatingActionButton) findViewById(R.id.tf_home);
        home.setColorNormal(getResources().getColor(R.color.grey100));
        practiceAgain = (FancyButton)findViewById(R.id.tf_train_again);
        practiceAgain.setText("PRACTICE AGAIN");

        wordCountText = findViewById(R.id.practice_finished_word_text);
        wordCountText.setText(sp.getInt("favoriteWordCount",0)+" "+"words were reviewed");

        boolean soundState = sp.getBoolean("soundState",true);
        MediaPlayer mPlayer2= MediaPlayer.create(this, R.raw.train_finished);

        if(soundState){

            mPlayer2.start();
        }


        practiceAgain.setOnClickListener(this);
        home.setOnClickListener(this);




    }
    private void getMostMistakenWord(){
        List<String> deserialized = new ArrayList<>();

        String word = sp.getString("MostMistakenWord","no");
        StringBuilder sb = new StringBuilder(word);


        if(word.equalsIgnoreCase("no")){



        }else{
            deserialized = builderToNums(sb);

            if(languageId >0){

            }else {


            }



        }


    }

    private static List<String> builderToNums(StringBuilder numBuilder){
        List<String> backToNums = new ArrayList<>();
        int plusCount = 0;
        int plusI = 0;

        for(int i = 0; i < numBuilder.length(); i++){

            if(numBuilder.charAt(i) == '+'){

                plusCount++;
            }
        }

        int plusPosition[] = new int[plusCount];

        for(int j = 0; j < numBuilder.length(); j++){

            if(numBuilder.charAt(j) == '+'){
                plusPosition[plusI] = j;

                plusI++;

            }

        }

        for( int k = 0; k < plusPosition.length-1; k++){


            if(numBuilder.charAt(plusPosition[k]) == '+'){

                String strNum = numBuilder.substring(plusPosition[k]+1, plusPosition[k+1]);
                backToNums.add((strNum));
            }
        }


        if( numBuilder.length() > 0 && numBuilder != null){

            String lastNum = numBuilder.substring(plusPosition[plusCount-1]+1, numBuilder.length());
            backToNums.add(lastNum);

        }

        return backToNums;
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
