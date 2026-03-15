package com.lockedin.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Claymorphic badge used for counts and small labels.
 *
 * Rounded shape, strong colored shadow below, and subtle inner highlight.
 */
@Composable
fun ClaymorphicBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 20.dp,
    modifier: Modifier = Modifier
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
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelSmall) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                text = text,
                color = color
            )
        }
    }
}

