package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.pretrain

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.pretrain.PretrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.trainfinished.TrainFinishedActivity
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme

/**
 * Pre-training Activity using Jetpack Compose.
 * Hosts the PretrainScreen composable.
 * 
 * This activity is kept for backward compatibility with legacy Java code
 * that still launches PretrainActivity via Intent.
 */
class PretrainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Lock to portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            VocabularyTheme {
                PretrainScreen(
                    onNavigateBack = { finish() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



