package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;
import android.util.Log;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.GREDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.IELTSDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.SATDataSource;
import com.fortitude.shamsulkarim.ieltsfordory.data.source.TOEFLDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Aggregates vocabulary from IELTS/TOEFL/SAT/GRE data sources.
 * Uses a fixed thread pool to fetch lists in parallel to reduce latency
 * while preserving the existing synchronous API.
 */
public class VocabularyRepository {

    private final GREDataSource greDataSource;
    private final IELTSDataSource ieltsDataSource;
    private final SATDataSource satDataSource;
    private final TOEFLDataSource toeflDataSource;

    private final ExecutorService pool;
    public VocabularyRepository(Context context){

        ieltsDataSource = new IELTSDataSource(context);

        satDataSource = new SATDataSource(context);
        greDataSource = new GREDataSource(context);
        toeflDataSource = new TOEFLDataSource(context);
        pool = Executors.newFixedThreadPool(4);

    }

    private List<Word> invokeAndMerge(List<Callable<List<Word>>> tasks) {
        try {
            List<Future<List<Word>>> futures = pool.invokeAll(tasks);
            List<Word> merged = new ArrayList<>();
            for (Future<List<Word>> f : futures) {
                merged.addAll(f.get());
            }
            return merged;
        } catch (Exception e) {
            Log.w("VocabularyRepository", "Parallel fetch failed, falling back", e);
            List<Word> merged = new ArrayList<>();
            for (Callable<List<Word>> t : tasks) {
                try { merged.addAll(t.call()); } catch (Exception ignored) { }
            }
            return merged;
        }
    }

    // Getting vocabulary data
    /** Fetches beginner-level vocabulary across all sources in parallel. */
    public List<Word> getBeginnerVocabulary(){
        List<Callable<List<Word>>> tasks = new ArrayList<>();
        tasks.add(() -> ieltsDataSource.getBeginnerWords());
        tasks.add(() -> toeflDataSource.getBeginnerWords());
        tasks.add(() -> satDataSource.getBeginnerWords());
        tasks.add(() -> greDataSource.getBeginnerWords());
        return invokeAndMerge(tasks);
    }
    /** Fetches intermediate-level vocabulary across all sources in parallel. */
    public List<Word> getIntermediateVocabulary(){
        List<Callable<List<Word>>> tasks = new ArrayList<>();
        tasks.add(() -> ieltsDataSource.getIntermediateWords());
        tasks.add(() -> toeflDataSource.getIntermediateWords());
        tasks.add(() -> satDataSource.getIntermediateWords());
        tasks.add(() -> greDataSource.getIntermediateWords());
        return invokeAndMerge(tasks);
    }
    /** Fetches advanced-level vocabulary across all sources in parallel. */
    public List<Word> getAdvanceVocabulary(){
        List<Callable<List<Word>>> tasks = new ArrayList<>();
        tasks.add(() -> ieltsDataSource.getAdvanceWords());
        tasks.add(() -> toeflDataSource.getAdvanceWords());
        tasks.add(() -> satDataSource.getAdvanceWords());
        tasks.add(() -> greDataSource.getAdvanceWords());
        return invokeAndMerge(tasks);
    }


    // Get favorite and learned words
    public List<Word> getFavoriteWords(){

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
