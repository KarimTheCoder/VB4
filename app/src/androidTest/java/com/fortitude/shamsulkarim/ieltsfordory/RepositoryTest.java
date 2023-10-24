package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RepositoryTest {

    private VocabularyRepository repository;

    @Before
    public void setUp(){

        Context context = ApplicationProvider.getApplicationContext();

        repository = new VocabularyRepository(context);


    }


    @Test
    public void GREBeginnerVocabularyCount(){

        int count = repository.getBeginnerVocabulary().size();


        Log.i("Test", "Count: "+count);

        Assert.assertEquals(count,count);

    }

    @Test
    public void GREIntermediateVocabularyCount(){

        int count = repository.getIntermediateVocabulary().size();


        Log.i("Test", "Count: "+count);

        Assert.assertEquals(count,count);

    }
    @Test
    public void GREAdvanceVocabularyCount(){

        int count = repository.getAdvanceVocabulary().size();


        Log.i("Test", "Count: "+count);

        Assert.assertEquals(count,count);

    }


}
