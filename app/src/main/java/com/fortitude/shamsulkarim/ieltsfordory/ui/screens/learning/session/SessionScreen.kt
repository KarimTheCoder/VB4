package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session

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
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme

// Colors matching the design
private val PrimaryBlue = Color(0xFF4052B5)
private val LightBlue = Color(0xFFE8EBFA)
private val StatusPink = Color(0xFFFF6B8A)
private val CardBorderColor = Color(0xFFE0E4F8)
private val ProgressTrackColor = Color(0xFFE8EBFA)
private val ProgressActiveColor = Color(0xFF4052B5)
private val ProgressIndicatorColor = Color(0xFF4052B5)
private val ExampleIconColor = Color(0xFF4052B5)

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = org.koin.androidx.compose.koinViewModel(),
    onBack: () -> Unit = {},
    onNavigateToResult: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
        onNotesChanged = viewModel::onNotesChanged,
        onReportMistake = { /* TODO */ },
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
                .background(Color.White)

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
                        status = uiState.currentWord.status,
                        progress = uiState.currentWord.familiarityProgress,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explanation Card
                    ExplanationCard(
                        meaning = uiState.currentWord.meaning,
                        examples = uiState.currentWord.examples,
                        isFavorite = uiState.currentWord.isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onReportMistake = onReportMistake,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes Card
                    NotesCard(
                        notes = uiState.userNotes,
                        onNotesChanged = onNotesChanged,
                        modifier = Modifier.fillMaxWidth()
                    )

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
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = "Next",
                            color = Color.White,
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
    // Session stats for mastery UI
    masteredCount: Int = 0,
    totalWords: Int = 0,
    currentStreak: Int = 0,
    accuracy: Float = 0f
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Session Stats Header
        SessionStatsHeader(
            masteredCount = masteredCount,
            totalWords = totalWords,
            currentStreak = currentStreak,
            accuracy = accuracy
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Question Card with Mastery Dots
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mastery Dots Indicator
                MasteryDotsIndicator(
                    correctCount = word.correctCount,
                    requiredCorrect = word.requiredCorrect
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = word.word,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "select the correct meaning",
                    fontSize = 14.sp,
                    color = StatusPink,
                    fontWeight = FontWeight.Medium
                )
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
                    isAnswerRevealed && isCorrect -> Triple(Color(0xFF4CAF50), Color(0xFFE8F5E9), Color.Black)
                    isAnswerRevealed && isSelected && !isCorrect -> Triple(StatusPink, Color(0xFFFFEBEE), Color.Black)
                    isSelected -> Triple(PrimaryBlue, LightBlue, PrimaryBlue)
                    else -> Triple(CardBorderColor, Color.White, Color.Black)
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
                                     tint = Color.White,
                                     modifier = Modifier.size(16.dp)
                                 )
                             } else if (isAnswerRevealed && isSelected && !isCorrect) {
                                 Icon(
                                     imageVector = Icons.Default.Close,
                                     contentDescription = null,
                                     tint = Color.White,
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
                containerColor = if (isAnswerRevealed && selectedOptionIndex == correctOptionIndex) Color(0xFF4CAF50) 
                                 else if (isAnswerRevealed) StatusPink 
                                 else PrimaryBlue
            )
        ) {
            Text(
                text = if (isAnswerRevealed) "Next" else "Check Answer",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Mastery dots indicator showing progress toward mastering a word.
 * Shows filled dots for correct answers, empty for remaining.
 */
@Composable
fun MasteryDotsIndicator(
    correctCount: Int,
    requiredCorrect: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(requiredCorrect) { index ->
            val isFilled = index < correctCount
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(horizontal = 2.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilled) Color(0xFF4CAF50)  // Green for filled
                        else Color(0xFFE0E0E0)           // Gray for empty
                    )
            )
        }
    }
}

/**
 * Session stats header showing mastered count, streak, and accuracy.
 */
@Composable
fun SessionStatsHeader(
    masteredCount: Int,
    totalWords: Int,
    currentStreak: Int,
    accuracy: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mastered count
        StatChip(
            label = "$masteredCount/$totalWords",
            icon = "✓",
            color = Color(0xFF4CAF50)
        )
        
        // Streak
        StatChip(
            label = "$currentStreak",
            icon = "🔥",
            color = if (currentStreak >= 3) Color(0xFFFF9800) else Color.Gray
        )
        
        // Accuracy
        StatChip(
            label = "${(accuracy * 100).toInt()}%",
            icon = "📊",
            color = when {
                accuracy >= 0.8f -> Color(0xFF4CAF50)
                accuracy >= 0.5f -> Color(0xFFFF9800)
                else -> StatusPink
            }
        )
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
            color = if (isQuizPhase) Color(0xFF4CAF50) else ProgressActiveColor,  // Green in quiz phase
            trackColor = ProgressTrackColor,
            strokeCap = StrokeCap.Round
        )

        // Dot indicator at current progress position
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isQuizPhase) Color(0xFF4CAF50) else PrimaryBlue)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
private fun WordCard(
    word: String,
    status: LearningStatus,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word text
            Text(
                text = word,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status text
                Text(
                    text = status.displayText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StatusPink
                )

                // Circular progress indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(40.dp),
                        color = ProgressTrackColor,
                        strokeWidth = 4.dp
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(40.dp),
                        color = ProgressIndicatorColor,
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
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
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onReportMistake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = null,
                        tint = ExampleIconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = meaning,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    lineHeight = 24.sp,
                    modifier = Modifier.weight(1f)
                )
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
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = ExampleIconColor,
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
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = CardBorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Report a mistake
                Text(
                    text = "Report a mistake",
                    fontSize = 14.sp,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onReportMistake() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Bookmark icon
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = PrimaryBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesCard(
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(4.dp),
            placeholder = {
                Text(
                    text = "Add your notes here...",
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
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
            containerColor = PrimaryBlue
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Next",
            color = Color.White,
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