package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.GREDataSource;
import java.util.List;


public class Repository {

    private GREDataSource greDataSource;


    public Repository(Context context){

        greDataSource = new GREDataSource(context);



    }


    public List<Word> getGreBeginnerVocabulary(){
        return greDataSource.getBeginnerWordData();
    }
    public List<Word> getGreIntermediateVocabulary(){
        return greDataSource.getIntermediateWordData();
    }
    public List<Word> getGreAdvanceVocabulary(){
        return greDataSource.getAdvanceWordData();
    }
}
