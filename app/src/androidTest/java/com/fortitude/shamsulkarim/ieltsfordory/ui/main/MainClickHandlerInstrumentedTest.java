package com.fortitude.shamsulkarim.ieltsfordory.ui.main;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainClickHandlerInstrumentedTest {
    @Test
    public void executesRegisteredAction() {
        Context ctx = ApplicationProvider.getApplicationContext();
        View button = new View(ctx);
        MainClickHandler handler = new MainClickHandler();
        final int[] count = {0};
        handler.register(button, new MainClickHandler.Action(){ @Override public void execute(){ count[0]++; } });
        handler.onClick(button);
        assertEquals(1, count[0]);
    }
}

