package com.fortitude.shamsulkarim.ieltsfordory;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;


public class SplashScreen extends AppCompatActivity {

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this,R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setNavigationBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);




        //----------------------------------------

        // This method initializes default settings
        initialize();

        //---------------------------------------

        // This methods Initializes database at the first launch of the app
        // after first launch it sends you to MainActivity.java activity.

        initializeOrMainActivity();

        //-----------------------------------------------------------------

    }

    private void initializeOrMainActivity(){


        if(sp.contains("home")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {


            createDatabase();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sp.edit().putInt("DarkMode",0).apply();
            // Billing
           // getPreviousPurchases();
        }

    }


    // This methods initializes the databases and when it

    private void createDatabase(){



        DatabaseAsyncTask databaseTask = new DatabaseAsyncTask();
        databaseTask.execute(10);

    }

    private void initialize(){
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        //Default Settings Initializtion

        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",3).apply();



        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",3).apply();


        }
        // Default Dark themes
        setupAppTheme();




        ProgressBar progressBar = findViewById(R.id.spin_splash_screen);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);





        sp.edit().putString("advanceFavNum","").apply();
        sp.edit().putString("advanceLearnedNum","0").apply();
        sp.edit().putString("beginnerFavNum","").apply();
        sp.edit().putString("beginnerLearnedNum","0").apply();
        sp.edit().putString("intermediateFavNum", "").apply();
        sp.edit().putString("intermediateLearnedNum","0").apply();

        if(!sp.contains("skip")){


            sp.edit().putBoolean("skip",false).apply();
        }


        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();


        }

        setupDefaultVocabulary();
        setupDefaultLanguage();
        //--------------------------------------------------------------





    }

    private void setupAppTheme(){

        if(!sp.contains("DarkMode")){

            sp.edit().putInt("DarkMode",0).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {

            int darkMode = sp.getInt("DarkMode",0);

            switch(darkMode) {

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

    private void setupDefaultVocabulary(){

        if(!sp.contains("home")){

            sp.edit().putBoolean("isIELTSActive",true).apply();
            sp.edit().putBoolean("isTOEFLActive",true).apply();
            sp.edit().putBoolean("isSATActive",true).apply();
            sp.edit().putBoolean("isGREActive",true).apply();



        }
    }

    private void setupDefaultLanguage(){

        if(!sp.contains("home")){


            sp.edit().putString("secondlanguage","english").apply();
        }

    }






/// Threading with Async Task

    @SuppressLint("StaticFieldLeak")
    private class  DatabaseAsyncTask extends AsyncTask<Integer,Integer,String>{

        private  IELTSWordDatabase ieltsWordDatabase;
        private  TOEFLWordDatabase toeflWordDatabase;
        private  SATWordDatabase satWordDatabase;
        private  GREWordDatabase greWordDatabase;

        // SQL Database Initialization
//---------------------------------------------------
        private void initializingSQLDatabase(){

            ieltsWordDatabase = new IELTSWordDatabase(getApplicationContext());
            satWordDatabase = new SATWordDatabase(getApplicationContext());
            toeflWordDatabase = new TOEFLWordDatabase(getApplicationContext());
            greWordDatabase = new GREWordDatabase(getApplicationContext());
        }
        private void IELTStoDatabaseInitialization(){

            SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);





            if(!sp.contains("beginnerWordCount1")){
                final int beginnerWordLength = getResources().getStringArray(R.array.IELTS_words).length;
                sp.edit().putInt("beginnerWordCount1",beginnerWordLength).apply();

                for(int i = 0; i < beginnerWordLength; i++){

                    ieltsWordDatabase.insertData(""+i,"false","false", "false","false");

                }

            }
            int PREVIOUSBEGINNERCOUNT = sp.getInt("beginnerWordCount1",0);
            int CURRENTBEGINNERCOUNT = getResources().getStringArray(R.array.IELTS_words).length;



            if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


                for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                    ieltsWordDatabase.insertData(""+i,"false","false", "false", "false");




                }
                sp.edit().putInt("beginnerWordCount1",CURRENTBEGINNERCOUNT).apply();




            }







        }

        private void TOEFLDatabaseInitialization(){

            SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);

            if(!sp.contains("intermediateWordCount1")){
                final int intermediateWordLength = getResources().getStringArray(R.array.TOEFL_words).length;
                sp.edit().putInt("intermediateWordCount1",intermediateWordLength).apply();

                for(int i = 0; i < intermediateWordLength; i++){

                    toeflWordDatabase.insertData(""+i,"false","false", "false", "false");

                }

            }
            int PREVIOUSBEGINNERCOUNT = sp.getInt("intermediateWordCount1",0);
            int CURRENTBEGINNERCOUNT = getResources().getStringArray(R.array.TOEFL_words).length;

            if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){

                for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                    toeflWordDatabase.insertData(""+i,"false","false", "false", "false");

                }
                sp.edit().putInt("intermediateWordCount1",CURRENTBEGINNERCOUNT).apply();




            }

        }

        private void SATDatabaseInitialization(){

            SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


            if(!sp.contains("advanceWordCount1")){
                final int advanceWordLength = getResources().getStringArray(R.array.SAT_words).length;
                sp.edit().putInt("advanceWordCount1",advanceWordLength).apply();

                for(int i = 0; i < advanceWordLength; i++){

                    satWordDatabase.insertData(""+i,"false","false", "false", "false");

                }

            }
            int PREVIOUSBEGINNERCOUNT = sp.getInt("advanceWordCount1",0);
            int CURRENTBEGINNERCOUNT = getResources().getStringArray(R.array.SAT_words).length;


            if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


                for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                    satWordDatabase.insertData(""+i,"false","false", "false", "false");




                }
                sp.edit().putInt("advanceWordCount1",CURRENTBEGINNERCOUNT).apply();




            }


        }

        private void GREDatabaseInitialization(){


            SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


            if(!sp.contains("GREwordCount1")){
                final int advanceWordLength = getResources().getStringArray(R.array.GRE_words).length;
                sp.edit().putInt("GREwordCount1",advanceWordLength).apply();

                for(int i = 0; i < advanceWordLength; i++){

                    greWordDatabase.insertData(""+i,"false","false", "false", "false");

                }

            }
            int PREVIOUSBEGINNERCOUNT = sp.getInt("GREwordCount1",0);
            int CURRENTBEGINNERCOUNT = getResources().getStringArray(R.array.GRE_words).length;


            if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


                for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                    greWordDatabase.insertData(""+i,"false","false", "false", "false");




                }
                sp.edit().putInt("GREwordCount1",CURRENTBEGINNERCOUNT).apply();




            }

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            initializingSQLDatabase();
            IELTStoDatabaseInitialization();
            TOEFLDatabaseInitialization();
            SATDatabaseInitialization();
            GREDatabaseInitialization();

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            launchMainActivity();
           // Toast.makeText(getApplicationContext(),"Async Task Finished",Toast.LENGTH_LONG).show();

        }

        private void launchMainActivity(){

            Handler handler =new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


//                    if(!sp.contains("home")){
//
//

//                        finish();
//
//                    }
//                    else {
                        startActivity(new Intent(getApplicationContext(), StartTrial.class));
                        finish();
//                    }




                    finish();
                }
            }, 1000L);

        }


    }


}
