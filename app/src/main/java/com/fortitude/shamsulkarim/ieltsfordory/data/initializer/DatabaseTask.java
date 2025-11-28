package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.IELTSDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.utils.DatabaseChecker;

public class DatabaseTask implements Task{

    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private DatabaseChecker databaseChecker;

    private final Context context;
    DatabaseInitConfig config;

    public DatabaseTask(Context context, DatabaseInitConfig config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public void execute() {

        Log.d("DB_INIT","Starting database initialization");
        initializingSQLDatabase();
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(4);
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(config.getParallelism());
        pool.execute(() -> { try { Log.d("DB_INIT","Init IELTS"); runWithRetry(this::IELTStoDatabaseInitialization, "IELTS"); } finally { latch.countDown(); } });
        pool.execute(() -> { try { Log.d("DB_INIT","Init TOEFL"); runWithRetry(this::TOEFLDatabaseInitialization, "TOEFL"); } finally { latch.countDown(); } });
        pool.execute(() -> { try { Log.d("DB_INIT","Init SAT"); runWithRetry(this::SATDatabaseInitialization, "SAT"); } finally { latch.countDown(); } });
        pool.execute(() -> { try { Log.d("DB_INIT","Init GRE"); runWithRetry(this::GREDatabaseInitialization, "GRE"); } finally { latch.countDown(); } });

        try {
            boolean completed = latch.await(config.getTimeoutMs(), java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!completed) {
                pool.shutdownNow();
                throw new RuntimeException("Initialization timed out");
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            throw new RuntimeException("Initialization interrupted", e);
        }
        pool.shutdown();
        Log.d("DB_INIT","All database initialization complete");
        ieltsWordDatabase.close();
        toeflWordDatabase.close();
        satWordDatabase.close();
        greWordDatabase.close();
    }

    private void runWithRetry(Runnable r, String label) {
        int attempts = 0;
        while (true) {
            try {
                r.run();
                return;
            } catch (Exception e) {
                attempts++;
                Log.e("DB_INIT", label + " phase failed (attempt " + attempts + ")", e);
                if (attempts > config.getMaxRetries()) {
                    throw e;
                }
            }
        }
    }


    private void initializingSQLDatabase(){

        ieltsWordDatabase = new IELTSWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        toeflWordDatabase = new TOEFLWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);
        databaseChecker = new DatabaseChecker(context);
        ieltsWordDatabase.setWriteAheadLoggingEnabled(true);
        toeflWordDatabase.setWriteAheadLoggingEnabled(true);
        satWordDatabase.setWriteAheadLoggingEnabled(true);
        greWordDatabase.setWriteAheadLoggingEnabled(true);
    }

    private void IELTStoDatabaseInitialization(){

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);



        if(!sp.contains("beginnerWordCount1")){
            final int beginnerWordLength = context.getResources().getStringArray(R.array.IELTS_words).length;
            sp.edit().putInt("beginnerWordCount1",beginnerWordLength).apply();


            for(int i = 0; i < beginnerWordLength; i++){
                ieltsWordDatabase.insertData(""+i,"false","false", "false","false");
            }

        }

        if(!databaseChecker.isIELTSDatabaseLoaded()){

            int currentSize = databaseChecker.getCurrentIELTSDatabaseSize();
            int requiredSize = context.getResources().getStringArray(R.array.IELTS_words).length;

            for(int i = currentSize; i < requiredSize; i++){
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

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        if(!sp.contains("intermediateWordCount1")){
            final int intermediateWordLength = context.getResources().getStringArray(R.array.TOEFL_words).length;
            sp.edit().putInt("intermediateWordCount1",intermediateWordLength).apply();

            for(int i = 0; i < intermediateWordLength; i++){

                toeflWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }

        if(!databaseChecker.isTOEFLDatabaseLoaded()){

            int currentSize = databaseChecker.getCurrentTOEFLDatabaseSize();
            int requiredSize = context.getResources().getStringArray(R.array.TOEFL_words).length;

            for(int i = currentSize; i < requiredSize; i++){
                toeflWordDatabase.insertData(""+i,"false","false", "false","false");
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

        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        if(!sp.contains("advanceWordCount1")){
            final int advanceWordLength = context.getResources().getStringArray(R.array.SAT_words).length;
            sp.edit().putInt("advanceWordCount1",advanceWordLength).apply();

            for(int i = 0; i < advanceWordLength; i++){

                satWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }

        if(!databaseChecker.isSATLDatabaseLoaded()){

            int currentSize = databaseChecker.getCurrentSAtDatabaseSize();
            int requiredSize = context.getResources().getStringArray(R.array.SAT_words).length;

            for(int i = currentSize; i < requiredSize; i++){
                satWordDatabase.insertData(""+i,"false","false", "false","false");
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


        SharedPreferences sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        if(!sp.contains("GREwordCount1")){
            final int advanceWordLength = context.getResources().getStringArray(R.array.GRE_words).length;
            sp.edit().putInt("GREwordCount1",advanceWordLength).apply();

            for(int i = 0; i < advanceWordLength; i++){

                greWordDatabase.insertData(""+i,"false","false", "false", "false");

            }

        }


        if(!databaseChecker.isGRELDatabaseLoaded()){

            int currentSize = databaseChecker.getCurrentGREDatabaseSize();
            int requiredSize = context.getResources().getStringArray(R.array.GRE_words).length;

            for(int i = currentSize; i < requiredSize; i++){
                greWordDatabase.insertData(""+i,"false","false", "false","false");
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
