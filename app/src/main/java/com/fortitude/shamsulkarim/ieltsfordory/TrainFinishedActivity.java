package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.TrainFinishedWordRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;

public class TrainFinishedActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView  words, mostMistenText, newLearnedWordCount;


    private ImageView mostMistakenImage;
    private FancyButton trainAgain, favoriteButton, unlearnButton;
    private FloatingActionButton home;
    private CardView mostMistakenCard,rate;
    private String level, toastMsg;
    private Boolean soundState = true;
    private SharedPreferences sp;
    private List<Word> learnedWords;
    private Word mostMistakenWord;
    private List<String> word, databasePosition, vocabularyType, favorite;
    private ConstraintLayout rate_card_layout;



    //  private PublisherInterstitialAd mPublisherInterstitialAd;
    //

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // firebase
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private StringBuilder beginnerFavNumBuilder, intermediateFavNumBuilder, advanceFavNumBuilder;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private PublisherInterstitialAd mPublisherInterstitialAd;
    private JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;
    private boolean connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_train_finished);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.primary_background_color));

        // This code reports to Crashlytics of connection
        Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);


        //Toast.makeText(this,"SharedLearned "+sharedLearned+" level "+level+" fivewordsize "+fiveWordSize,Toast.LENGTH_SHORT).show();

//        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
//        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-7815894766256601/7917485135xxx");
//
//        if(BuildConfig.FLAVOR.equalsIgnoreCase("free") || BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){
//            mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());
//        }
//
//
//
//
//
//                mPublisherInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//              //  if( cb != 1){
//
//                    mPublisherInterstitialAd.show();
//               // }
//
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//            }
//        });
        initialization();
        stylize();
        setRecyclerView();

        UpdateCrashlyticsLearnedWordCount();



    }







    private void initialization(){

        try{

            // Checking Network Connection
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else{
                connected = false;
            }


        }catch (NullPointerException e){


        }


        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        level = sp.getString("level", "beginner");
        soundState = sp.getBoolean("soundState",true);

        // Databases
        ieltsWordDatabase = new IELTSWordDatabase(this);
        satWordDatabase = new SATWordDatabase(this);
        toeflWordDatabase = new TOEFLWordDatabase(this);
        greWordDatabase = new GREWordDatabase(this);

        justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(this);
        justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(this);
        justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(this);



        // Datalist
        learnedWords = new ArrayList<>();
        word = new ArrayList<>();
        databasePosition = new ArrayList<>();
        vocabularyType = new ArrayList<>();

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





        // firebase

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        user = firebaseAuth.getCurrentUser();
        beginnerFavNumBuilder = new StringBuilder();
        intermediateFavNumBuilder = new StringBuilder();
        advanceFavNumBuilder  = new StringBuilder();

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

    private void stylize(){


//        home.setColorPressed(getResources().getColor(R.color.colorPrimary));
//        home.setColorNormal(getResources().getColor(R.color.grey100));
//        trainAgain.setFocusBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//        trainAgain.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        trainAgain.setTextColor(getResources().getColor(R.color.grey100));

    }










    private void setRecyclerView(){

        gettingLearnedWords();


        recyclerView = (RecyclerView)findViewById(R.id.train_finished_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new TrainFinishedWordRecyclerView(this,learnedWords);
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
            newLearnedWordCount.setText(newLearnedWordCountInt+" new words learned!");


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

            newLearnedWordCount.setText(newLearnedWordCountInt+" new words learned!");


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

            newLearnedWordCount.setText(newLearnedWordCountInt+" new words learned!");


        }





    }

    private void setFavorite(){

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                ieltsWordDatabase.updateFav(mostMistakenWord.position+"","false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);

            }else {

                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                ieltsWordDatabase.updateFav(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);
                toeflWordDatabase.updateFav(mostMistakenWord.position+"","false");

            }else {
                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);

                toeflWordDatabase.updateFav(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                satWordDatabase.updateFav(mostMistakenWord.position+"","false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);

            }else {
                mostMistakenWord.setIsFavorite("true");

                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                satWordDatabase.updateFav(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")){

            if(mostMistakenWord.isFavorite.equalsIgnoreCase("true")){

                mostMistakenWord.setIsFavorite("false");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon);
                greWordDatabase.updateFav(mostMistakenWord.position+"","false");

            }else {

                mostMistakenWord.setIsFavorite("true");
                favoriteButton.setIconResource(R.drawable.ic_favorite_icon_active);
                greWordDatabase.updateFav(mostMistakenWord.position+"","true");
            }


        }


    }

    private void setUnlearn(){

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                ieltsWordDatabase.updateLearned(mostMistakenWord.position+"","false");
                unlearnButton.setText("Learn");



            }else {

                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                ieltsWordDatabase.updateLearned(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                unlearnButton.setText("Learn");
                toeflWordDatabase.updateLearned(mostMistakenWord.position+"","false");

            }else {
                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                toeflWordDatabase.updateLearned(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                satWordDatabase.updateLearned(mostMistakenWord.position+"","false");
                unlearnButton.setText("Learn");

            }else {
                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                satWordDatabase.updateLearned(mostMistakenWord.position+"","true");
            }


        }

        if(mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")){

            if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                mostMistakenWord.setIsLearned("false");
                unlearnButton.setText("Learn");
                greWordDatabase.updateLearned(mostMistakenWord.position+"","false");

            }else {

                mostMistakenWord.setIsLearned("true");
                unlearnButton.setText("Unlearn");
                greWordDatabase.updateLearned(mostMistakenWord.position+"","true");
            }


        }


    }



    // Updating Firebase Methods

    private void updateFirebase(){
        addFavNumber();
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String advanceLearnedNum = String.valueOf(sp.getInt("advance",0));
        String beginnerLearnedNum = String.valueOf(sp.getInt("beginner",0));
        String intermediateLearnedNum = String.valueOf(sp.getInt("intermediate",0));

        String advanceFavNumString = String.valueOf(advanceFavNumBuilder);
        String intermediateNumString = String.valueOf(intermediateFavNumBuilder);
        String beginnerNumString  = String.valueOf(beginnerFavNumBuilder);




        if( currentUser != null && connected){
            FavLearnedState favLearnedState = new FavLearnedState(sp.getString("userName",currentUser.getDisplayName()),beginnerLearnedNum,intermediateLearnedNum,advanceLearnedNum,"",beginnerNumString,intermediateNumString,advanceFavNumString,"");
            ref.child(user.getUid()).setValue(favLearnedState);
        }

    }

    private void addFavNumber(){

        Cursor beginner = ieltsWordDatabase.getData();
        Cursor intermediate = toeflWordDatabase.getData();
        Cursor advance = satWordDatabase.getData();
        beginnerFavNumBuilder = new StringBuilder();
        intermediateFavNumBuilder = new StringBuilder();
        advanceFavNumBuilder = new StringBuilder();



        while (beginner.moveToNext()){

            if(beginner.getString(2).equalsIgnoreCase("true")){
                beginnerFavNumBuilder.append("+"+beginner.getString(1));
            }
        }
        beginner.close();

        while (intermediate.moveToNext()){

            if(intermediate.getString(2).equalsIgnoreCase("true")){
                intermediateFavNumBuilder.append("+"+intermediate.getString(1));
            }
        }
        beginner.close();

        while (advance.moveToNext()){

            if(advance.getString(2).equalsIgnoreCase("true")){
                advanceFavNumBuilder.append("+"+advance.getString(1));
            }
        }
        beginner.close();


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

    // Overriden Methods

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
            UpdateCrashlyticsLearnedWordCount();
        }




    }


    private void UpdateCrashlyticsLearnedWordCount(){

        // This method takes learn word count from database and puts them on Crashlytics keys

        Cursor IELTSCursor = ieltsWordDatabase.getData();
        Cursor TOEFLCursor = toeflWordDatabase.getData();
        Cursor SATCursor = satWordDatabase.getData();
        Cursor GRECursor = greWordDatabase.getData();

        String isIELTSLearned;
        int IELTSLearnedCount = 0;
        String isTOEFLLearned;
        int TOEFLLeanedCount = 0;
        String isSATLearned;
        int SATLearnedCount = 0;
        String isGRELearned;
        int GRELearnedCount =0;

        while (IELTSCursor.moveToNext()){

            isIELTSLearned = IELTSCursor.getString(3);

            if( isIELTSLearned.equalsIgnoreCase("true")){
                IELTSLearnedCount++;

            }
        }
        IELTSCursor.close();

        while(TOEFLCursor.moveToNext()){

            isTOEFLLearned = TOEFLCursor.getString(3);

            if(isTOEFLLearned.equalsIgnoreCase("true")){
                TOEFLLeanedCount++;
            }
        }

        TOEFLCursor.close();
        while(SATCursor.moveToNext()){

            isSATLearned = SATCursor.getString(3);

            if(isSATLearned.equalsIgnoreCase("true")){
                SATLearnedCount++;
            }
        }
        SATCursor.close();

        while(GRECursor.moveToNext()){

            isGRELearned = GRECursor.getString(3);

            if(isGRELearned.equalsIgnoreCase("true")){
                GRELearnedCount++;
            }
        }
        GRECursor.close();



    }


    @Override
    protected void onStop() {
        super.onStop();

        try{
            if(firebaseAuth.getCurrentUser() != null && connected){
               // updateFirebase();
            }

        }catch (NullPointerException e){

        }


    }




    // Check Trial State
    private String getTrialStatus(){

        String trialStatus = "";

        if(sp.contains("trial_end_date")){

            Date today = Calendar.getInstance().getTime();

            long endMillies = sp.getLong("trial_end_date",0) ;
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;



            if(leftMillies >=0){


                trialStatus = "active";

            }
            else {

                trialStatus = "ended";


            }

        }

        return trialStatus;

    }





}
