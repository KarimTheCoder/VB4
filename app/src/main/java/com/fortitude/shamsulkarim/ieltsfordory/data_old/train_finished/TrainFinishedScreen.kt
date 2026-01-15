package com.fortitude.shamsulkarim.ieltsfordory.data_old.train_finished

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Green
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Purple500
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Cyan200
import com.fortitude.shamsulkarim.ieltsfordory.data_old.NewTrain
import org.koin.androidx.compose.koinViewModel

/**
 * Training finished screen showing results and learned words.
 * Replaces TrainFinishedActivity.
 */
@Composable
fun TrainFinishedScreen(
    viewModel: TrainFinishedViewModel = koinViewModel(),
    onNavigateHome: () -> Unit = {},
    onTrainAgain: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Play completion sound
    LaunchedEffect(Unit) {
        if (viewModel.shouldPlaySound()) {
            try {
                val mediaPlayer = MediaPlayer.create(context, R.raw.train_finished)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { it.release() }
            } catch (_: Exception) {
                // Ignore sound errors
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Celebration header
            Text(
                text = "🎉",
                fontSize = 64.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Training Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uiState.learnedWordsCount} new words learned",
                style = MaterialTheme.typography.titleMedium,
                color = Green
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Most mistaken word card
            if (uiState.showMostMistakenCard && uiState.mostMistakenWord != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Most Challenging Word",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.mostMistakenWord?.word ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Favorite button
                            IconButton(
                                onClick = { viewModel.toggleMostMistakenFavorite() }
                            ) {
                                Icon(
                                    imageVector = if (uiState.isMostMistakenFavorite)
                                        Icons.Default.Favorite
                                    else
                                        Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (uiState.isMostMistakenFavorite)
                                        Color.Red
                                    else
                                        MaterialTheme.colorScheme.onErrorContainer
                                )
                            }

                            // Learn/Unlearn button
                            OutlinedButton(
                                onClick = { viewModel.toggleMostMistakenLearned() }
                            ) {
                                Text(
                                    text = if (uiState.isMostMistakenLearned) "Unlearn" else "Learn"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Learned words list
            if (uiState.learnedWords.isNotEmpty()) {
                Text(
                    text = "Words You Learned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.learnedWords,
                        key = { it.word }
                    ) { word ->
                        LearnedWordItem(word = word.word, meaning = word.translation)
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Rate App Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.rateAppUrl))
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enjoying the app?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Rate us on the store!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Home button
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as? Activity)?.finish()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Home")
                }

                // Train Again button
                Button(
                    onClick = {
                        context.startActivity(Intent(context, NewTrain::class.java))
                        (context as? Activity)?.finish()
                    },
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Purple500, Cyan200)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Train Again",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LearnedWordItem(
    word: String,
    meaning: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.35f)
            )
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.65f)
            )
        }
    }
}



