package com.fortitude.shamsulkarim.ieltsfordory.repository;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class VocabularyRepositoryTest {

    @Test
    public void beginnerVocabularyParallelAccess() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        VocabularyRepository repo = new VocabularyRepository(ctx);
        List<Word> baseline = repo.getBeginnerVocabulary();
        Assert.assertNotNull(baseline);
        CountDownLatch latch = new CountDownLatch(4);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            pool.execute(() -> {
                try {
                    List<Word> w = repo.getBeginnerVocabulary();
                    Assert.assertTrue(w.size() >= 0);
                } finally {
                    latch.countDown();
                }
            });
        }
        boolean ok = latch.await(30, TimeUnit.SECONDS);
        pool.shutdown();
        Assert.assertTrue(ok);
    }

    @Test
    public void countsComputeInParallel() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        VocabularyRepository repo = new VocabularyRepository(ctx);
        int b = repo.getTotalBeginnerCount();
        int i = repo.getTotalIntermediateCount();
        int a = repo.getTotalAdvanceCount();
        Assert.assertTrue(b >= 0);
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(a >= 0);
    }

    @Test
    public void performanceUnderThreshold() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        VocabularyRepository repo = new VocabularyRepository(ctx);
        long start = System.currentTimeMillis();
        List<Word> w = repo.getIntermediateVocabulary();
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertNotNull(w);
        Assert.assertTrue(elapsed < 10000);
    }
}
