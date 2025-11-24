package com.fortitude.shamsulkarim.ieltsfordory.ui.main;

import android.view.View;
import java.util.HashMap;
import java.util.Map;

public class MainClickHandler implements View.OnClickListener {
    public interface Action { void execute(); }

    private final Map<View, Action> actions = new HashMap<>();

    public void register(View button, Action action){
        actions.put(button, action);
    }

    @Override
    public void onClick(View v) {
        Action a = actions.get(v);
        if (a != null) a.execute();
    }
}

