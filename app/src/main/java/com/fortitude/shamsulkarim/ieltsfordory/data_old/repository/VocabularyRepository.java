package com.fortitude.shamsulkarim.ieltsfordory.data_old.repository;

import android.content.Context;
 

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetVocabularyUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetFavoriteWordsUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedWordsUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetUnlearnedWordsUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedCountUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetTotalCountUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase;
import org.koin.java.KoinJavaComponent;

import java.util.List;
 


/**
 * Aggregates vocabulary from IELTS/TOEFL/SAT/GRE data sources.
 * Uses a fixed thread pool to fetch lists in parallel to reduce latency
 * while preserving the existing synchronous API.
 */
public class VocabularyRepository {

    private final GetVocabularyUseCase getVocabularyUseCase;
    private final GetFavoriteWordsUseCase getFavoriteWordsUseCase;
    private final GetLearnedWordsUseCase getLearnedWordsUseCase;
    private final GetUnlearnedWordsUseCase getUnlearnedWordsUseCase;
    private final GetLearnedCountUseCase getLearnedCountUseCase;
    private final GetTotalCountUseCase getTotalCountUseCase;
    private final UpdateFavoriteStateUseCase updateFavoriteStateUseCase;
    private final UpdateLearnStateUseCase updateLearnStateUseCase;
    public VocabularyRepository(Context context){
        this.getVocabularyUseCase = KoinJavaComponent.get(GetVocabularyUseCase.class);
        this.getFavoriteWordsUseCase = KoinJavaComponent.get(GetFavoriteWordsUseCase.class);
        this.getLearnedWordsUseCase = KoinJavaComponent.get(GetLearnedWordsUseCase.class);
        this.getUnlearnedWordsUseCase = KoinJavaComponent.get(GetUnlearnedWordsUseCase.class);
        this.getLearnedCountUseCase = KoinJavaComponent.get(GetLearnedCountUseCase.class);
        this.getTotalCountUseCase = KoinJavaComponent.get(GetTotalCountUseCase.class);
        this.updateFavoriteStateUseCase = KoinJavaComponent.get(UpdateFavoriteStateUseCase.class);
        this.updateLearnStateUseCase = KoinJavaComponent.get(UpdateLearnStateUseCase.class);
    }

    

    // Getting vocabulary data
    /** Fetches beginner-level vocabulary across all sources in parallel. */
    public List<Word> getBeginnerVocabulary(){
        return getVocabularyUseCase.execute("beginner");
    }
    /** Fetches intermediate-level vocabulary across all sources in parallel. */
    public List<Word> getIntermediateVocabulary(){
        return getVocabularyUseCase.execute("intermediate");
    }
    /** Fetches advanced-level vocabulary across all sources in parallel. */
    public List<Word> getAdvanceVocabulary(){
        return getVocabularyUseCase.execute("advance");
    }


    // Get favorite and learned words
    public List<Word> getFavoriteWords(){
        return getFavoriteWordsUseCase.execute();
    }

    public List<Word> getBeginnerLearnedWords(){
        return getLearnedWordsUseCase.execute("beginner");
    }
    public List<Word> getIntermediateLearnedWords(){
        return getLearnedWordsUseCase.execute("intermediate");
    }
    public List<Word> getAdvanceLearnedWords(){
        return getLearnedWordsUseCase.execute("advance");
    }

    public List<Word> getBeginnerUnlearnedWords(){
        return getUnlearnedWordsUseCase.execute("beginner");
    }
    public List<Word> getIntermediateUnlearnedWords(){
        return getUnlearnedWordsUseCase.execute("intermediate");
    }
    public List<Word> getAdvanceUnlearnedWords(){
        return getUnlearnedWordsUseCase.execute("advance");
    }



    // Getting numbers
    public int getBeginnerLearnedCount(){
        return getLearnedCountUseCase.execute("beginner");
    }

    public int getIntermediateLearnedCount(){
        return getLearnedCountUseCase.execute("intermediate");
    }

    public int getAdvanceLearnedCount(){
        return getLearnedCountUseCase.execute("advance");
    }

    public int getTotalBeginnerCount(){
        return getTotalCountUseCase.execute("beginner");
    }

    public int getTotalIntermediateCount(){
        return getTotalCountUseCase.execute("intermediate");
    }

    public int getTotalAdvanceCount(){
        return getTotalCountUseCase.execute("advance");
    }



    // Updating favorite state

    public void updateIELTSFavoriteState(String id, String isFavorite){
        updateFavoriteStateUseCase.execute("IELTS", id, isFavorite);
    }

    public void updateTOEFLFavoriteState(String id, String isFavorite){
        updateFavoriteStateUseCase.execute("TOEFL", id, isFavorite);
    }
    public void updateGREFavoriteState(String id, String isFavorite){
        updateFavoriteStateUseCase.execute("GRE", id, isFavorite);
    }
    public void updateSATFavoriteState(String id, String isFavorite){
        updateFavoriteStateUseCase.execute("SAT", id, isFavorite);
    }


    // Update learn state

    public void updateIELTSLearnState(String id, String isFavorite){
        updateLearnStateUseCase.execute("IELTS", id, isFavorite);
    }
    public void updateTOEFLLearnState(String id, String isFavorite){
        updateLearnStateUseCase.execute("TOEFL", id, isFavorite);
    }
    public void updateSATLearnState(String id, String isFavorite){
        updateLearnStateUseCase.execute("SAT", id, isFavorite);
    }
    public void updateGRELearnState(String id, String isFavorite){
        updateLearnStateUseCase.execute("GRE", id, isFavorite);
    }
}
