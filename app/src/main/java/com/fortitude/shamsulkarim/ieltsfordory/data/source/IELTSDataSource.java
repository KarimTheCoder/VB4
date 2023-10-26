package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IELTSDataSource extends DataSource{
    private static int LEARNED_COLL = 3;
    private static int FAVORITE_COLL = 2;
    private static int POSITION_COLL = 0;
    private List<String> learnedStates;
    private final int IELTS_WORD_SIZE;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;

    private int[] position;
    private final boolean isChecked;
    private final IELTSWordDatabase database;
    private final Context context;
    private List<String> favoriteStates;
    private List<Integer> databasePosition;

    public IELTSDataSource(Context context) {
        super(context);
        this.context = context;
        IELTS_WORD_SIZE = context.getResources().getStringArray(R.array.IELTS_words).length;

        database = new IELTSWordDatabase(context);

        favoriteStates = new ArrayList<>();
        databasePosition = new ArrayList<>();
        learnedStates = new ArrayList<>();
        isChecked =   sp.getBoolean("isIELTSActive",true);

        getFavoritePosition();
        initArray();

    }


    public List<String> getFavoritePosition(){

        Cursor res = database.getData();
        while (res.moveToNext()){

            favoriteStates.add(res.getString(FAVORITE_COLL));
            learnedStates.add(res.getString(LEARNED_COLL));
            databasePosition.add(res.getInt(POSITION_COLL));
        }
        res.close();
        return favoriteStates;
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

                wordList.add(new Word(wordArray[i], translationArray[i],"", pronunArray[i], grammarArray[i], example1array[i], example2array[i], example3Array[i], vocabularyType[i], position[i], learnedStates.get(i),favoriteStates.get(i)));

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
