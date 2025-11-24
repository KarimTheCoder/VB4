package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Repository class to handle all Firebase operations.
 * This class centralizes Firebase authentication and database operations,
 * providing a clean separation of concerns from the UI layer.
 */
public class FirebaseRepository {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final Context context;

    /**
     * Constructor for FirebaseRepository
     * 
     * @param context Application context for connectivity checks
     */
    public FirebaseRepository(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = firebaseDatabase.getReference();
    }

    /**
     * Get the current authenticated Firebase user
     * 
     * @return FirebaseUser if authenticated, null otherwise
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Check if a user is currently authenticated
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
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
     * Get the FirebaseAuth instance
     * 
     * @return FirebaseAuth instance
     */
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
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
     * @param favLearnedState The user's favorite and learned state data
     * @param listener        Callback for completion (can be null)
     */
    public void updateUserData(FavLearnedState favLearnedState, OnCompleteListener<Void> listener) {
        FirebaseUser currentUser = getCurrentUser();

        if (currentUser != null && isConnected()) {
            try {
                if (listener != null) {
                    databaseReference.child(currentUser.getUid())
                            .setValue(favLearnedState)
                            .addOnCompleteListener(listener);
                } else {
                    databaseReference.child(currentUser.getUid())
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
     * @param favLearnedState The user's favorite and learned state data
     */
    public void updateUserData(FavLearnedState favLearnedState) {
        updateUserData(favLearnedState, null);
    }
}
