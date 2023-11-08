package com.fortitude.shamsulkarim.ieltsfordory.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.List;

public abstract class DataSource {


    public SharedPreferences sp;

    public DataSource(Context context) {
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
    }

    public int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int)beginnerNum;

    }



    public abstract List<Word> getBeginnerWords();
    public abstract List<Word> getIntermediateWords();
    public abstract List<Word> getAdvanceWords();





}
