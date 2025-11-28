package com.fortitude.shamsulkarim.ieltsfordory.utility.sync;

import com.fortitude.shamsulkarim.ieltsfordory.application.service.SyncService;
import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.AuthProvider;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.DatabaseApi;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.FavLearnedStateProvider;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.NetworkChecker;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class FirebaseSyncServiceTest {

    static class FakeAuth implements AuthProvider {
        private final String id;
        FakeAuth(String id){ this.id = id; }
        @Override public String getUserId(){ return id; }
    }

    static class FakeNetwork implements NetworkChecker {
        private final boolean connected;
        FakeNetwork(boolean connected){ this.connected = connected; }
        @Override public boolean isConnected(){ return connected; }
    }

    static class FakeProvider implements FavLearnedStateProvider {
        @Override public FavLearnedState provide(){ return new FavLearnedState("n","a","b","c","g","af","bf","cf","gf"); }
    }

    static class CaptureDb implements DatabaseApi {
        final AtomicReference<FavLearnedState> captured = new AtomicReference<>();
        String uid;
        @Override public void setValue(String userId, FavLearnedState state){ uid = userId; captured.set(state);}    }

    @Test
    public void syncCallsDbWhenUserAndNetworkOk() throws InterruptedException {
        CaptureDb db = new CaptureDb();
        SyncService svc = new FirebaseSyncService(new FakeAuth("U"), db, new FakeNetwork(true), new FakeProvider());
        svc.sync();
        Thread.sleep(100);
        assertEquals("U", db.uid);
        assertNotNull(db.captured.get());
    }

    @Test
    public void syncDoesNothingWhenNoUser() throws InterruptedException {
        CaptureDb db = new CaptureDb();
        SyncService svc = new FirebaseSyncService(new FakeAuth(null), db, new FakeNetwork(true), new FakeProvider());
        svc.sync();
        Thread.sleep(100);
        assertNull(db.captured.get());
    }

    @Test
    public void syncDoesNothingWhenOffline() throws InterruptedException {
        CaptureDb db = new CaptureDb();
        SyncService svc = new FirebaseSyncService(new FakeAuth("U"), db, new FakeNetwork(false), new FakeProvider());
        svc.sync();
        Thread.sleep(100);
        assertNull(db.captured.get());
    }
}

