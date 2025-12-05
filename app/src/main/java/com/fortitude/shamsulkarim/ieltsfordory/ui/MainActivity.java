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
import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.IsUserAuthenticatedUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.GetCurrentUserUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.data_old.sync.FirebaseSyncManager;
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.UpdateUserDataUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetFavLearnedStateUseCase;
import org.koin.java.KoinJavaComponent;
import com.fortitude.shamsulkarim.ieltsfordory.ui.words.AllWordsFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.favorites.FavoriteFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.HomeFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.learned.LearnedFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.ProfileFragment;
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UpdateUserDataUseCase updateUserDataUseCase;
    private GetFavLearnedStateUseCase getFavLearnedStateUseCase;
    private IsUserAuthenticatedUseCase isUserAuthenticatedUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;
    private FirebaseSyncManager syncManager;
    private Toast toast;
    private long lastBackPressTime = 0;
    private boolean connected;
    private IsConnectedUseCase isConnectedUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        isConnectedUseCase = KoinJavaComponent.get(IsConnectedUseCase.class);
        connected = isConnectedUseCase.execute();

        // BottomNavigation bottomNavigation;
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        if (!sp.contains("soundState")) {

            sp.edit().putBoolean("soundState", true).apply();
            sp.edit().putInt("totalCorrects", 0).apply();
            sp.edit().putInt("noshowads", 0).apply();

        }

        // Initialize repositories
        isUserAuthenticatedUseCase = KoinJavaComponent.get(IsUserAuthenticatedUseCase.class);
        getCurrentUserUseCase = KoinJavaComponent.get(GetCurrentUserUseCase.class);
        updateUserDataUseCase = KoinJavaComponent.get(UpdateUserDataUseCase.class);
        getFavLearnedStateUseCase = KoinJavaComponent.get(GetFavLearnedStateUseCase.class);
        AddChildEventListenerUseCase addChildEventListenerUseCase = KoinJavaComponent.get(AddChildEventListenerUseCase.class);
        syncManager = new FirebaseSyncManager(this, addChildEventListenerUseCase);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // firebase auto sync
        if (isUserAuthenticatedUseCase.execute() && connected) {
            try {
                FirebaseUser currentUser = getCurrentUserUseCase.execute();
                if (currentUser != null) {
                    syncManager.startSync(currentUser.getUid(), null);
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

        // Get aggregated state from LearningProgressRepository
        FavLearnedState favLearnedState = getFavLearnedStateUseCase.execute(userName);

        // Upload to Firebase
        try {
            if (isUserAuthenticatedUseCase.execute()) {
                updateUserDataUseCase.execute(getCurrentUserUseCase.execute().getUid(), favLearnedState, null);
            }
        } catch (Exception e) {
            Toast.makeText(this, "update failure", Toast.LENGTH_SHORT).show();
        }
    }

    // Overriden Methods
    @Override
    protected void onStop() {
        super.onStop();

        if (isUserAuthenticatedUseCase.execute() && connected) {
            updateFirebase();
        }
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
        return isConnectedUseCase.execute();
    }
}
