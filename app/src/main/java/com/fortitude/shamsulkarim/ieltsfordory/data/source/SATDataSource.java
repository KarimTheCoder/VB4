package com.fortitude.shamsulkarim.ieltsfordory.data.source;

import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;

public class SATDataSource extends DataSource{

    private static final int FAVORITE_COLL = 2;
    private static final int LEARNED_COLL = 3;

    private final int WORD_SIZE;
    private String[] wordArray, translationArray, grammarArray, pronunArray, example1array, example2array, example3Array, vocabularyType;
    private final CopyOnWriteArrayList<String> favoriteStates;
    private final CopyOnWriteArrayList<String> learnedStates;
    private int[] position;
    private String[] wordsArraySL, translationArraySL, example1arraySL, example2ArraySL, example3ArraySL;

    private final String secondLanguage;
    private final boolean isChecked;
    private final SATWordDatabase database;
    private final Context context;

    public SATDataSource(Context context) {
        super(context);

        this.context = context;

        WORD_SIZE = context.getResources().getStringArray(R.array.SAT_words).length;

        database = SATWordDatabase.getInstance(context);
        favoriteStates = new CopyOnWriteArrayList<>();
        learnedStates = new CopyOnWriteArrayList<>();
        isChecked =   sp.getBoolean("isSATActive",true);
        secondLanguage = sp.getString("secondlanguage","english");
        getFavoritePosition();
        initArray();



    }

    public int getLearnedWordCount(){

        getFavoritePosition();

        return learnedStates.size();

    }

    public int getSATDatabaseSize(){


        int size = 0;

        Cursor res = database.getData();
        while (res.moveToNext()){


            size++;
        }
        res.close();
        return size;
    }

    public void getFavoritePosition(){
        favoriteStates.clear();
        learnedStates.clear();
        Cursor res = database.getData();
        while (res.moveToNext()){
            favoriteStates.add(res.getString(FAVORITE_COLL));
            learnedStates.add(res.getString(LEARNED_COLL));
        }
        res.close();
    }
    private void initArray(){

        wordArray = context.getResources().getStringArray(R.array.SAT_words);
        translationArray = context.getResources().getStringArray(R.array.SAT_translation);
        grammarArray = context.getResources().getStringArray(R.array.SAT_grammar);
        pronunArray = context.getResources().getStringArray(R.array.SAT_pronunciation);
        example1array = context.getResources().getStringArray(R.array.SAT_example1);
        example2array = context.getResources().getStringArray(R.array.SAT_example2);
        example3Array = context.getResources().getStringArray(R.array.SAT_example3);
        vocabularyType = context.getResources().getStringArray(R.array.SAT_level);
        position = context.getResources().getIntArray(R.array.SAT_position);

        // Translation
        wordsArraySL =       context.getResources().getStringArray(R.array.SAT_words_sp);
        translationArraySL = context.getResources().getStringArray(R.array.SAT_translation_sp);
        example1arraySL =    context.getResources().getStringArray(R.array.SAT_example1_sp);
        example2ArraySL =    context.getResources().getStringArray(R.array.SAT_example2_sp);
        example3ArraySL =    context.getResources().getStringArray(R.array.SAT_example3_sp);

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
