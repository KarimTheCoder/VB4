package com.fortitude.shamsulkarim.ieltsfordory.viewpager;

import androidx.cardview.widget.CardView;

/**
 * Created by shamsul on 2/18/18.
 */

public interface CardAdapter {



    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
