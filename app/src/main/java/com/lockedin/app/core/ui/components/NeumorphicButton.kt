package com.lockedin.app.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.animation.SpringButtonPress

/**
 * Neumorphic button for primary/secondary actions.
 *
 * Visual:
 * - Dual shadow to create "raised" soft shape.
 * - Press state inverts shadow (appears pressed-in).
 * Interactions:
 * - Scale 1.0 -> 0.96 on press, spring back.
 * - Light haptic feedback on click.
 */
@Composable
fun NeumorphicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptics = LocalHapticFeedback.current

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseColor = MaterialTheme.colorScheme.surface
    val lightShadow = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.7f)
    val darkShadow = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.15f)

    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = SpringButtonPress,
        label = "neumorphicButtonScale"
    )

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .neumorphicBackground(
                baseColor = baseColor,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                pressed = pressed,
                isDark = isDark
            )
            .scale(scale)
            .neumorphicClickable(
                enabled = enabled,
                interactionSource = interactionSource,
                onPressStateChange = { pressed = it },
                onClick = {
                    if (!enabled) return@neumorphicClickable
                    haptics.performHapticFeedback(HapticFeedbackType.LightTap)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            val color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            }
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                text = text,
                color = color
            )
        }
    }
}

private fun Modifier.neumorphicBackground(
    baseColor: Color,
    lightShadow: Color,
    darkShadow: Color,
    pressed: Boolean,
    isDark: Boolean
): Modifier = this.then(
    Modifier.drawBehind {
        val radius = 16.dp.toPx()
        val offset = 4.dp.toPx()

        if (!pressed) {
            // Raised: light top-left, dark bottom-right
            drawRoundRect(
                color = darkShadow,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
                topLeft = androidx.compose.ui.geometry.Offset(offset, offset)
            )
            drawRoundRect(
                color = lightShadow,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
                topLeft = androidx.compose.ui.geometry.Offset(-offset, -offset)
            )
        } else {
            // Pressed: inverted shadows
            drawRoundRect(
                color = darkShadow.copy(alpha = if (isDark) 0.4f else 0.25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
                topLeft = androidx.compose.ui.geometry.Offset(-offset, -offset)
            )
            drawRoundRect(
                color = lightShadow.copy(alpha = if (isDark) 0.15f else 0.6f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
                topLeft = androidx.compose.ui.geometry.Offset(offset, offset)
            )
        }

        drawRoundRect(
            color = baseColor,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius)
        )
    }
)

private fun Modifier.neumorphicClickable(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onPressStateChange: (Boolean) -> Unit,
    onClick: () -> Unit
): Modifier = composed {
    pointerInput(interactionSource, enabled) {
        detectTapGestures(
            onPress = {
                if (!enabled) return@detectTapGestures
                onPressStateChange(true)
                val success = tryAwaitRelease()
                onPressStateChange(false)
                if (success) {
                    onClick()
                }
            }
        )
    }
}

