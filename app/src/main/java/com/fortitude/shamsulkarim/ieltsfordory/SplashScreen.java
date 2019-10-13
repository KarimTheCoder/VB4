package com.fortitude.shamsulkarim.ieltsfordory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;



public class SplashScreen extends AppCompatActivity {

//    SQL Database Initialization
    //--------------------------------------------
    private  IELTSWordDatabase ieltsWordDatabase;
    private  TOEFLWordDatabase toeflWordDatabase;
    private  SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private JustLearnedDatabaseBeginner justLearnedDatabase;

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
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

        delayInitialization();










        //launchMainActivity();

        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",3).apply();

        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",3).apply();

        }



    }




    private void launchMainActivity(){

        Handler handler =new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if(!sp.contains("isIELTSActive")){


                     startActivity(new Intent(getApplicationContext(), ChooseVocabulary.class));

                }
                else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }




                finish();
            }
        }, 1000L);

    }

    private void delayInitialization(){

        ProgressDialog dialog = ProgressDialog.show(SplashScreen.this, "",
                "Loading. Please wait...", true);




        Handler handler =new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                initializingSQLDatabase();
                IELTStoDatabaseInitialization();
                TOEFLDatabaseInitialization();
                SATDatabaseInitialization();
                GREDatabaseInitialization();
                launchMainActivity();


                finish();
            }
        }, 200L);


    }


    private void delayMethod(){

        Handler handler =new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {







                finish();
            }
        }, 600L);



    }



    private void initializingSQLDatabase(){

        ieltsWordDatabase = new IELTSWordDatabase(this);
        satWordDatabase = new SATWordDatabase(this);
        toeflWordDatabase = new TOEFLWordDatabase(this);
        greWordDatabase = new GREWordDatabase(this);
        justLearnedDatabase = new JustLearnedDatabaseBeginner(this);
    }

// SQL Database Initialization
//---------------------------------------------------
    private void IELTStoDatabaseInitialization(){

        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);





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

        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);

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

        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


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


        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


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








}
