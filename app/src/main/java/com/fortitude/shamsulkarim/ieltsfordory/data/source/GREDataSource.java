package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GREDataSource extends DataSource{


    private static final int FAVORITE_COLL = 2;
    private static final int LEARNED_COLL = 3;
    private final String secondLanguage;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;


    private final int WORD_SIZE;
    private int[] position;
    private final List<String> favoriteStates;
    private final List<String> learnedStates;
    private final boolean isChecked;
    private final GREWordDatabase database;
    private final Context context;
    private String[] wordsArraySL, translationArraySL, example1arraySL, example2ArraySL, example3ArraySL;

    public GREDataSource(Context context){
        super(context);
        this.context = context;
        WORD_SIZE = context.getResources().getStringArray(R.array.GRE_words).length;

        database = new GREWordDatabase(context);


        favoriteStates = new ArrayList<>();
        learnedStates = new ArrayList<>();

        secondLanguage = sp.getString("secondlanguage","english");
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


        wordsArraySL =       context.getResources().getStringArray(R.array.GRE_words_sp);
        translationArraySL = context.getResources().getStringArray(R.array.GRE_translation_sp);
        example1arraySL =    context.getResources().getStringArray(R.array.GRE_example1_sp);
        example2ArraySL =    context.getResources().getStringArray(R.array.GRE_example2_sp);
        example3ArraySL =    context.getResources().getStringArray(R.array.GRE_example3_sp);

    }


    public void getFavoritePosition(){
        Cursor greRes = database.getData();


        while (greRes.moveToNext()){
            favoriteStates.add(greRes.getString(FAVORITE_COLL));
            learnedStates.add(greRes.getString(LEARNED_COLL));
        }

        greRes.close();
    }


    public boolean wasDatabaseLoadedProperly(){

        int databaseSize = 0;

        Cursor res = database.getData();

        while (res.moveToNext()){

            databaseSize++;
        }
        res.close();


        if(databaseSize< WORD_SIZE){
            Log.i("Database","Database was not loaded properly!");
            return false;
        }else {
            return true;
        }
    }




    private List<Word> listWords (int startPoint , int beginnerNumber){


        List<Word> wordList = new ArrayList<>();

        if(isChecked){

            for(int i = startPoint; i < beginnerNumber; i++){

                Word word = new Word(wordArray[i], translationArray[i],"", pronunArray[i], grammarArray[i], example1array[i], example2array[i], example3Array[i], vocabularyType[i], position[i], learnedStates.get(i),favoriteStates.get(i));


                if(!secondLanguage.equalsIgnoreCase("english")){


                    word.setWordSL(wordsArraySL[i]);
                    word.setTranslationSL(translationArraySL[i]);
                    word.setExample1SL(example1arraySL[i]);
                    word.setExample2SL(example2ArraySL[i]);
                    word.setExample3SL(example3ArraySL[i]);

                }else {

                    word.setWordSL("");
                    word.setTranslationSL("");
                    word.setExample1SL("");
                    word.setExample2SL("");
                    word.setExample3SL("");

                }

                wordList.add(word);





            }

        }


        return wordList;



    }







    @Override
    public List<Word> getBeginnerWords() {


        int GREbeginnerNumber = 0;

        if(isChecked){
            GREbeginnerNumber = (int) getPercentageNumber(30, WORD_SIZE);
        }

        return listWords(0,GREbeginnerNumber);
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

    public List<Word> getBeginnerFilteredWords(String isLearned){

        // returns learned or unlearned words based on parameter

        return getBeginnerWords().stream().filter( w -> w.isLearned.equalsIgnoreCase(isLearned)).collect(Collectors.toList());
    }

    public List<Word> getIntermediateFilteredWords(String isLearned){
        // returns learned or unlearned words based on parameter

        return getIntermediateWords().stream().filter(w -> w.isLearned.equalsIgnoreCase(isLearned)).collect(Collectors.toList());
    }
    public List<Word> getAdvanceFilteredWords(String isLearned){
        // returns learned or unlearned words based on parameter

        return getAdvanceWords().stream().filter(w -> w.isLearned.equalsIgnoreCase(isLearned)).collect(Collectors.toList());
    }

    public void updateFavorite(String id, String isFavorite){
        database.updateFav(id,isFavorite);
    }

    public void updateLearnState(String id, String isLearned){
        database.updateLearned(id,isLearned);
    }

    public int getBeginnerWordCount() {


        int beginnerCount = 0;

        if(isChecked){
            beginnerCount = (int) getPercentageNumber(30, WORD_SIZE);
        }

        return beginnerCount;
    }
    public int getIntermediateWordCount() {


        int beginnerCount = 0;


        if(isChecked){
            beginnerCount = getPercentageNumber(40, WORD_SIZE);

        }

        return beginnerCount;
    }
    public int getAdvanceWordCount() {

        int advanceCount = 0;

        if(isChecked){

            advanceCount = getPercentageNumber(30, WORD_SIZE);

        }

        return advanceCount;
    }
}
