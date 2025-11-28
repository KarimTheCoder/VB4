package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentNavigator implements Navigator {
    private final FragmentManager fragmentManager;
    private final int containerId;
    private final BottomLineIndicator indicator;

    public FragmentNavigator(FragmentManager fragmentManager, int containerId, BottomLineIndicator indicator){
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.indicator = indicator;
    }

    @Override
    public void navigate(Fragment fragment, View activeLine) {
        fragmentManager.beginTransaction().replace(containerId, fragment).commit();
        indicator.setActive(activeLine);
    }
}

