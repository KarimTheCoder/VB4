package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Repository class to handle all Firebase operations.
 * This class centralizes Firebase authentication and database operations,
 * providing a clean separation of concerns from the UI layer.
 */
public class FirebaseRepository {

    private final DatabaseReference databaseReference;
    private final Context context;

    /**
     * Constructor for FirebaseRepository
     * 
     * @param context Application context for connectivity checks
     */
    public FirebaseRepository(Context context) {
        this.context = context;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = firebaseDatabase.getReference();
    }

    /**
     * Get the Firebase database reference
     * 
     * @return DatabaseReference instance
     */
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * Check if device is connected to network
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return ConnectivityHelper.isConnectedToNetwork(context);
    }

    /**
     * Update user data to Firebase
     * 
     * @param userId          The user ID to update data for
     * @param favLearnedState The user's favorite and learned state data
     * @param listener        Callback for completion (can be null)
     */
    public void updateUserData(String userId, FavLearnedState favLearnedState, OnCompleteListener<Void> listener) {
        if (userId != null && isConnected()) {
            try {
                if (listener != null) {
                    databaseReference.child(userId)
                            .setValue(favLearnedState)
                            .addOnCompleteListener(listener);
                } else {
                    databaseReference.child(userId)
                            .setValue(favLearnedState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update user data to Firebase (simplified version without callback)
     * 
     * @param userId          The user ID to update data for
     * @param favLearnedState The user's favorite and learned state data
     */
    public void updateUserData(String userId, FavLearnedState favLearnedState) {
        updateUserData(userId, favLearnedState, null);
    }

    public void addChildEventListener(String userId, ChildEventListener listener) {
        if (userId != null) {
            databaseReference.child(userId).addChildEventListener(listener);
        }
    }

    public void removeChildEventListener(String userId, ChildEventListener listener) {
        if (userId != null && listener != null) {
            databaseReference.child(userId).removeEventListener(listener);
        }
    }
}
