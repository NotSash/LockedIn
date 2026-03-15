package com.lockedin.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.theme.DarkSurfaceGlass
import com.lockedin.app.core.ui.theme.LightSurfaceGlass

/**
 * Generic glassmorphic surface with translucent background, blur, gradient border,
 * and subtle colored shadow. Used as the base for cards, sheets, nav bars, etc.
 */
@Composable
fun GlassmorphicSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    blurRadius: Dp = 24.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
    ) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val glassColor: Color = if (isDark) DarkSurfaceGlass else LightSurfaceGlass

    val borderBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.04f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.3f)
            )
        }
    )

    val shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = shadowColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * 0.04f)
                )
            }
            .clip(RoundedCornerShape(cornerRadius))
            // Approximation of backdrop blur
            .blur(
                radius = blurRadius,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            )
            .background(glassColor)
            .drawBehind {
                drawRoundRect(
                    brush = borderBrush,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                )
            }
            .padding(contentPadding)
    ) {
        content()
    }
}

