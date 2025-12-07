package com.fortitude.shamsulkarim.ieltsfordory.ui.compose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom animated pie/circular progress chart.
 * Replaces the third-party az.plainpie.PieView library.
 *
 * @param percentage Progress percentage (0-100)
 * @param modifier Modifier for the composable
 * @param size Size of the chart
 * @param strokeWidth Width of the progress arc
 * @param progressColor Color of the progress arc
 * @param backgroundColor Color of the background arc
 * @param showPercentage Whether to show percentage text in center
 * @param animationDuration Animation duration in milliseconds
 */
@Composable
fun PieChart(
    percentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    strokeWidth: Dp = 8.dp,
    progressColor: Color = Color.White,
    backgroundColor: Color = Color.White.copy(alpha = 0.3f),
    showPercentage: Boolean = true,
    animationDuration: Int = 1000
) {
    var animatedPercentage by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(percentage) {
        animatedPercentage = percentage.toFloat()
    }
    
    val animatedValue by animateFloatAsState(
        targetValue = animatedPercentage,
        animationSpec = tween(durationMillis = animationDuration),
        label = "pie_animation"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val arcSize = this.size.minDimension - strokeWidthPx
            
            // Background arc
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                topLeft = androidx.compose.ui.geometry.Offset(
                    strokeWidthPx / 2,
                    strokeWidthPx / 2
                )
            )
            
            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = (animatedValue / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                topLeft = androidx.compose.ui.geometry.Offset(
                    strokeWidthPx / 2,
                    strokeWidthPx / 2
                )
            )
        }
        
        if (showPercentage) {
            Text(
                text = "${animatedValue.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}
