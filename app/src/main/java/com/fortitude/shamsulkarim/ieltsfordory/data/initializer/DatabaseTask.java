package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;


import android.content.Context;
import android.content.SharedPreferences;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;

public class DatabaseTask implements Task{

    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;

    private Context context;

    public DatabaseTask(Context context) {
        this.context = context;
    }

    @Override
    public void execute() {

        initializingSQLDatabase();
        IELTStoDatabaseInitialization();
        TOEFLDatabaseInitialization();
        SATDatabaseInitialization();
        GREDatabaseInitialization();
    }


    private void initializingSQLDatabase(){

        ieltsWordDatabase = new IELTSWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        toeflWordDatabase = new TOEFLWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);
    }

    private void IELTStoDatabaseInitialization(){

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);





        if(!sp.contains("beginnerWordCount1")){
            final int beginnerWordLength = context.getResources().getStringArray(R.array.IELTS_words).length;
            sp.edit().putInt("beginnerWordCount1",beginnerWordLength).apply();

            for(int i = 0; i < beginnerWordLength; i++){

                ieltsWordDatabase.insertData(""+i,"false","false", "false","false");

            }

        }
        int PREVIOUSBEGINNERCOUNT = sp.getInt("beginnerWordCount1",0);
        int CURRENTBEGINNERCOUNT = context.getResources().getStringArray(R.array.IELTS_words).length;



        if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


            for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                ieltsWordDatabase.insertData(""+i,"false","false", "false", "false");




            }
            sp.edit().putInt("beginnerWordCount1",CURRENTBEGINNERCOUNT).apply();




        }







    }

    private void TOEFLDatabaseInitialization(){

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);

        if(!sp.contains("intermediateWordCount1")){
            final int intermediateWordLength = context.getResources().getStringArray(R.array.TOEFL_words).length;
            sp.edit().putInt("intermediateWordCount1",intermediateWordLength).apply();

            for(int i = 0; i < intermediateWordLength; i++){

                toeflWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }
        int PREVIOUSBEGINNERCOUNT = sp.getInt("intermediateWordCount1",0);
        int CURRENTBEGINNERCOUNT = context.getResources().getStringArray(R.array.TOEFL_words).length;

        if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){

            for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                toeflWordDatabase.insertData(""+i,"false","false", "false", "false");

            }
            sp.edit().putInt("intermediateWordCount1",CURRENTBEGINNERCOUNT).apply();




        }

    }

    private void SATDatabaseInitialization(){

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


        if(!sp.contains("advanceWordCount1")){
            final int advanceWordLength = context.getResources().getStringArray(R.array.SAT_words).length;
            sp.edit().putInt("advanceWordCount1",advanceWordLength).apply();

            for(int i = 0; i < advanceWordLength; i++){

                satWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }
        int PREVIOUSBEGINNERCOUNT = sp.getInt("advanceWordCount1",0);
        int CURRENTBEGINNERCOUNT = context.getResources().getStringArray(R.array.SAT_words).length;


        if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


            for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                satWordDatabase.insertData(""+i,"false","false", "false", "false");




            }
            sp.edit().putInt("advanceWordCount1",CURRENTBEGINNERCOUNT).apply();




        }


    }

    private void GREDatabaseInitialization(){


        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);


        if(!sp.contains("GREwordCount1")){
            final int advanceWordLength = context.getResources().getStringArray(R.array.GRE_words).length;
            sp.edit().putInt("GREwordCount1",advanceWordLength).apply();

            for(int i = 0; i < advanceWordLength; i++){

                greWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }
        int PREVIOUSBEGINNERCOUNT = sp.getInt("GREwordCount1",0);
        int CURRENTBEGINNERCOUNT = context.getResources().getStringArray(R.array.GRE_words).length;


        if(CURRENTBEGINNERCOUNT > PREVIOUSBEGINNERCOUNT){


            for(int i = PREVIOUSBEGINNERCOUNT; i < CURRENTBEGINNERCOUNT; i++){

                greWordDatabase.insertData(""+i,"false","false", "false", "false");




            }
            sp.edit().putInt("GREwordCount1",CURRENTBEGINNERCOUNT).apply();




        }

    }




}
