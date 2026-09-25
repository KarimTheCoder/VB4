package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

import android.content.Intent
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.LocalExtendedColors
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = org.koin.androidx.compose.koinViewModel(),
    onBack: () -> Unit = {},
    onNavigateToResult: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Navigate to result when session is complete
    LaunchedEffect(uiState.isSessionComplete) {
        if (uiState.isSessionComplete) {
            onNavigateToResult()
        }
    }

    SessionScreenContent(
        uiState = uiState,
        onNextClick = viewModel::onNextWord,
        onKnowIt = viewModel::recordCorrectAnswer,
        onNeedPractice = viewModel::recordMistake,
        onToggleFavorite = viewModel::onToggleFavorite,
        onSpeakClick = viewModel::speakCurrentWord,
        onNotesChanged = viewModel::onNotesChanged,
        onReportMistake = {
            val word = uiState.currentWord
            val subject = "Vocabulary Builder - Report Word Issue: ${word.word}"
            val body = "Word: ${word.word}\nMeaning: ${word.meaning}\n\nPlease describe the issue below:\n"
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:fortitudedevs@gmail.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            try {
                context.startActivity(Intent.createChooser(emailIntent, "Report Mistake via Email"))
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "No email app found to send report", android.widget.Toast.LENGTH_SHORT).show()
            }
        },
        onOptionSelected = viewModel::onOptionSelected,
        onCheckAnswer = viewModel::checkAnswer
    )
}

@Composable
fun SessionScreenContent(
    uiState: SessionUiState,
    onNextClick: () -> Unit = {},
    onKnowIt: () -> Unit = {},           // User knows this word
    onNeedPractice: () -> Unit = {},      // User needs more practice
    onToggleFavorite: () -> Unit = {},
    onSpeakClick: () -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onReportMistake: () -> Unit = {},
    onOptionSelected: (Int) -> Unit = {},
    onCheckAnswer: () -> Unit = {}
) {
    Scaffold(modifier = Modifier.fillMaxSize().systemBarsPadding()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)

        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar - shows different progress based on phase
            // In quiz: progress = correctAnswers / (totalWords * 3) since each word needs 3 correct
            SessionProgressBar(
                currentIndex = if (uiState.phase == SessionPhase.QUIZZING) 
                    uiState.correctAnswers else uiState.currentWordIndex,
                totalWords = if (uiState.phase == SessionPhase.QUIZZING)
                    uiState.totalWords * 3 else uiState.totalWords,  // 3 correct per word needed
                isQuizPhase = uiState.phase == SessionPhase.QUIZZING,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.phase == SessionPhase.QUIZZING) {
                QuizContent(
                    word = uiState.currentWord,
                    options = uiState.quizOptions,
                    selectedOptionIndex = uiState.selectedOptionIndex,
                    correctOptionIndex = uiState.correctOptionIndex,
                    isAnswerRevealed = uiState.isAnswerRevealed,
                    onOptionSelected = onOptionSelected,
                    onCheckAnswer = onCheckAnswer,
                    onNextClick = onNextClick,
                    onSpeakClick = onSpeakClick,
                    // Session stats for mastery UI
                    masteredCount = uiState.masteredCount,
                    totalWords = uiState.totalWords,
                    currentStreak = uiState.currentStreak,
                    accuracy = if (uiState.totalQuestions > 0) 
                        uiState.correctAnswers.toFloat() / uiState.totalQuestions 
                        else 0f
                )
            } else {
                // Scrollable content for Learning Phase
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Word Card
                    WordCard(
                        word = uiState.currentWord.word,
                        isFavorite = uiState.currentWord.isFavorite,
                        onSpeakClick = onSpeakClick,
                        onToggleFavorite = onToggleFavorite,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explanation Card
                    ExplanationCard(
                        meaning = uiState.currentWord.meaning,
                        examples = uiState.currentWord.examples,
                        secondTranslation = uiState.currentWord.secondTranslation,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Report a mistake
                        Text(
                            text = "Report a mistake",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onReportMistake() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Single Next Button for Learning Phase
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onNextClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Next",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizContent(
    word: SessionWord,
    options: List<String>,
    selectedOptionIndex: Int?,
    correctOptionIndex: Int,
    isAnswerRevealed: Boolean,
    onOptionSelected: (Int) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextClick: () -> Unit,
    onSpeakClick: () -> Unit = {},
    // Session stats for mastery UI
    masteredCount: Int = 0,
    totalWords: Int = 0,
    currentStreak: Int =  0,
    accuracy: Float = 0f
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = word.word,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (word.requiredCorrect > 1) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mastery: ${word.correctCount}/${word.requiredCorrect}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce word",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Options List
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == correctOptionIndex
                
                val (borderColor, containerColor, textColor) = when {
                    isAnswerRevealed && isCorrect -> Triple(LocalExtendedColors.current.statusGreen, LocalExtendedColors.current.statusGreenLight, MaterialTheme.colorScheme.onSurface)
                    isAnswerRevealed && isSelected && !isCorrect -> Triple(LocalExtendedColors.current.statusPink, LocalExtendedColors.current.statusPinkLight, MaterialTheme.colorScheme.onSurface)
                    isSelected -> Triple(MaterialTheme.colorScheme.primary, LocalExtendedColors.current.lightBlue, MaterialTheme.colorScheme.primary)
                    else -> Triple(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswerRevealed) { onOptionSelected(index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 2.dp,
                                    color = borderColor,
                                    shape = CircleShape
                                )
                                .background(
                                    color = if (isSelected || (isAnswerRevealed && isCorrect)) borderColor else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                             if (isAnswerRevealed && isCorrect) {
                                 Icon(
                                     imageVector = Icons.Default.Check,
                                     contentDescription = null,
                                     tint = MaterialTheme.colorScheme.onPrimary,
                                     modifier = Modifier.size(16.dp)
                                 )
                             } else if (isAnswerRevealed && isSelected && !isCorrect) {
                                 Icon(
                                     imageVector = Icons.Default.Close,
                                     contentDescription = null,
                                     tint = MaterialTheme.colorScheme.onPrimary,
                                     modifier = Modifier.size(16.dp)
                                 )
                             }
                        }
                        

                        Text(
                            text = option,
                            modifier = Modifier.padding(16.dp),

                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (isAnswerRevealed) {
                    onNextClick()
                } else {
                    onCheckAnswer()
                }
            },
            enabled = selectedOptionIndex != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnswerRevealed && selectedOptionIndex == correctOptionIndex) LocalExtendedColors.current.statusGreen 
                                 else if (isAnswerRevealed) LocalExtendedColors.current.statusPink 
                                 else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isAnswerRevealed) "Next" else "Check Answer",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



/**
 * Individual stat chip for the stats header.
 */
@Composable
private fun StatChip(
    label: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun SessionProgressBar(
    currentIndex: Int,
    totalWords: Int,
    isQuizPhase: Boolean = false,
    modifier: Modifier = Modifier
) {
    // In learning phase: (currentIndex + 1) / total (so first word shows some progress)
    // In quiz phase: masteredCount / total (0% at start, 100% when all mastered)
    val progress = if (totalWords > 0) {
        if (isQuizPhase) currentIndex.toFloat() / totalWords
        else (currentIndex + 1).toFloat() / totalWords
    } else 0f

    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        // Track
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp)),
            color = if (isQuizPhase) LocalExtendedColors.current.statusGreen else MaterialTheme.colorScheme.primary,  // Green in quiz phase
            trackColor = LocalExtendedColors.current.progressTrack,
            strokeCap = StrokeCap.Round
        )

        // Dot indicator at current progress position
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isQuizPhase) LocalExtendedColors.current.statusGreen else MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
        )
    }
}

@Composable
private fun WordCard(
    word: String,
    isFavorite: Boolean,
    onSpeakClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word text
            Text(
                text = word,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Speaker icon button
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce word",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Bookmark icon button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplanationCard(
    meaning: String,
    examples: List<String>,
    secondTranslation: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Meaning section with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // Light bulb icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LocalExtendedColors.current.lightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meaning,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    if (!secondTranslation.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "ES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = secondTranslation,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Examples section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // List icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LocalExtendedColors.current.lightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    examples.forEach { example ->
                        Text(
                            text = example,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }


        }
    }
}


@Composable
private fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(140.dp)
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Next",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SessionScreenContentPreview() {
    VocabularyTheme {
        SessionScreenContent(
            uiState = SessionUiState(
                isLoading = false,
                currentWordIndex = 0,
                totalWords = 5,
                currentWord = SessionWord(
                    id = "1",
                    word = "Get out",
                    status = LearningStatus.FAMILIARIZING,
                    familiarityProgress = 0.25f,
                    meaning = "It can simply mean to leave a place. For example, \"It's time to get out of here.\"",
                    examples = listOf(
                        "It's getting late, we should get out of here before the traffic gets worse.",
                        "Get out! You mean to tell me you met the President?",
                        "He managed to get out of the burning building just in time."
                    ),
                    isFavorite = false
                ),
                userNotes = ""
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScreenContentPreview() {
    VocabularyTheme {
        SessionScreenContent(
            uiState = SessionUiState(
                isLoading = false,
                currentWordIndex = 0,
                totalWords = 5,
                phase = SessionPhase.QUIZZING,
                currentWord = SessionWord(
                    id = "1",
                    word = "Get out",
                    status = LearningStatus.FAMILIARIZING,
                    familiarityProgress = 0.25f,
                    meaning = "It can simply mean to leave a place. For example, \"It's time to get out of here.\"",
                    correctCount = 1,
                    requiredCorrect = 3
                ),
                quizOptions = listOf(
                    "To enter a building quickly",
                    "To leave a place",
                    "To understand something",
                    "To buy a ticket"
                ),
                correctOptionIndex = 1,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                totalQuestions = 5,
                correctAnswers = 3,
                currentStreak = 2,
                masteredCount = 1
            )
        )
    }
}