package com.fortitude.shamsulkarim.ieltsfordory.utility.sync;

import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.AuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthProvider implements AuthProvider {
    private final FirebaseAuth auth;
    public FirebaseAuthProvider(FirebaseAuth auth){ this.auth = auth; }
    @Override
    public String getUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}

