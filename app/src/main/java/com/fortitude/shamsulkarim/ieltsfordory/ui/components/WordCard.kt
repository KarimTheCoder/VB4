package com.fortitude.shamsulkarim.ieltsfordory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Green
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Red

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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with pronunciation and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pronunciation (main word display)
                Text(
                    text = word.pronunciation ?: word.word,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speaker button
                    IconButton(
                        onClick = onSpeakerClick,
                        enabled = !isAudioLoading,
                        modifier = Modifier.size(40.dp)
                    ) {
                        if (isAudioLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speak word",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Favorite button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (word.isFavorite) 
                                Icons.Default.Favorite 
                            else 
                                Icons.Default.FavoriteBorder,
                            contentDescription = if (word.isFavorite) 
                                "Remove from favorites" 
                            else 
                                "Add to favorites",
                            tint = if (word.isFavorite) Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Translation
            Text(
                text = word.translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Grammar (if available)
            word.grammar?.takeIf { it.isNotBlank() }?.let { grammar ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = grammar,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Example sentence (if available)
            word.example1?.takeIf { it.isNotBlank() }?.let { example ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$example\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



