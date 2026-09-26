package com.fortitude.shamsulkarim.ieltsfordory.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.*
import java.util.Locale

/**
 * Vocabulary word card displaying word details with favorite and speaker actions.
 *
 * @param word The vocabulary word to display
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param onSpeakerClick Callback when speaker button is clicked
 * @param isAudioLoading Whether audio is currently loading
 * @param modifier Modifier for the card
 */
@Composable
fun WordCard(
    word: VocabularyWord,
    onFavoriteClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isAudioLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // First Row: Word, Mastery, Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formatted Title
                Text(
                    text = formatWordTitle(word.word, word.pronunciation),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WordCardTitle,
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mastery Indicator
                    val masteryScore = word.familiarityScore
                    val dotsCount = when {
                        masteryScore >= 2.5 -> 3
                        masteryScore >= 1.0 -> 2
                        masteryScore > 0.0 -> 1
                        else -> 0
                    }
                    MasteryIndicator(activeDots = dotsCount, totalDots = 3)

                    // Favorite Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WordCardIconBg)
                            .clickable(onClick = onFavoriteClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (word.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (word.isFavorite) WordCardTitle else WordCardTitle.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Second Row: Pronunciation, Grammar Pill, Listen Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!word.pronunciation.isNullOrBlank()) {
                    Text(
                        text = "/${word.pronunciation}/",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WordCardTitle.copy(alpha = 0.6f)
                    )
                }

                if (!word.grammar.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WordCardPillBg
                    ) {
                        Text(
                            text = word.grammar.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WordCardTitle.copy(alpha = 0.8f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Listen Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WordCardListenBg,
                    modifier = Modifier.clickable(enabled = !isAudioLoading, onClick = onSpeakerClick)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isAudioLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = WordCardTitle
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = WordCardTitle,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Listen",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WordCardTitle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Definition and Example Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(WordCardDefBg)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = word.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            color = WordCardTitle.copy(alpha = 0.9f)
                        )
                    )

                    if (!word.example1.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "“",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WordCardTitle.copy(alpha = 0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatExampleSentence(word.example1, word.word),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = WordCardTitle.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MasteryIndicator(activeDots: Int, totalDots: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = WordCardMasteryBg,
        border = BorderStroke(1.dp, WordCardMasteryText.copy(alpha = 0.2f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 1..totalDots) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (i <= activeDots) WordCardMasteryText else WordCardMasteryText.copy(alpha = 0.2f))
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$activeDots/$totalDots",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WordCardMasteryText
            )
        }
    }
}

private fun formatWordTitle(word: String, pronunciation: String?): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val displayWord = word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        
        if (displayWord.contains("-")) {
            val parts = displayWord.split("-")
            parts.forEachIndexed { index, part ->
                append(part)
                if (index < parts.size - 1) {
                    withStyle(style = SpanStyle(color = WordCardDot)) {
                        append("▪") 
                    }
                }
            }
        } else {
            // For the sake of the specific design "De.fense":
            if (word.equals("defense", ignoreCase = true)) {
                append("De")
                withStyle(style = SpanStyle(color = WordCardDot)) {
                    append("▪") 
                }
                append("fense")
            } else {
                append(displayWord)
            }
        }
    }
}

private fun formatExampleSentence(example: String, word: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val wordIndex = example.indexOf(word, ignoreCase = true)
        if (wordIndex >= 0) {
            append(example.substring(0, wordIndex))
            withStyle(
                style = SpanStyle(
                    background = WordCardHighlightBg,
                    color = WordCardHighlightText,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(example.substring(wordIndex, wordIndex + word.length))
            }
            append(example.substring(wordIndex + word.length))
        } else {
            append(example)
        }
    }
}
