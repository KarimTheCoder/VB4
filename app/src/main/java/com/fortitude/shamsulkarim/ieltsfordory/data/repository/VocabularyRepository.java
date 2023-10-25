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
        //words.addAll(ieltsDataSource.getBeginnerWords());
        //words.addAll(toeflDataSource.getBeginnerWords());
       // words.addAll(satDataSource.getBeginnerWords());
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


    // Getting favorite state
    public List<String> getIELTSFavorite(){

        return ieltsDataSource.getFavoritePosition();
    }
    public List<String> getTOEFLFavorite(){
        return toeflDataSource.getFavoritePosition();
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
