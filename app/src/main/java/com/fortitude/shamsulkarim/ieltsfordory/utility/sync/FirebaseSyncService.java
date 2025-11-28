package com.fortitude.shamsulkarim.ieltsfordory.utility.sync;

import com.fortitude.shamsulkarim.ieltsfordory.application.service.SyncService;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.AuthProvider;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.DatabaseApi;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.FavLearnedStateProvider;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.NetworkChecker;

public class FirebaseSyncService implements SyncService {
    private final AuthProvider auth;
    private final DatabaseApi db;
    private final NetworkChecker network;
    private final FavLearnedStateProvider provider;

    public FirebaseSyncService(AuthProvider auth, DatabaseApi db, NetworkChecker network, FavLearnedStateProvider provider){
        this.auth = auth;
        this.db = db;
        this.network = network;
        this.provider = provider;
    }

    @Override
    public void sync() {
        new Thread(() -> {
            String userId = auth.getUserId();
            if (userId != null && network.isConnected()) {
                db.setValue(userId, provider.provide());
            }
        }).start();
    }
}

