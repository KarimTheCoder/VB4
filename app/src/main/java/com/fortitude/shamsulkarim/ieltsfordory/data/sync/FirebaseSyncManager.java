package com.fortitude.shamsulkarim.ieltsfordory.data.sync;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

/**
 * Manager class to handle Firebase auto-sync functionality.
 * This class manages the lifecycle of Firebase ChildEventListener
 * for real-time data synchronization.
 */
public class FirebaseSyncManager {

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private String currentUserId;

    /**
     * Callback interface for sync events
     */
    public interface SyncCallback {
        void onChildAdded(@NotNull DataSnapshot dataSnapshot, String previousChildName);

        void onChildChanged(@NotNull DataSnapshot dataSnapshot, String previousChildName);

        void onChildRemoved(@NotNull DataSnapshot dataSnapshot);

        void onChildMoved(@NotNull DataSnapshot dataSnapshot, String previousChildName);

        void onCancelled(@NotNull DatabaseError databaseError);
    }

    /**
     * Start auto-sync for a specific user
     * 
     * @param databaseReference Firebase database reference
     * @param userId            User ID to sync data for
     * @param callback          Callback for sync events (can be null for default
     *                          behavior)
     */
    public void startAutoSync(DatabaseReference databaseReference, String userId, final SyncCallback callback) {
        // Stop any existing sync first
        stopAutoSync();

        this.databaseReference = databaseReference;
        this.currentUserId = userId;

        // Create the child event listener
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                if (callback != null) {
                    callback.onChildAdded(dataSnapshot, s);
                }
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                if (callback != null) {
                    callback.onChildChanged(dataSnapshot, s);
                } else {
                    // Default behavior - just get the value
                    dataSnapshot.getValue(String.class);
                    dataSnapshot.getKey();
                }
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
                if (callback != null) {
                    callback.onChildRemoved(dataSnapshot);
                }
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
                if (callback != null) {
                    callback.onChildMoved(dataSnapshot, s);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                if (callback != null) {
                    callback.onCancelled(databaseError);
                }
            }
        };

        // Attach the listener to the user's node
        try {
            databaseReference.child(userId).addChildEventListener(childEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start auto-sync with default callback behavior
     * 
     * @param databaseReference Firebase database reference
     * @param userId            User ID to sync data for
     */
    public void startAutoSync(DatabaseReference databaseReference, String userId) {
        startAutoSync(databaseReference, userId, null);
    }

    /**
     * Stop auto-sync and remove the listener
     */
    public void stopAutoSync() {
        if (childEventListener != null && databaseReference != null && currentUserId != null) {
            try {
                databaseReference.child(currentUserId).removeEventListener(childEventListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            childEventListener = null;
            currentUserId = null;
        }
    }

    /**
     * Check if auto-sync is currently active
     * 
     * @return true if sync is active, false otherwise
     */
    public boolean isSyncActive() {
        return childEventListener != null;
    }
}
