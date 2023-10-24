package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;

public class IELTSDataSource extends DataSource{

    private final int IELTS_WORD_SIZE;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;

    private int[] position;
    private List<String> greFavPosition;
    private final boolean isChecked;
    private final IELTSWordDatabase database;
    private final Context context;


    public IELTSDataSource(Context context) {
        super(context);
        this.context = context;
        IELTS_WORD_SIZE = context.getResources().getStringArray(R.array.IELTS_words).length;

        database = new IELTSWordDatabase(context);

        greFavPosition = new ArrayList<>();
        isChecked =   sp.getBoolean("isIELTSActive",true);

        getFavoritePosition();
        initArray();

    }


    private void getFavoritePosition(){
        Cursor res = database.getData();

        while (res.moveToNext()){
            greFavPosition.add(res.getString(2));
        }

        res.close();
    }

    private void initArray(){

        wordArray = context.getResources().getStringArray(R.array.IELTS_words);
        translationArray = context.getResources().getStringArray(R.array.IELTS_translation);
        grammarArray = context.getResources().getStringArray(R.array.IELTS_grammar);
        pronunArray = context.getResources().getStringArray(R.array.IELTS_pronunciation);
        example1array = context.getResources().getStringArray(R.array.IELTS_example1);
        example2array = context.getResources().getStringArray(R.array.IELTS_example2);
        example3Array = context.getResources().getStringArray(R.array.IELTS_example3);
        vocabularyType = context.getResources().getStringArray(R.array.IELTS_level);
        position = context.getResources().getIntArray(R.array.IELTS_position);


    }



    private List<Word> listWords (int startPoint , int beginnerNumber){
        //Todo define isLearned

        List<Word> wordList = new ArrayList<>();

        if(isChecked){

            for(int i = startPoint; i < beginnerNumber; i++){

                wordList.add(new Word(wordArray[i], translationArray[i],"", pronunArray[i], grammarArray[i], example1array[i], example2array[i], example3Array[i], vocabularyType[i], position[i], "",""));

            }

        }
        return wordList;
    }



    @Override
    public List<Word> getBeginnerWords() {
        int beginnerNumber = 0;

        if(isChecked){
            beginnerNumber = (int) getPercentageNumber(30, IELTS_WORD_SIZE);
        }

        return listWords(0,beginnerNumber);
    }

    @Override
    public List<Word> getIntermediateWords() {
        int intermediateNumber = 0;
        int beginnerNumber = 0;

        if(isChecked){
            intermediateNumber = getPercentageNumber(40, IELTS_WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, IELTS_WORD_SIZE);

        }

        return listWords(beginnerNumber,beginnerNumber+intermediateNumber);
    }

    @Override
    public List<Word> getAdvanceWords() {
        int intermediateNumber = 0;
        int beginnerNumber = 0;


        if(isChecked){

            intermediateNumber = getPercentageNumber(40, IELTS_WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, IELTS_WORD_SIZE);
        }

        return listWords(beginnerNumber+intermediateNumber,IELTS_WORD_SIZE);
    }
}
