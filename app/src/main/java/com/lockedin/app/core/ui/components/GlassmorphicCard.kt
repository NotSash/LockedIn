package com.lockedin.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Convenience wrapper around GlassmorphicSurface for card usage.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    GlassmorphicSurface(
        modifier = modifier,
        cornerRadius = cornerRadius,
        borderWidth = 1.dp,
        blurRadius = 24.dp,
        contentPadding = contentPadding,
        content = content
    )
}

