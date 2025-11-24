package com.fortitude.shamsulkarim.ieltsfordory.utility.sync;

import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.DatabaseApi;
import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDatabaseApi implements DatabaseApi {
    private final DatabaseReference ref;
    public FirebaseDatabaseApi(DatabaseReference ref){ this.ref = ref; }
    @Override
    public void setValue(String userId, FavLearnedState state) {
        ref.child(userId).setValue(state);
    }
}

