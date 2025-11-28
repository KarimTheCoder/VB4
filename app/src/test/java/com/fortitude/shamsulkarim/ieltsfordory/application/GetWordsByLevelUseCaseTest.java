package com.fortitude.shamsulkarim.ieltsfordory.application;

import com.fortitude.shamsulkarim.ieltsfordory.application.usecase.GetWordsByLevelUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.domain.model.Level;
import com.fortitude.shamsulkarim.ieltsfordory.domain.repository.VocabularyRepositoryContract;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GetWordsByLevelUseCaseTest {
    static class FakeRepo implements VocabularyRepositoryContract {
        @Override public List<Word> getBeginnerVocabulary(){ return Arrays.asList(new Word("a","b","","","","","","")); }
        @Override public List<Word> getIntermediateVocabulary(){ return Arrays.asList(new Word("c","d","","","","","","")); }
        @Override public List<Word> getAdvanceVocabulary(){ return Arrays.asList(new Word("e","f","","","","","","")); }
    }

    @Test
    public void executeReturnsCorrectLevel() {
        GetWordsByLevelUseCase uc = new GetWordsByLevelUseCase(new FakeRepo());
        Assert.assertEquals(1, uc.execute(Level.BEGINNER).size());
        Assert.assertEquals(1, uc.execute(Level.INTERMEDIATE).size());
        Assert.assertEquals(1, uc.execute(Level.ADVANCE).size());
    }

    @Test
    public void performanceUnderThreshold() {
        GetWordsByLevelUseCase uc = new GetWordsByLevelUseCase(new FakeRepo());
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            uc.execute(Level.BEGINNER);
            uc.execute(Level.INTERMEDIATE);
            uc.execute(Level.ADVANCE);
        }
        long elapsed = System.nanoTime() - start;
        Assert.assertTrue(elapsed < 50_000_000L);
    }
}
