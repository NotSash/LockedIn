package com.lockedin.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.theme.DarkError
import com.lockedin.app.core.ui.theme.DarkSuccess
import com.lockedin.app.core.ui.theme.DarkWarning

/**
 * Password strength meter with animated width and color transitions.
 *
 * @param score strength score 0..100
 */
@Composable
fun AnimatedStrengthMeter(
    strengthScore: Int,
    modifier: Modifier = Modifier
) {
    val clamped = strengthScore.coerceIn(0, 100)

    val targetFraction = when {
        clamped < 25 -> 0.25f
        clamped < 50 -> 0.5f
        clamped < 75 -> 0.75f
        else -> 1f
    }

    val label = when {
        clamped < 25 -> "Weak"
        clamped < 50 -> "Fair"
        clamped < 75 -> "Strong"
        else -> "Very Strong"
    }

    val targetColor: Color = when {
        clamped < 25 -> DarkError
        clamped < 50 -> DarkWarning
        clamped < 75 -> MaterialTheme.colorScheme.primary
        else -> DarkSuccess
    }

    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 350),
        label = "strengthWidth"
    )

    val color by animateColorAsState(
        targetValue = targetColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 350),
        label = "strengthColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(999.dp))
            .drawBehind {
                val radius = size.height / 2
                drawRoundRect(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius)
                )
                drawRoundRect(
                    color = color,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
                    size = androidx.compose.ui.geometry.Size(size.width * fraction, size.height)
                )
            }
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = "$label • $clamped/100",
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}

