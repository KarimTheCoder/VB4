package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.WordItem
import kotlin.math.roundToInt

@Composable
fun AnimatedWordCard(
    word: WordItem,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Track if this is a new appearance
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(word.id) {
        isVisible = true
    }
    
    // Subtle scale animation (no bounce, smooth ease)
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(durationMillis = 250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "scale"
    )
    
    // Gentle horizontal slide
    val offsetX by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "offsetX"
    )
    
    // Smooth fade in
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )
    
    WordCard(
        text = word.text,
        progress = word.progress,
        type = word.type,
        onSkipClick = onSkipClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                this.alpha = alpha
            }
    )
}

@Composable
fun WordCard(
    text: String,
    progress: Float,
    type: String,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedSteps = (progress * 3).roundToInt().coerceIn(0, 3)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE8EBFA),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )

                // Progress Pill
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE8EBFA)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3 Dots
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 1..3) {
                                val isActive = i <= completedSteps
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (isActive) Color(0xFFD0D7F5) else Color(0xFFF1F5F9),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$completedSteps/3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = Color(0xFFF1F5F9),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Type Label
                val bgColor = when(type) {
                    "Due" -> Color(0xFFFFF0EC)
                    "Mistaken" -> Color(0xFFFFEBEE)
                    "New" -> Color(0xFFF0F4FF)
                    else -> Color(0xFFF1F5F9)
                }
                val fgColor = when(type) {
                    "Due" -> Color(0xFFD84315)
                    "Mistaken" -> Color(0xFFC62828)
                    "New" -> Color(0xFF1E88E5)
                    else -> Color(0xFF475569)
                }

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = type.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = fgColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Skip button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onSkipClick() }
                ) {
                    Text(
                        text = "Skip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
