package com.lockedin.app.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.animation.DurationSecurityGauge

/**
 * Circular security score gauge with animated arc and counting number.
 *
 * @param score 0..100 overall score.
 */
@Composable
fun SecurityScoreGauge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val clamped = score.coerceIn(0, 100)
    val sweepAnim = remember { Animatable(0f) }
    val numberAnim = remember { Animatable(0f) }

    LaunchedEffect(clamped) {
        sweepAnim.animateTo(
            targetValue = clamped / 100f,
            animationSpec = tween(DurationSecurityGauge)
        )
        numberAnim.animateTo(
            targetValue = clamped.toFloat(),
            animationSpec = tween(DurationSecurityGauge)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val thickness = 10.dp.toPx()
            val sweepAngle = sweepAnim.value * 270f
            val startAngle = 135f

            drawArc(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = thickness, cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )

            val gaugeColor = when {
                clamped < 25 -> MaterialTheme.colorScheme.error
                clamped < 50 -> MaterialTheme.colorScheme.error
                clamped < 75 -> MaterialTheme.colorScheme.tertiary
                clamped < 100 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.tertiary
            }

            drawArc(
                color = gaugeColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness, cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
        }

        Text(
            text = numberAnim.value.toInt().toString(),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

