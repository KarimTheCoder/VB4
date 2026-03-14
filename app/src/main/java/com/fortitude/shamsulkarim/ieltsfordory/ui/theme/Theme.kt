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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

@Immutable
data class ExtendedColors(
    val statusPink: Color,
    val statusPinkLight: Color,
    val statusGreen: Color,
    val statusGreenLight: Color,
    val cardBorder: Color,
    val progressTrack: Color,
    val neutralGray: Color,
    val lightBlue: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        statusPink = Color.Unspecified,
        statusPinkLight = Color.Unspecified,
        statusGreen = Color.Unspecified,
        statusGreenLight = Color.Unspecified,
        cardBorder = Color.Unspecified,
        progressTrack = Color.Unspecified,
        neutralGray = Color.Unspecified,
        lightBlue = Color.Unspecified
    )
}

private val LightExtendedColors = ExtendedColors(
    statusPink = StatusPink,
    statusPinkLight = StatusPinkLight,
    statusGreen = ProgressGreen,
    statusGreenLight = StatusGreenLight,
    cardBorder = CardBorderColor,
    progressTrack = ProgressTrack,
    neutralGray = NeutralGray,
    lightBlue = LightBlueBackground
)

private val DarkExtendedColors = ExtendedColors(
    statusPink = StatusPink,
    statusPinkLight = DarkStatusPinkLight,
    statusGreen = ProgressGreen,
    statusGreenLight = DarkStatusGreenLight,
    cardBorder = DarkCardBorder,
    progressTrack = DarkProgressTrack,
    neutralGray = Color(0xFF424242),
    lightBlue = DarkLightBlue
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

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VocabularyTypography,
            content = content
        )
    }
}


