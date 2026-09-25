package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.PrimaryBlue

@Composable
fun HomeTitle(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Fortitude Learn",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = "VOCABULARY MASTERY",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            letterSpacing = 1.5.sp
        )
    }
}
