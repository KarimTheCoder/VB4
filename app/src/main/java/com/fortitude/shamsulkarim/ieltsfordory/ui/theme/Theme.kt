package com.fortitude.shamsulkarim.ieltsfordory.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = Purple700,
    secondary = Cyan200,
    secondaryContainer = Cyan200,
    tertiary = BeginnerSecondary,
    background = Background,
    surface = BackgroundPrimary,
    surfaceVariant = BackgroundSecondary,
    onPrimary = TextPrimaryWhite,
    onSecondary = TextPrimary,
    onTertiary = TextPrimaryWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = Red,
    onError = TextPrimaryWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    primaryContainer = Purple700,
    secondary = Cyan200,
    secondaryContainer = Cyan200,
    tertiary = BeginnerSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onPrimary = TextPrimaryWhite,
    onSecondary = TextPrimary,
    onTertiary = TextPrimaryWhite,
    onBackground = TextPrimaryWhite,
    onSurface = TextPrimaryWhite,
    onSurfaceVariant = TextSecondaryLight,
    error = Red,
    onError = TextPrimaryWhite
)

@Composable
fun VocabularyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VocabularyTypography,
        content = content
    )
}


