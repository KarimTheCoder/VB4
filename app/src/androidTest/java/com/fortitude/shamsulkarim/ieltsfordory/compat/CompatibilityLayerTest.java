package com.fortitude.shamsulkarim.ieltsfordory.compat;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.fortitude.shamsulkarim.ieltsfordory.compat.v1.VocabularyRepositoryV1;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CompatibilityLayerTest {
    @Test
    public void repoV1MatchesDirectRepository() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        VocabularyRepository direct = new VocabularyRepository(ctx);
        VocabularyRepositoryV1 v1 = new VocabularyRepositoryV1(ctx);
        Assert.assertEquals(direct.getBeginnerVocabulary().size(), v1.getBeginnerVocabulary().size());
        Assert.assertEquals(direct.getIntermediateVocabulary().size(), v1.getIntermediateVocabulary().size());
        Assert.assertEquals(direct.getAdvanceVocabulary().size(), v1.getAdvanceVocabulary().size());
    }
}
