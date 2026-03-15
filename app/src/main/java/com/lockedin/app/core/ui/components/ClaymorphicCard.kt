package com.lockedin.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Claymorphic card, primarily used for category tiles and hero elements.
 */
@Composable
fun ClaymorphicCard(
    color: Color,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val shadowColor = color.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = shadowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * 0.15f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                )
            }
            .background(
                color = color,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

