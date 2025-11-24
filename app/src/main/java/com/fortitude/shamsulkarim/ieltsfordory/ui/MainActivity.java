package com.fortitude.shamsulkarim.ieltsfordory.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.FirebaseRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.WordRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.sync.FirebaseSyncManager;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.AllWordsFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.FavoriteFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.HomeFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.LearnedFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.ProfileFragment;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseRepository firebaseRepository;
    private FirebaseSyncManager syncManager;
    private WordRepository wordRepository;
    private Toast toast;
    private long lastBackPressTime = 0;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                toastMsg = "Xlarge";
                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }

        connected = isOnline();

        // BottomNavigation bottomNavigation;
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        if (!sp.contains("soundState")) {

            sp.edit().putBoolean("soundState", true).apply();
            sp.edit().putInt("totalCorrects", 0).apply();
            sp.edit().putInt("noshowads", 0).apply();

        }

        // Initialize repositories
        firebaseRepository = new FirebaseRepository(this);
        syncManager = new FirebaseSyncManager();
        wordRepository = new WordRepository(this);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // firebase auto sync
        if (firebaseRepository.isUserAuthenticated() && connected) {
            try {
                FirebaseUser currentUser = firebaseRepository.getCurrentUser();
                if (currentUser != null) {
                    syncManager.startAutoSync(
                            firebaseRepository.getDatabaseReference(),
                            currentUser.getUid());
                }
            } catch (NullPointerException n) {
                Toast.makeText(this, "Reference exception", Toast.LENGTH_SHORT).show();
            }
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (lastBackPressTime < System.currentTimeMillis() - 4000) {
                    toast = Toast.makeText(getApplicationContext(), "Press back again to close this app",
                            Toast.LENGTH_LONG);
                    toast.show();
                    lastBackPressTime = System.currentTimeMillis();

                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    finish();
                }
            }
        });

    }

    // Firebase
    // ----------------------------------------------------------------------------------------------
    private void updateFirebase() {
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        String userName = sp.getString("userName", "Boo");

        // Get aggregated state from WordRepository
        FavLearnedState favLearnedState = wordRepository.getFavLearnedState(userName);

        // Upload to Firebase
        try {
            firebaseRepository.updateUserData(favLearnedState);
        } catch (Exception e) {
            Toast.makeText(this, "update failure", Toast.LENGTH_SHORT).show();
        }
    }

    // Overriden Methods
    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRepository.isUserAuthenticated() && connected) {
            updateFirebase();
        }

        // Stop auto-sync when activity stops
        syncManager.stopAutoSync();

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_words) {
                selectedFragment = new AllWordsFragment();
            } else if (itemId == R.id.navigation_learned) {
                selectedFragment = new LearnedFragment();
            } else if (itemId == R.id.navigation_favorite) {
                selectedFragment = new FavoriteFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frag, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default selection (Home)
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private boolean isOnline() {

        return ConnectivityHelper.isConnectedToNetwork(this);

    }
}
