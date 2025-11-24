package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation;

import android.view.View;

public class DefaultBottomLineIndicator implements BottomLineIndicator {
    private final View home;
    private final View words;
    private final View learned;
    private final View favorites;
    private final View profile;

    public DefaultBottomLineIndicator(View home, View words, View learned, View favorites, View profile) {
        this.home = home;
        this.words = words;
        this.learned = learned;
        this.favorites = favorites;
        this.profile = profile;
    }

    @Override
    public void setActive(View active) {
        home.setVisibility(active == home ? View.VISIBLE : View.INVISIBLE);
        words.setVisibility(active == words ? View.VISIBLE : View.INVISIBLE);
        learned.setVisibility(active == learned ? View.VISIBLE : View.INVISIBLE);
        favorites.setVisibility(active == favorites ? View.VISIBLE : View.INVISIBLE);
        profile.setVisibility(active == profile ? View.VISIBLE : View.INVISIBLE);
    }
}

