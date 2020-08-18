package com.fortitude.shamsulkarim.ieltsfordory;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;


public class SplashScreen extends AppCompatActivity {





    // SQL Database Initialization
    //----------------------------

    private JustLearnedDatabaseBeginner justLearnedDatabase;

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // This code reports to Crashlytics of connection
        checkInternetConnection();

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
        }

    }


    // This methods initializes the databases and when it
    //finishes it launches the ChooseVocabulary.java activity.
    private void createDatabase(){



        DatabaseAsyncTask databaseTask = new DatabaseAsyncTask();
        databaseTask.execute(10);

    }

    private void initialize(){
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        //Default Settings Initializtion

        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",3).apply();

            Crashlytics.setBool("SignIn Status",false);
            Crashlytics.setBool("Sound Status",true);
            Crashlytics.setInt("Words Per Session",3);

        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",3).apply();
            Crashlytics.setInt("Repetition Per Session",3);

        }
        // Default Dark themes
        setupAppTheme();



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
        Crashlytics.setBool("isIELTSActive",true);
        Crashlytics.setBool("isTOEFLActive",true);
        Crashlytics.setBool("isSATActive",true);
        Crashlytics.setBool("isGREActive",true);

    }

    private void setupDefaultLanguage(){

        if(!sp.contains("home")){


            sp.edit().putString("secondlanguage","english").apply();
        }

    }



    private void delayInitialization(){

        final ProgressDialog dialog = ProgressDialog.show(SplashScreen.this, "",
                "Loading. Please wait...", true);




        Handler handler =new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {






                finish();
            }
        }, 5000L);

        dialog.dismiss();


    }

    // This method Checks internet connection and reports to Crashlytics
    private void checkInternetConnection(){
        Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);
        Crashlytics.setBool("Connection Status",connected);
    }



/// Threading with Async Task

    private class DatabaseAsyncTask extends AsyncTask<Integer,Integer,String>{

        private  IELTSWordDatabase ieltsWordDatabase;
        private  TOEFLWordDatabase toeflWordDatabase;
        private  SATWordDatabase satWordDatabase;
        private GREWordDatabase greWordDatabase;
        private SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);;

        ProgressDialog dialog;
        // SQL Database Initialization
//---------------------------------------------------
        private void initializingSQLDatabase(){

            ieltsWordDatabase = new IELTSWordDatabase(getApplicationContext());
            satWordDatabase = new SATWordDatabase(getApplicationContext());
            toeflWordDatabase = new TOEFLWordDatabase(getApplicationContext());
            greWordDatabase = new GREWordDatabase(getApplicationContext());
            justLearnedDatabase = new JustLearnedDatabaseBeginner(getApplicationContext());
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




            }else {



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




            }else {




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
        protected void onPreExecute() {
            super.onPreExecute();

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
//                        startActivity(new Intent(getApplicationContext(), ChooseVocabulary.class));
//                        finish();
//
//                    }
//                    else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
//                    }




                    finish();
                }
            }, 1000L);

        }


    }

}
