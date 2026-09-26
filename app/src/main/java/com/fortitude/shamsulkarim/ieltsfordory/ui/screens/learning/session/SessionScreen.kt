package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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
        onBack = onBack,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreenContent(
    uiState: SessionUiState,
    onBack: () -> Unit = {},
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
    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (uiState.phase == SessionPhase.QUIZZING) "Quizzing" else "Learning",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SessionProgressBar(
                            currentIndex = if (uiState.phase == SessionPhase.QUIZZING) 
                                uiState.correctAnswers else uiState.currentWordIndex,
                            totalWords = if (uiState.phase == SessionPhase.QUIZZING)
                                uiState.totalWords * 3 else uiState.totalWords,
                            isQuizPhase = uiState.phase == SessionPhase.QUIZZING,
                            modifier = Modifier.width(160.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close session",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)

        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
                    onToggleFavorite = onToggleFavorite,
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
                        word = uiState.currentWord,
                        onSpeakClick = onSpeakClick,
                        onToggleFavorite = onToggleFavorite,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explanation Card
                    ExplanationCard(
                        targetWord = uiState.currentWord.word,
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
    onToggleFavorite: () -> Unit = {},
    // Session stats for mastery UI
    masteredCount: Int = 0,
    totalWords: Int = 0,
    currentStreak: Int =  0,
    accuracy: Float = 0f
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        WordCard(
            word = word,
            onSpeakClick = onSpeakClick,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Options List Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SELECT CORRECT DEFINITION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Options List
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == correctOptionIndex
                val letter = ('A' + index).toString()
                
                val (borderColor, containerColor, textColor) = when {
                    isAnswerRevealed && isCorrect -> Triple(LocalExtendedColors.current.statusGreen, LocalExtendedColors.current.statusGreenLight, MaterialTheme.colorScheme.onSurface)
                    isAnswerRevealed && isSelected && !isCorrect -> Triple(LocalExtendedColors.current.statusPink, LocalExtendedColors.current.statusPinkLight, MaterialTheme.colorScheme.onSurface)
                    isSelected -> Triple(MaterialTheme.colorScheme.primary, LocalExtendedColors.current.lightBlue, MaterialTheme.colorScheme.primary)
                    else -> Triple(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswerRevealed) { onOptionSelected(index) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Letter Circle (Left)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LocalExtendedColors.current.lightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Option Text (Middle)
                        Text(
                            text = option,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                        )

                        // Selection Indicator Circle (Right)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected || (isAnswerRevealed && isCorrect)) borderColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
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
    }
}

@Composable
private fun WordCard(
    word: SessionWord,
    onSpeakClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Top Row: Syllables and Icon Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Syllables
                val syllablesText = word.syllables?.takeIf { it.isNotBlank() } ?: "SYL · LA · BLES"
                Text(
                    text = syllablesText.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    letterSpacing = 4.sp
                )

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Speaker icon button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LocalExtendedColors.current.lightBlue)
                            .clickable { onSpeakClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce word",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Bookmark icon button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LocalExtendedColors.current.lightBlue)
                            .clickable { onToggleFavorite() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (word.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (word.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            // Main Word
            Text(
                text = word.word,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Phonetic and Stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phonetic Pill
                val phoneticText = word.phonetic?.takeIf { it.isNotBlank() } ?: "/placeholder/"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = phoneticText,
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Stage Pill
                val stageNumber = when (word.status) {
                    LearningStatus.FAMILIARIZING -> 1
                    LearningStatus.LEARNING -> 2
                    LearningStatus.REVIEWING -> 3
                    LearningStatus.MASTERED -> 3
                }
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(LocalExtendedColors.current.lightBlue)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        for (i in 1..3) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i == stageNumber) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }

                    Text(
                        text = "Stage $stageNumber/3",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplanationCard(
    targetWord: String,
    meaning: String,
    examples: List<String>,
    secondTranslation: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // MEANING HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(LocalExtendedColors.current.lightBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MEANING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // MEANING TEXT
            Text(
                text = meaning,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 28.sp
            )
            
            // SECOND TRANSLATION (if any)
            if (!secondTranslation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = secondTranslation,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // EXAMPLES HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(14.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EXAMPLES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // EXAMPLES BOX
            if (examples.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        examples.forEachIndexed { index, example ->
                            // Highlight target word in example
                            val annotatedExample = androidx.compose.ui.text.buildAnnotatedString {
                                val regex = Regex("(?i)\\b${Regex.escape(targetWord)}\\b")
                                var lastIndex = 0
                                regex.findAll(example).forEach { matchResult ->
                                    append(example.substring(lastIndex, matchResult.range.first))
                                   withStyle(
                                        style = androidx.compose.ui.text.SpanStyle(
                                            background = LocalExtendedColors.current.lightBlue,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append(matchResult.value)
                                    }
                                    lastIndex = matchResult.range.last + 1
                                }
                                append(example.substring(lastIndex))
                            }

                            Text(
                                text = annotatedExample,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 24.sp
                            )

                            // Divider (except after last element)
                            if (index < examples.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                )
                            }
                        }
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
                    isFavorite = false,
                    phonetic = "/ˈpɒv.ə.ti/",
                    syllables = "POV · ER · TY"
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
                    requiredCorrect = 3,
                    phonetic = "/ˈpɒv.ə.ti/",
                    syllables = "POV · ER · TY"
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