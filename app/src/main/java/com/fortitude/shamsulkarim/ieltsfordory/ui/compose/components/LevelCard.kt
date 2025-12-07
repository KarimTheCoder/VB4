package com.fortitude.shamsulkarim.ieltsfordory.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.AdvancePrimary
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.BeginnerPrimary
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.BeginnerSecondary
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.Cyan200
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.IntermediatePrimary
import com.fortitude.shamsulkarim.ieltsfordory.ui.compose.theme.Purple500

/**
 * Level selection card for the home screen.
 * Shows level name with a circular progress indicator.
 *
 * @param title Level title (e.g., "Beginner", "Intermediate", "Advanced")
 * @param percentage Progress percentage (0-100)
 * @param level Level identifier ("beginner", "intermediate", "advance")
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun LevelCard(
    title: String,
    percentage: Int,
    level: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (level) {
        "beginner" -> listOf(BeginnerPrimary, BeginnerSecondary)
        "intermediate" -> listOf(IntermediatePrimary, Cyan200)
        "advance" -> listOf(AdvancePrimary, Purple500)
        else -> listOf(Purple500, Cyan200)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PieChart(
                    percentage = percentage,
                    size = 70.dp,
                    strokeWidth = 6.dp
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }
    }
}
