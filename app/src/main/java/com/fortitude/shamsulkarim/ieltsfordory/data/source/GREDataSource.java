package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GREDataSource extends DataSource{


    private static int FAVORITE_COLL = 2;
    private static int LEARNED_COLL = 3;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;
    private final int GRE_WORD_SIZE;
    private int[] position;
    private List<String> favoriteStates;
    private List<String> learnedStates;
    private final boolean isChecked;
    private final GREWordDatabase database;
    private final Context context;

    public GREDataSource(Context context){
        super(context);
        this.context = context;
        GRE_WORD_SIZE = context.getResources().getStringArray(R.array.GRE_words).length;

        database = new GREWordDatabase(context);


        favoriteStates = new ArrayList<>();
        learnedStates = new ArrayList<>();
        isChecked =   sp.getBoolean("isGREActive",true);

        arrayInit();
        getFavoritePosition();
    }




    private void arrayInit(){

        wordArray = context.getResources().getStringArray(R.array.GRE_words);
        translationArray = context.getResources().getStringArray(R.array.GRE_translation);
        grammarArray = context.getResources().getStringArray(R.array.GRE_grammar);
        pronunArray = context.getResources().getStringArray(R.array.GRE_pronunciation);
        example1array = context.getResources().getStringArray(R.array.GRE_example1);
        example2array = context.getResources().getStringArray(R.array.GRE_example2);
        example3Array = context.getResources().getStringArray(R.array.GRE_example3);
        vocabularyType = context.getResources().getStringArray(R.array.GRE_level);
        position = context.getResources().getIntArray(R.array.GRE_position);

    }

    public void getFavoritePosition(){
        Cursor greRes = database.getData();

        while (greRes.moveToNext()){
            favoriteStates.add(greRes.getString(FAVORITE_COLL));
            learnedStates.add(greRes.getString(LEARNED_COLL));
        }

        greRes.close();
    }
    private List<Word> listWords (int startPoint , int beginnerNumber){


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


        int GREbeginnerNumber = 0;

        if(isChecked){
            GREbeginnerNumber = (int) getPercentageNumber(30, GRE_WORD_SIZE);
        }

        return listWords(0,GREbeginnerNumber);
    }

    @Override
    public List<Word> getIntermediateWords() {


        int intermediateNumber = 0;
        int beginnerNumber = 0;

        if(isChecked){
            intermediateNumber = getPercentageNumber(40, GRE_WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, GRE_WORD_SIZE);

        }

        return listWords(beginnerNumber,beginnerNumber+intermediateNumber);
    }

    @Override
    public List<Word> getAdvanceWords() {

        int intermediateNumber = 0;
        int beginnerNumber = 0;


        if(isChecked){

            intermediateNumber = getPercentageNumber(40, GRE_WORD_SIZE);
            beginnerNumber = getPercentageNumber(30, GRE_WORD_SIZE);
        }

        return listWords(beginnerNumber+intermediateNumber,GRE_WORD_SIZE);
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
