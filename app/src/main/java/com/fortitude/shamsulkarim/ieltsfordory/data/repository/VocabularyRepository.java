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

        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getBeginnerLearnedWords());
        words.addAll(toeflDataSource.getBeginnerLearnedWords());
        words.addAll(satDataSource.getBeginnerLearnedWords());
        words.addAll(greDataSource.getBeginnerLearnedWords());
        return words;

    }
    public List<Word> getIntermediateLearnedWords(){

        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getIntermediateLearnedWords());
        words.addAll(toeflDataSource.getIntermediateLearnedWords());
        words.addAll(satDataSource.getIntermediateLearnedWords());
        words.addAll(greDataSource.getIntermediateLearnedWords());
        return words;

    }
    public List<Word> getAdvanceLearnedWords(){

        List<Word> words = new ArrayList<>();
        words.addAll(ieltsDataSource.getAdvanceLearnedWords());
        words.addAll(toeflDataSource.getAdvanceLearnedWords());
        words.addAll(satDataSource.getAdvanceLearnedWords());
        words.addAll(greDataSource.getAdvanceLearnedWords());
        return words;

    }

    // Getting numbers
    public int getBeginnerLearnedCount(){

        return ieltsDataSource.getBeginnerLearnedWords().size()+
                toeflDataSource.getBeginnerLearnedWords().size()+
                satDataSource.getBeginnerLearnedWords().size()+
                greDataSource.getBeginnerLearnedWords().size();


    }

    public int getIntermediateLearnedCount(){

        // Todo write getting learned words for HomeFragment
        return ieltsDataSource.getIntermediateLearnedWords().size()+
                toeflDataSource.getIntermediateLearnedWords().size()+
                satDataSource.getIntermediateLearnedWords().size()+
                greDataSource.getIntermediateLearnedWords().size();


    }

    public int getAdvanceLearnedCount(){

        // Todo write getting learned words for HomeFragment
        return ieltsDataSource.getAdvanceLearnedWords().size()+
                toeflDataSource.getAdvanceLearnedWords().size()+
                satDataSource.getAdvanceLearnedWords().size()+
                greDataSource.getAdvanceLearnedWords().size();


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







}
