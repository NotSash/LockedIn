package com.lockedin.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProviders

/**
 * Simple Glance theme for widgets, aligned with app colors.
 */
object LockedInWidgetTheme {
    private val lightColors = ColorProviders(
        primary = Color(0xFF651FFF),
        background = Color(0xFFF8FAFC),
        onBackground = Color(0xFF0F172A)
    )

    private val darkColors = ColorProviders(
        primary = Color(0xFF7C4DFF),
        background = Color(0xFF0A0E1A),
        onBackground = Color(0xFFF1F5F9)
    )

    @Composable
    @GlanceComposable
    fun Content(
        darkTheme: Boolean,
        content: @Composable @GlanceComposable () -> Unit
    ) {
        GlanceTheme(
            colors = if (darkTheme) darkColors else lightColors,
            content = content
        )
    }
}

