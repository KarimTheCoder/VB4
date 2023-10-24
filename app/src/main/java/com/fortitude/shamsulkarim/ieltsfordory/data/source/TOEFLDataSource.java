package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;

public class TOEFLDataSource extends DataSource{

    private final int WORD_SIZE;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;

    private int[] position;
    private List<String> favoriteState;
    private final boolean isChecked;
    private final SATWordDatabase database;
    private final Context context;






    public TOEFLDataSource(Context context) {
        super(context);


        this.context = context;

        WORD_SIZE = context.getResources().getStringArray(R.array.TOEFL_words).length;

        database = new SATWordDatabase(context);

        favoriteState = new ArrayList<>();
        isChecked =   sp.getBoolean("isTOEFLActive",true);

        getFavoritePosition();
        initArray();

    }

    private void getFavoritePosition(){
        Cursor res = database.getData();

        while (res.moveToNext()){
            favoriteState.add(res.getString(2));
        }

        res.close();
    }
    private void initArray(){

        wordArray = context.getResources().getStringArray(R.array.TOEFL_words);
        translationArray = context.getResources().getStringArray(R.array.TOEFL_translation);
        grammarArray = context.getResources().getStringArray(R.array.TOEFL_grammar);
        pronunArray = context.getResources().getStringArray(R.array.TOEFL_pronunciation);
        example1array = context.getResources().getStringArray(R.array.TOEFL_example1);
        example2array = context.getResources().getStringArray(R.array.TOEFL_example2);
        example3Array = context.getResources().getStringArray(R.array.TOEFL_example3);
        vocabularyType = context.getResources().getStringArray(R.array.TOEFL_level);
        position = context.getResources().getIntArray(R.array.TOEFL_position);

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
            beginnerNumber = (int) getPercentageNumber(30, WORD_SIZE);
        }

        return listWords(0,beginnerNumber);
    }

    @Override
    public List<Word> getIntermediateWords() {
        int intermediateNumber = 0;
        int beginnerNumber = 0;

        if(isChecked){
            intermediateNumber = getPercentageNumber(40, WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, WORD_SIZE);

        }

        return listWords(beginnerNumber,beginnerNumber+intermediateNumber);
    }

    @Override
    public List<Word> getAdvanceWords() {

        int intermediateNumber = 0;
        int beginnerNumber = 0;

        if(isChecked){

            intermediateNumber = getPercentageNumber(40, WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, WORD_SIZE);
        }

        return listWords(beginnerNumber+intermediateNumber, WORD_SIZE);
    }
}
