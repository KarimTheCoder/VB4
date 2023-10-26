package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TOEFLDataSource extends DataSource{
    private static int FAVORITE_COLL = 2;
    private static int LEARNED_COLL = 3;
    private final int WORD_SIZE;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;

    private int[] position;

    private final boolean isChecked;
    private final TOEFLWordDatabase database;
    private final Context context;
    private List<String> favoriteState;
    private List<String> learnedStates;





    public TOEFLDataSource(Context context) {
        super(context);


        this.context = context;
        favoriteState = new ArrayList<>();
        learnedStates = new ArrayList<>();

        WORD_SIZE = context.getResources().getStringArray(R.array.TOEFL_words).length;

        database = new TOEFLWordDatabase(context);
        isChecked =   sp.getBoolean("isTOEFLActive",true);

        getFavoritePosition();
        initArray();

    }

    public List<String> getFavoritePosition(){

        Cursor res = database.getData();

        while (res.moveToNext()){
            favoriteState.add(res.getString(FAVORITE_COLL));
            learnedStates.add(res.getString(LEARNED_COLL));
        }

        res.close();
        return favoriteState;
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

                wordList.add(new Word(wordArray[i], translationArray[i],"", pronunArray[i], grammarArray[i], example1array[i], example2array[i], example3Array[i], vocabularyType[i], position[i],learnedStates.get(i),favoriteState.get(i)));

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

    public List<Word> getFavoriteWords(){

        List<Word> words = new ArrayList<>();
        words.addAll(getBeginnerWords().stream().filter( w -> w.isFavorite.equalsIgnoreCase("True")).collect(Collectors.toList()));
        words.addAll(getIntermediateWords().stream().filter(w -> w.isFavorite.equalsIgnoreCase("True")).collect(Collectors.toList()));
        words.addAll(getAdvanceWords().stream().filter(w -> w.isFavorite.equalsIgnoreCase("True")).collect(Collectors.toList()));



        return words;


    }
    public List<Word> getBeginnerLearnedWords(){
        return getBeginnerWords().stream().filter( w -> w.isLearned.equalsIgnoreCase("True")).collect(Collectors.toList());
    }

    public List<Word> getIntermediateLearnedWords(){
        return getIntermediateWords().stream().filter(w -> w.isLearned.equalsIgnoreCase("True")).collect(Collectors.toList());
    }
    public List<Word> getAdvanceLearnedWords(){
        return getAdvanceWords().stream().filter(w -> w.isLearned.equalsIgnoreCase("True")).collect(Collectors.toList());
    }


    public void updateFavorite(String id, String isFavorite){
        database.updateFav(id,isFavorite);
    }
}
