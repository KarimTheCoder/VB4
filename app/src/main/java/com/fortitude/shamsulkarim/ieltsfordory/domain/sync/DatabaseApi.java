package com.fortitude.shamsulkarim.ieltsfordory.domain.sync;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;

public interface DatabaseApi {
    void setValue(String userId, FavLearnedState state);
}

