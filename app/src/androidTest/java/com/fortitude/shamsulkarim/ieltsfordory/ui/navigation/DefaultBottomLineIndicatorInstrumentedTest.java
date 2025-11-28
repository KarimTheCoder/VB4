package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DefaultBottomLineIndicatorInstrumentedTest {

    @Test
    public void setsActiveVisibilityCorrectly() {
        Context ctx = ApplicationProvider.getApplicationContext();
        View home = new View(ctx);
        View words = new View(ctx);
        View learned = new View(ctx);
        View favorites = new View(ctx);
        View profile = new View(ctx);

        DefaultBottomLineIndicator indicator = new DefaultBottomLineIndicator(home, words, learned, favorites, profile);
        indicator.setActive(words);

        assertEquals(View.INVISIBLE, home.getVisibility());
        assertEquals(View.VISIBLE, words.getVisibility());
        assertEquals(View.INVISIBLE, learned.getVisibility());
        assertEquals(View.INVISIBLE, favorites.getVisibility());
        assertEquals(View.INVISIBLE, profile.getVisibility());
    }
}

