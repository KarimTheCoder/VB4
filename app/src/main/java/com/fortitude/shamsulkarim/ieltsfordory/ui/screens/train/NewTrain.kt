package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.train

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.train.TrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.trainfinished.TrainFinishedActivity
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme

/**
 * Training Activity - now hosts the Compose TrainScreen.
 * Uses ComponentActivity for Jetpack Compose integration.
 */
class NewTrain : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            VocabularyTheme {
                TrainScreen(
                    onNavigateHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onTrainingComplete = {
                        startActivity(Intent(this, TrainFinishedActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}



