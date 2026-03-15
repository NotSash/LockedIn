package com.lockedin.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DarkAccentPrimary,
    onPrimary = DarkTextOnAccent,
    primaryContainer = DarkBackgroundTertiary,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkAccentSecondary,
    onSecondary = DarkTextOnAccent,
    secondaryContainer = DarkBackgroundTertiary,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = DarkSuccess,
    onTertiary = DarkTextOnAccent,
    tertiaryContainer = DarkBackgroundTertiary,
    onTertiaryContainer = DarkTextPrimary,
    background = DarkBackgroundPrimary,
    onBackground = DarkTextPrimary,
    surface = DarkBackgroundSecondary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBackgroundTertiary,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = DarkTextOnAccent,
    errorContainer = DarkBackgroundTertiary,
    onErrorContainer = DarkError,
    outline = DarkDivider,
    outlineVariant = DarkDivider,
    surfaceBright = DarkBackgroundTertiary,
    surfaceDim = DarkBackgroundPrimary,
    inverseSurface = DarkBackgroundTertiary,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = DarkAccentSecondary,
    scrim = DarkBackgroundPrimary.copy(alpha = 0.75f)
)

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = LightAccentPrimary,
    onPrimary = LightTextOnAccent,
    primaryContainer = LightBackgroundTertiary,
    onPrimaryContainer = LightTextPrimary,
    secondary = LightAccentSecondary,
    onSecondary = LightTextOnAccent,
    secondaryContainer = LightBackgroundTertiary,
    onSecondaryContainer = LightTextPrimary,
    tertiary = LightSuccess,
    onTertiary = LightTextOnAccent,
    tertiaryContainer = LightBackgroundTertiary,
    onTertiaryContainer = LightTextPrimary,
    background = LightBackgroundPrimary,
    onBackground = LightTextPrimary,
    surface = LightBackgroundSecondary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackgroundTertiary,
    onSurfaceVariant = LightTextSecondary,
    error = LightError,
    onError = LightTextOnAccent,
    errorContainer = LightBackgroundTertiary,
    onErrorContainer = LightError,
    outline = LightDivider,
    outlineVariant = LightDivider,
    surfaceBright = LightBackgroundTertiary,
    surfaceDim = LightBackgroundPrimary,
    inverseSurface = LightBackgroundTertiary,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = LightAccentSecondary,
    scrim = LightBackgroundPrimary.copy(alpha = 0.75f)
)

@Immutable
data class LockedInDesignTokens(
    val glassSurfaceColor: androidx.compose.ui.graphics.Color,
    val shimmerBase: androidx.compose.ui.graphics.Color,
    val shimmerHighlight: androidx.compose.ui.graphics.Color
)

object LockedInThemeTokens {
    val Local: LockedInDesignTokens
        @Composable
        get() {
            val isDark = isSystemInDarkTheme()
            return if (isDark) {
                LockedInDesignTokens(
                    glassSurfaceColor = DarkSurfaceGlass,
                    shimmerBase = DarkShimmerBase,
                    shimmerHighlight = DarkShimmerHighlight
                )
            } else {
                LockedInDesignTokens(
                    glassSurfaceColor = LightSurfaceGlass,
                    shimmerBase = LightShimmerBase,
                    shimmerHighlight = LightShimmerHighlight
                )
            }
        }
}

@Composable
fun LockedInTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
    ) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = LockedInTypography,
        shapes = LockedInShapes,
        content = content
    )
}

