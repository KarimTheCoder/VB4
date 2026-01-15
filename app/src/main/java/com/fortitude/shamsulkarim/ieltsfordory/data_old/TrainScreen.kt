package com.fortitude.shamsulkarim.ieltsfordory.data_old

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Green
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Purple500
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Red
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/**
 * Main composable for the Training screen.
 * Replaces the XML-based NewTrain Activity.
 */
@Composable
fun TrainScreen(
    viewModel: TrainViewModel = koinViewModel(),
    onNavigateHome: () -> Unit = {},
    onTrainingComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Initialize words on first composition
    LaunchedEffect(Unit) {
        viewModel.initializingWords()
    }
    
    // Handle session completion
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == TrainPhase.Finished) {
            onTrainingComplete()
        }
    }
    
    // Handle word advancement trigger
    LaunchedEffect(uiState.shouldTriggerNextWord) {
        if (uiState.shouldTriggerNextWord) {
            viewModel.onNextWordTriggered()
            if (uiState.showCycle >= uiState.fiveWordSize) {
                // Learning phase complete, show vocabulary selection
                if (!uiState.isVocabularySelectionShown) {
                    viewModel.showVocabularySelection()
                }
            } else {
                // Advance to next word
                viewModel.advanceToNextWord()
            }
        }
    }
    
    // Auto-reset color scheme after showing correct/wrong feedback
    LaunchedEffect(uiState.colorScheme) {
        if (uiState.colorScheme != TrainColorScheme.DEFAULT) {
            delay(800)
            viewModel.resetColorScheme()
        }
    }
    
    val backgroundColor = when (uiState.colorScheme) {
        TrainColorScheme.CORRECT -> Green
        TrainColorScheme.WRONG -> Red
        TrainColorScheme.DEFAULT -> Purple500
    }
    
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top section with color
            TrainHeader(
                uiState = uiState,
                backgroundColor = backgroundColor,
                onSpeakerClick = { word ->
                    // Audio playback would be handled here
                }
            )
            
            // Progress indicator
            TrainProgressBar(
                progress = uiState.totalProgress,
                max = uiState.maxProgress,
                color = backgroundColor
            )
            
            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.phase) {
                    is TrainPhase.Learning -> {
                        LearningContent(
                            word = uiState.currentWord,
                            showSecondaryLanguage = uiState.showSecondaryLanguage
                        )
                    }
                    is TrainPhase.VocabularySelection -> {
                        VocabularySelectionDialog(
                            words = uiState.fiveWordsCopy.ifEmpty { uiState.fiveWords },
                            onConfirm = { selectedIndices ->
                                if (selectedIndices.isEmpty()) {
                                    viewModel.skipVocabularySelection()
                                } else {
                                    viewModel.onVocabularySelected(selectedIndices)
                                }
                            }
                        )
                    }
                    is TrainPhase.Quiz -> {
                        if (uiState.isShowingWordReview) {
                            // Show word review after wrong answer
                            WordReviewContent(
                                word = uiState.currentWord,
                                correctAnswer = uiState.correctAnswer,
                                onContinue = { viewModel.continueAfterReview() }
                            )
                        } else {
                            QuizContent(
                                uiState = uiState,
                                onAnswerSelected = { index ->
                                    val result = viewModel.checkAnswer(index)
                                    if (result.isCorrect) {
                                        if (uiState.soundEnabled) {
                                            MediaPlayer.create(context, R.raw.correct)?.start()
                                        }
                                        // generateAnswers() is now called in ViewModel after state update
                                    } else {
                                        if (uiState.soundEnabled) {
                                            MediaPlayer.create(context, R.raw.incorrect)?.start()
                                        }
                                    }
                                }
                            )
                        }
                    }
                    is TrainPhase.Finished -> {
                        // Will trigger navigation via LaunchedEffect
                    }
                }
            }
            
            // Timer/Next button (only in Learning phase)
            AnimatedVisibility(
                visible = uiState.phase == TrainPhase.Learning,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TimerButton(
                    progressCount = uiState.progressCount,
                    isTimerRunning = uiState.isTimerRunning,
                    onClick = { viewModel.onNextClicked() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
    
    // Back press confirmation dialog
    BackPressConfirmDialog(
        userName = viewModel.getUserName(),
        onConfirmLeave = onNavigateHome
    )
}

@Composable
private fun TrainHeader(
    uiState: TrainUiState,
    backgroundColor: Color,
    onSpeakerClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current word display
            uiState.currentWord?.let { word ->
                val displayText = if (uiState.showSecondaryLanguage) {
                    buildAnnotatedString {
                        append(word.word)
                        word.wordSecondLang?.let { secondLang ->
                            appendLine()
                            withStyle(SpanStyle(fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))) {
                                append(secondLang)
                            }
                        }
                    }
                } else {
                    buildAnnotatedString { append(word.word) }
                }
                
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            // Speaker icon
            AnimatedVisibility(visible = uiState.isSpeakerVisible) {
                IconButton(
                    onClick = { uiState.currentWord?.word?.let(onSpeakerClick) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainProgressBar(
    progress: Float,
    max: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (max > 0) progress / max else 0f,
        animationSpec = tween(300),
        label = "progress"
    )
    
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = color,
        trackColor = color.copy(alpha = 0.3f)
    )
}

@Composable
private fun LearningContent(
    word: VocabularyWord?,
    showSecondaryLanguage: Boolean
) {
    if (word == null) return
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Definition card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Definition",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (showSecondaryLanguage) {
                        "${word.translation}\n${word.translationSecondLang ?: ""}"
                    } else {
                        word.translation
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                word.pronunciation?.let { pronunciation ->
                    Text(
                        text = pronunciation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        // Example sentences
        word.example1?.let { example ->
            ExampleCard(
                example = example,
                exampleSecondLang = if (showSecondaryLanguage) word.example1SecondLang else null
            )
        }
        
        word.example2?.let { example ->
            ExampleCard(
                example = example,
                exampleSecondLang = if (showSecondaryLanguage) word.example2SecondLang else null
            )
        }
    }
}

@Composable
private fun ExampleCard(
    example: String,
    exampleSecondLang: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = example,
                style = MaterialTheme.typography.bodyMedium
            )
            exampleSecondLang?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun WordReviewContent(
    word: VocabularyWord?,
    correctAnswer: String,
    onContinue: () -> Unit
) {
    if (word == null) return
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wrong answer indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Red.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Wrong Answer!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Red
                )
                
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Correct answer:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = correctAnswer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Green
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple500
            )
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun QuizContent(
    uiState: TrainUiState,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Answer cards
        uiState.answers.forEachIndexed { index, answer ->
            QuizAnswerCard(
                answer = if (uiState.languageId == 0) {
                    answer.translation
                } else {
                    answer.translationSecondLang ?: ""
                },
                onClick = { onAnswerSelected(index) },
                colorScheme = uiState.colorScheme
            )
        }
    }
}

@Composable
private fun QuizAnswerCard(
    answer: String,
    onClick: () -> Unit,
    colorScheme: TrainColorScheme
) {
    val backgroundColor = when (colorScheme) {
        TrainColorScheme.CORRECT -> Green.copy(alpha = 0.1f)
        TrainColorScheme.WRONG -> Red.copy(alpha = 0.1f)
        TrainColorScheme.DEFAULT -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = answer,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimerButton(
    progressCount: Int,
    isTimerRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple500
            )
        ) {
            if (isTimerRunning) {
                Text(
                    text = "${5 - progressCount}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
private fun VocabularySelectionDialog(
    words: List<VocabularyWord>,
    onConfirm: (List<Int>) -> Unit
) {
    val selectedIndices = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(true) }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Non-dismissable */ },
            title = { Text("Which vocabularies do you want to test?") },
            text = {
                Column {
                    words.forEachIndexed { index, word ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedIndices.contains(index),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedIndices.add(index)
                                    } else {
                                        selectedIndices.remove(index)
                                    }
                                }
                            )
                            Text(
                                text = word.word,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onConfirm(selectedIndices.toList())
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun BackPressConfirmDialog(
    userName: String,
    onConfirmLeave: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    // Handle back press via BackHandler
    BackHandler {
        showDialog = true
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Do you want to leave this session, $userName?") },
            text = { Text("Leaving this session will make you lose your progress") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onConfirmLeave()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}