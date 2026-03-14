package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme


@Composable
fun ResultScreen(
    viewModel: ResultViewModel = org.koin.androidx.compose.koinViewModel(),
    onHomeClick: () -> Unit = {},
    onNewSessionClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ResultScreenContent(
        uiState = uiState,
        onHomeClick = onHomeClick,
        onNewSessionClick = onNewSessionClick,
        onToggleWordExpanded = viewModel::onToggleWordExpanded
    )
}

@Composable
fun ResultScreenContent(
    uiState: ResultUiState,
    onHomeClick: () -> Unit = {},
    onNewSessionClick: () -> Unit = {},
    onToggleWordExpanded: (String) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title
            Text(
                text = "Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grade Section
            GradeSection(
                grade = uiState.grade,
                progress = uiState.gradeProgress,
                title = uiState.congratsTitle,
                message = uiState.congratsMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Word Results List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.wordResults) { wordResult ->
                    WordResultCard(
                        wordResult = wordResult,
                        onToggleExpanded = { onToggleWordExpanded(wordResult.id) }
                    )
                }
            }

            // Bottom Buttons
            BottomButtons(
                onHomeClick = onHomeClick,
                onNewSessionClick = onNewSessionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun GradeSection(
    grade: String,
    progress: Float,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Congrats message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Right side - Grade circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            // Background track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 8.dp
            )
            // Progress
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(100.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            // Grade text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = grade,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Grade",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WordResultCard(
    wordResult: WordResult,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onToggleExpanded() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Word
                Text(
                    text = wordResult.word,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Status label if present
                wordResult.statusLabel?.let { label ->
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = com.fortitude.shamsulkarim.ieltsfordory.ui.theme.ProgressGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Meaning
            Text(
                text = wordResult.meaning,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun BottomButtons(
    onHomeClick: () -> Unit,
    onNewSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Home button (outlined)
        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier
                .weight(0.4f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Home",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // New session button (filled)
        Button(
            onClick = onNewSessionClick,
            modifier = Modifier
                .weight(0.6f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "New session",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultScreenContentPreview() {
    VocabularyTheme {
        ResultScreenContent(
            uiState = ResultUiState(
                isLoading = false,
                grade = "A+",
                gradeProgress = 0.85f,
                congratsTitle = "Well Done!",
                congratsMessage = "You are doing great. Next time focus on improving accuracy",
                wordResults = listOf(
                    WordResult(
                        id = "1",
                        word = "Get out",
                        meaning = "It can simply mean to leave a place. For example, \"It's time to get out of here.\"",
                        status = WordResultStatus.CORRECT,
                        statusLabel = "Most mistaken word",
                        isExpanded = false,
                        performanceIndicators = listOf(
                            PerformanceIndicator(IndicatorType.SPEED, isPositive = true),
                            PerformanceIndicator(IndicatorType.ACCURACY, isPositive = true)
                        )
                    ),
                    WordResult(
                        id = "2",
                        word = "Inside out",
                        meaning = "It can simply mean to leave a place. For example, \"It's time to get out of here.\"",
                        status = WordResultStatus.MISTAKEN,
                        isExpanded = true,
                        feedbackMessage = "You are answering too fast and not reviewing the idiom enough.",
                        performanceIndicators = listOf(
                            PerformanceIndicator(IndicatorType.SPEED, isPositive = false),
                            PerformanceIndicator(IndicatorType.ACCURACY, isPositive = false)
                        )
                    )
                )
            )
        )
    }
}
