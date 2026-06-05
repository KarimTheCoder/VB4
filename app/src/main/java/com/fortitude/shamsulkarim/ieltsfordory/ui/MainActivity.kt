package com.fortitude.shamsulkarim.ieltsfordory.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.data_old.sync.FirebaseSyncManager
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.GetCurrentUserUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.IsUserAuthenticatedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.UpdateUserDataUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetFavLearnedStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.ui.navigation.AppNavigation
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import org.koin.android.ext.android.inject
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.ThemeRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * Main Activity using Jetpack Compose.
 * Hosts the AppNavigation composable with bottom navigation.
 */
class MainActivity : ComponentActivity() {

    // Use cases injected via Koin
    private val isConnectedUseCase: IsConnectedUseCase by inject()
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase by inject()
    private val getCurrentUserUseCase: GetCurrentUserUseCase by inject()
    private val updateUserDataUseCase: UpdateUserDataUseCase by inject()
    private val getFavLearnedStateUseCase: GetFavLearnedStateUseCase by inject()
    private val addChildEventListenerUseCase: AddChildEventListenerUseCase by inject()
    private val themeRepository: ThemeRepository by inject()

    private lateinit var syncManager: FirebaseSyncManager
    private var toast: Toast? = null
    private var lastBackPressTime: Long = 0
    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Lock to portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Check connectivity
        isConnected = isConnectedUseCase.execute()

        // Initialize default SharedPreferences if needed
        initializeDefaultPreferences()

        // Initialize Firebase sync manager
        syncManager = FirebaseSyncManager(this, addChildEventListenerUseCase)

        // Start Firebase auto-sync if authenticated and connected
        startFirebaseSync()

        // Setup double-back-to-exit behavior
        setupBackPressHandler()

        // Set Compose content
        setContent {
            val themeMode by themeRepository.themeMode.collectAsState(initial = 0)
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (themeMode) {
                0 -> false // Light
                1 -> true  // Dark
                else -> isSystemDark // System
            }

            VocabularyTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    /**
     * Initialize default SharedPreferences values if not already set.
     */
    private fun initializeDefaultPreferences() {
        val sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE)
        
        if (!sp.contains("soundState")) {
            sp.edit().apply {
                putBoolean("soundState", true)
                putInt("totalCorrects", 0)
                putInt("noshowads", 0)
                apply()
            }
        }
    }

    /**
     * Start Firebase sync if user is authenticated and connected.
     */
    private fun startFirebaseSync() {
        if (isUserAuthenticatedUseCase.execute() && isConnected) {
            try {
                val currentUser = getCurrentUserUseCase.execute()
                currentUser?.let { user ->
                    syncManager.startSync(user.uid, null)
                }
            } catch (e: NullPointerException) {
                Toast.makeText(this, "Reference exception", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Upload user data to Firebase.
     */
    private fun updateFirebase() {
        val sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE)
        val userName = sp.getString("userName", "Boo") ?: "Boo"

        // Get aggregated state from LearningProgressRepository
        val favLearnedState: FavLearnedState = getFavLearnedStateUseCase.execute(userName)

        // Upload to Firebase
        try {
            if (isUserAuthenticatedUseCase.execute()) {
                getCurrentUserUseCase.execute()?.let { user ->
                    updateUserDataUseCase.execute(user.uid, favLearnedState, null)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Update failure", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Setup double-back-to-exit confirmation.
     */
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                
                if (lastBackPressTime < currentTime - 4000) {
                    toast = Toast.makeText(
                        applicationContext,
                        "Press back again to close this app",
                        Toast.LENGTH_LONG
                    )
                    toast?.show()
                    lastBackPressTime = currentTime
                } else {
                    toast?.cancel()
                    finish()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        
        // Sync data to Firebase when leaving the app
        if (isUserAuthenticatedUseCase.execute() && isConnected) {
            updateFirebase()
        }
    }
}



