package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.trainfinished

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.trainfinished.TrainFinishedScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.train.NewTrain
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme

/**
 * Training Finished Activity using Jetpack Compose.
 * Hosts the TrainFinishedScreen composable.
 * 
 * This activity is kept for backward compatibility with legacy Java code
 * that launches TrainFinishedActivity via Intent.
 */
class TrainFinishedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Lock to portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            VocabularyTheme {
                TrainFinishedScreen(
                    onNavigateHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onTrainAgain = {
                        startActivity(Intent(this, NewTrain::class.java))
                        finish()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



