package com.fortitude.shamsulkarim.ieltsfordory.data.utils;


import android.content.Context;
import android.util.Log;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.GREDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.IELTSDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.SATDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.TOEFLDataSource;

public class DatabaseChecker {

    public static final String TAG = DatabaseChecker.class.getSimpleName();
    private final Context context;

    private IELTSDataSource ieltsDataSource;
    private TOEFLDataSource toeflDataSource;
    private SATDataSource satDataSource;
    private GREDataSource greDataSource;

    public DatabaseChecker(Context context) {

        this.context = context;
        ieltsDataSource = new IELTSDataSource(context);
        toeflDataSource = new TOEFLDataSource(context);
        satDataSource = new SATDataSource(context);
        greDataSource = new GREDataSource(context);


        isDatabaseLoaded();

    }

    public boolean isDatabaseLoaded() {

        int totalWordsInDatabase = getTotalWordsInDatabase();

        int requiredWordsInDatabase = getRequiredWordsInDatabase();


        Log.i(TAG, "Required words in db: " + requiredWordsInDatabase + " total words in db: " + totalWordsInDatabase);


        if (totalWordsInDatabase < requiredWordsInDatabase) {

            return false;
        } else {
            return true;
        }


    }

    private int getRequiredWordsInDatabase() {
        VocabularyRepository repository = new VocabularyRepository(context);

        return repository.getTotalBeginnerCount()
                + repository.getTotalIntermediateCount() + repository.getTotalAdvanceCount();
    }

    private int getTotalWordsInDatabase() {
        int ieltsSize = ieltsDataSource.getIELTSDatabaseSize();
        int toeflSize = toeflDataSource.getTOEFLDatabaseSize();
        int satSize = satDataSource.getSATDatabaseSize();
        int greSize = greDataSource.getGREDatabaseSize();

        Log.i(TAG, "ielts in db: " + ieltsSize);
        Log.i(TAG, "toefl in db: " + toeflSize);
        Log.i(TAG, "sat in db: " + satSize);
        Log.i(TAG, "gre in db: " + greSize);

        return ieltsSize + toeflSize + satSize + greSize;
    }

    public boolean isIELTSDatabaseLoaded() {

        int wordsInDatabase = ieltsDataSource.getIELTSDatabaseSize();
        int wordsInArray = context.getResources().getStringArray(R.array.IELTS_words).length;


        if (wordsInDatabase < wordsInArray) {

            return false;
        } else {
            return true;
        }
    }

    public int getCurrentIELTSDatabaseSize() {

        return ieltsDataSource.getIELTSDatabaseSize();

    }

    public int getCurrentTOEFLDatabaseSize() {

        return toeflDataSource.getTOEFLDatabaseSize();

    }

    public int getCurrentSAtDatabaseSize() {

        return satDataSource.getSATDatabaseSize();

    }

    public int getCurrentGREDatabaseSize() {

        return greDataSource.getGREDatabaseSize();

    }

    public boolean isTOEFLDatabaseLoaded() {

        int wordsInDatabase = toeflDataSource.getTOEFLDatabaseSize();
        int wordsInArray = context.getResources().getStringArray(R.array.TOEFL_words).length;


        if (wordsInDatabase < wordsInArray) {

            return false;
        } else {
            return true;
        }
    }

    public boolean isSATLDatabaseLoaded() {

        int wordsInDatabase = satDataSource.getSATDatabaseSize();
        int wordsInArray = context.getResources().getStringArray(R.array.SAT_words).length;


        if (wordsInDatabase < wordsInArray) {

            return false;
        } else {
            return true;
        }
    }

    public boolean isGRELDatabaseLoaded() {

        int wordsInDatabase = greDataSource.getGREDatabaseSize();
        int wordsInArray = context.getResources().getStringArray(R.array.GRE_words).length;


        if (wordsInDatabase < wordsInArray) {

            return false;
        } else {
            return true;
        }
    }


}
