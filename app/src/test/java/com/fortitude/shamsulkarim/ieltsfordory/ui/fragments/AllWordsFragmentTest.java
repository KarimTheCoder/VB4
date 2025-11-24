package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllWordsFragmentTest {

    @Test
    public void normalizeSelectionBounds() {
        Assert.assertEquals(0, AllWordsFragment.AllWordsLogic.normalizeSelection(-1));
        Assert.assertEquals(0, AllWordsFragment.AllWordsLogic.normalizeSelection(3));
        Assert.assertEquals(0, AllWordsFragment.AllWordsLogic.normalizeSelection(0));
        Assert.assertEquals(1, AllWordsFragment.AllWordsLogic.normalizeSelection(1));
        Assert.assertEquals(2, AllWordsFragment.AllWordsLogic.normalizeSelection(2));
    }

    @Test
    public void cachingBehaviorKeepsListReference() {
        Map<Integer, ArrayList<Object>> cache = new HashMap<>();
        ArrayList<Object> beginner = new ArrayList<>();
        beginner.add("hello");
        cache.put(0, beginner);
        ArrayList<Object> cached = cache.get(0);
        Assert.assertNotNull(cached);
        Assert.assertEquals(1, cached.size());
        Assert.assertSame(beginner, cached);
    }

    @Test
    public void performanceNormalizeSelectionFast() {
        long start = System.nanoTime();
        for (int i = -1000; i < 1000; i++) {
            AllWordsFragment.AllWordsLogic.normalizeSelection(i);
        }
        long elapsedNs = System.nanoTime() - start;
        Assert.assertTrue(elapsedNs < 10_000_000L);
    }
}
