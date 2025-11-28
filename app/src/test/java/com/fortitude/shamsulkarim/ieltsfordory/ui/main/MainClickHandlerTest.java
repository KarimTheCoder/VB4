package com.fortitude.shamsulkarim.ieltsfordory.ui.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MainClickHandlerTest {
    @Test
    public void dispatchesToRegisteredAction() {
        MainClickHandler handler = new MainClickHandler();
        ObjectView view = new ObjectView();
        final int[] count = {0};
        handler.register(view, new MainClickHandler.Action(){ @Override public void execute(){ count[0]++; } });
        handler.onClick(view);
        assertEquals(1, count[0]);
    }

    static class ObjectView extends android.view.View {
        public ObjectView(){ super(null); }
    }
}

