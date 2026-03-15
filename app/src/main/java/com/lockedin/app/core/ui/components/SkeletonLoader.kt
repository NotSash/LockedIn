package com.lockedin.app.core.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer-based skeleton placeholder for loading states.
 */
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeletonProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .drawWithContent {
                val baseColor = Color(0xFF1E293B)
                val highlightColor = Color(0xFF334155)

                val width = size.width
                val height = size.height
                val xOffset = (width + height) * progress

                val brush = Brush.linearGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.2f),
                        highlightColor.copy(alpha = 0.8f),
                        baseColor.copy(alpha = 0.2f)
                    ),
                    start = Offset(xOffset - height, 0f),
                    end = Offset(xOffset, height)
                )

                drawRect(color = baseColor)
                drawRect(brush = brush)
            }
    )
}

