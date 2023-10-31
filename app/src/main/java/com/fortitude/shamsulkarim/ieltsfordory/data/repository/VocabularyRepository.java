package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.GREDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.IELTSDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.SATDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.TOEFLDataSource;

import java.util.ArrayList;
import java.util.List;


public class VocabularyRepository {

    private final GREDataSource greDataSource;
    private final IELTSDataSource ieltsDataSource;
    private final SATDataSource satDataSource;
    private final TOEFLDataSource toeflDataSource;

    public VocabularyRepository(Context context){

        ieltsDataSource = new IELTSDataSource(context);

        satDataSource = new SATDataSource(context);
        greDataSource = new GREDataSource(context);
        toeflDataSource = new TOEFLDataSource(context);

    }

    // Getting vocabulary data
    public List<Word> getBeginnerVocabulary(){

        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getBeginnerWords());
        words.addAll(toeflDataSource.getBeginnerWords());
        words.addAll(satDataSource.getBeginnerWords());
        words.addAll(greDataSource.getBeginnerWords());


        return words;
    }
    public List<Word> getIntermediateVocabulary(){

        List<Word> words = new ArrayList<>();



        words.addAll(ieltsDataSource.getIntermediateWords());
        words.addAll(toeflDataSource.getIntermediateWords());
        words.addAll(satDataSource.getIntermediateWords());
        words.addAll(greDataSource.getIntermediateWords());

        return words;
    }
    public List<Word> getAdvanceVocabulary(){
        List<Word> words = new ArrayList<>();



        words.addAll(ieltsDataSource.getAdvanceWords());
        words.addAll(toeflDataSource.getAdvanceWords());
        words.addAll(satDataSource.getAdvanceWords());
        words.addAll(greDataSource.getAdvanceWords());

        return words;
    }


    // Get favorite and learned words
    public List<Word> getFavoriteWord(){

        List<Word> words = new ArrayList<>();

        words.addAll(ieltsDataSource.getFavoriteWords());
        words.addAll(toeflDataSource.getFavoriteWords());
        words.addAll(satDataSource.getFavoriteWords());
        words.addAll(greDataSource.getFavoriteWords());


        return words;

    }

    public List<Word> getBeginnerLearnedWords(){
        String isLearned = "True";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(toeflDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(satDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(greDataSource.getBeginnerFilteredWords(isLearned));
        return words;

    }
    public List<Word> getIntermediateLearnedWords(){
        String isLearned = "True";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(toeflDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(satDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(greDataSource.getIntermediateFilteredWords(isLearned));
        return words;

    }
    public List<Word> getAdvanceLearnedWords(){
        String isLearned = "True";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(toeflDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(satDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(greDataSource.getAdvanceFilteredWords(isLearned));
        return words;

    }

    public List<Word> getBeginnerUnlearnedWords(){
        String isLearned = "False";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(toeflDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(satDataSource.getBeginnerFilteredWords(isLearned));
        words.addAll(greDataSource.getBeginnerFilteredWords(isLearned));
        return words;

    }
    public List<Word> getIntermediateUnlearnedWords(){
        String isLearned = "False";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(toeflDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(satDataSource.getIntermediateFilteredWords(isLearned));
        words.addAll(greDataSource.getIntermediateFilteredWords(isLearned));
        return words;

    }
    public List<Word> getAdvanceUnlearnedWords(){
        String isLearned = "False";
        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(toeflDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(satDataSource.getAdvanceFilteredWords(isLearned));
        words.addAll(greDataSource.getAdvanceFilteredWords(isLearned));
        return words;

    }



    // Getting numbers
    public int getBeginnerLearnedCount(){

        String isLearned = "True";
        return ieltsDataSource.getBeginnerFilteredWords(isLearned).size()+
                toeflDataSource.getBeginnerFilteredWords(isLearned).size()+
                satDataSource.getBeginnerFilteredWords(isLearned).size()+
                greDataSource.getBeginnerFilteredWords(isLearned).size();


    }

    public int getIntermediateLearnedCount(){

        String isLearned = "True";
        return ieltsDataSource.getIntermediateFilteredWords(isLearned).size()+
                toeflDataSource.getIntermediateFilteredWords(isLearned).size()+
                satDataSource.getIntermediateFilteredWords(isLearned).size()+
                greDataSource.getIntermediateFilteredWords(isLearned).size();


    }

    public int getAdvanceLearnedCount(){

        String isLearned = "True";
        return ieltsDataSource.getAdvanceFilteredWords(isLearned).size()+
                toeflDataSource.getAdvanceFilteredWords(isLearned).size()+
                satDataSource.getAdvanceFilteredWords(isLearned).size()+
                greDataSource.getAdvanceFilteredWords(isLearned).size();


    }

    public int getTotalBeginnerCount(){
        return ieltsDataSource.getBeginnerWordCount()+
                toeflDataSource.getBeginnerWordCount()+
                satDataSource.getBeginnerWordCount()+
                greDataSource.getBeginnerWordCount();
    }

    public int getTotalIntermediateCount(){
        return ieltsDataSource.getIntermediateWordCount()+
                toeflDataSource.getIntermediateWordCount()+
                satDataSource.getIntermediateWordCount()+
                greDataSource.getIntermediateWordCount();
    }

    public int getTotalAdvanceCount(){
        return ieltsDataSource.getAdvanceWordCount()+
                toeflDataSource.getAdvanceWordCount()+
                satDataSource.getAdvanceWordCount()+
                greDataSource.getAdvanceWordCount();

    }



    // Updating favorite state

    public void updateIELTSFavoriteState(String id, String isFavorite){
        ieltsDataSource.updateFavorite(id,isFavorite);
    }

    public void updateTOEFLFavoriteState(String id, String isFavorite){

        toeflDataSource.updateFavorite(id,isFavorite);
    }
    public void updateGREFavoriteState(String id, String isFavorite){
        greDataSource.updateFavorite(id,isFavorite);
    }
    public void updateSATFavoriteState(String id, String isFavorite){
        satDataSource.updateFavorite(id,isFavorite);
    }


    // Update learn state

    public void updateIELTSLearnState(String id, String isFavorite){
        ieltsDataSource.updateLearnState(id,isFavorite);
    }
    public void updateTOEFLLearnState(String id, String isFavorite){
        toeflDataSource.updateLearnState(id,isFavorite);
    }
    public void updateSATLearnState(String id, String isFavorite){
        satDataSource.updateLearnState(id,isFavorite);
    }
    public void updateGRELearnState(String id, String isFavorite){
        greDataSource.updateLearnState(id,isFavorite);
    }






}
