package com.fortitude.shamsulkarim.ieltsfordory;

/**
 * Created by Shamsul Karim on 14-Dec-16.
 */

public class Word {


    public int count;

    public String getWordSL() {

        return  wordSL;

    }

    public void setWordSL(String wordSL) {
        this.wordSL = wordSL;
    }

    public String word;
    public String wordSL;
    public String translation;
    public String extra;
    public String translationSL;
    public final String pronun;
    public final String grammar;
    public final String example1;
    public String example1SL;
    public String example2;
    public String example2SL;
    public String example3;
    public String example3SL;
    public String vocabularyType;
    public boolean seen;
    public boolean removable;
    public String level;
    public int databasePosition;
    public int favNum;


    public String isFavorite;
    public int number;
    public int position;
    public String isLearned;

    public String getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(String isLearned) {
        this.isLearned = isLearned;
    }

    public Word(String word, String translation, String extra, String pronun, String grammar, String example1, String example2, String example3, String vocabularyType, int position, String isLearned, String isFavorite) {
        this.word = word;
        this.translation = translation;
        this.extra = extra;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.vocabularyType = vocabularyType;
        this.position = position;
        this.isLearned = isLearned;
        this.isFavorite = isFavorite;

    }

    public Word(String word, String translation, String extra, String pronun, String grammar, String example1, String example2, String example3, int databasePosition, String level) {
        this.word = word;
        this.translation = translation;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.databasePosition = databasePosition;
        this.level = level;
        this.extra = extra;
    }

    public Word(String word, String translation, String pronun, String grammar, String example1, String example2, String example3) {
        this(word,translation,pronun,grammar,example1,example2,example3,"");

    }
    public Word(String word, String translation,String extra, String pronun, String grammar, String example1, String example2, String example3,String nothing) {
        this(word,translation,pronun,grammar,example1,example2,example3,"");
        this.extra = extra;

    }

    public Word(String word, String translation,String extra, String pronun, String grammar, String example1, String level,int favNum,int number) {
        this.word = word;
        this.translation = translation;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.level = level;
        this.extra = extra;
        this.favNum = favNum;
        this.number = number;


    }

    public Word(String word, String translation,String extra, String pronun, String grammar, String example1, String example2, String example3, String level,int favNum,int number) {
        this.word = word;
        this.translation = translation;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.level = level;
        this.extra = extra;
        this.favNum = favNum;
        this.number = number;


    }

    public Word(String word, String translation, String pronun, String grammar, String example1, String level) {
        this.word = word;
        this.translation = translation;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.level = level;
        this.extra = extra;

    }

    public Word( String word, String translation, String pronun, String grammar, String example1, String example2, String example3,String level) {
        this.word = word;
        this.translation = translation;
        this.pronun = pronun;
        this.grammar = grammar;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.level = level;
    }
    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String isFavorite() {
        return isFavorite;
    }

    public int getFavNum() {
        return favNum;
    }

    public void setFavNum(int favNum) {
        this.favNum = favNum;
    }

    public void setFavorite(String favorite) {
        isFavorite = favorite;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLevel() {
        return level;
    }

    public String getPronun() {
        return pronun;
    }

    public String getGrammar() {
        return grammar;
    }

    public String getExample1() {
        return example1;
    }

    public String getExample2() {
        return example2;
    }

    public String getExample3() {
        return example3;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count += count;
    }
    public void setCountToZero(int count) {
        this.count = count;
    }



    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getWord() {

        if( word == null){
            return "null";
        }else {
            return word;
        }

    }

    public String getTranslation() {

        if(translation == null){
            return "null";
        }else {
            return translation;
        }

    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTranslationSL() {

        if(translationSL == null){
            return "null";
        }else {
            return translationSL;
        }

    }

    public void setTranslationSL(String translationSL) {
        this.translationSL = translationSL;
    }

    public String getExample1SL() {
        return example1SL;
    }

    public void setExample1SL(String example1SL) {
        this.example1SL = example1SL;
    }

    public String getExample2SL() {
        return example2SL;
    }

    public void setExample2SL(String example2SL) {
        this.example2SL = example2SL;
    }

    public String getExample3SL() {
        return example3SL;
    }

    public void setExample3SL(String example3SL) {
        this.example3SL = example3SL;
    }
}
