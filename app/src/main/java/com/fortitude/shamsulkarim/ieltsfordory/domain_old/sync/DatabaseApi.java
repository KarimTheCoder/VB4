package com.fortitude.shamsulkarim.ieltsfordory.domain_old.sync;

import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState;

public interface DatabaseApi {
    void setValue(String userId, FavLearnedState state);
}

