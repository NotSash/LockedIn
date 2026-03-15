package com.lockedin.app.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Backspace
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.animation.SpringButtonPress

/**
 * 3x4 Neumorphic PIN pad for 8-digit quick unlock PIN.
 *
 * Security-related logic (attempt counting, lockout) lives in ViewModels/use cases.
 */
@Composable
fun NeumorphicPinPad(
    digitsEntered: Int,
    maxDigits: Int = 8,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "←")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxDigits) { index ->
                val filled = index < digitsEntered
                PinDot(filled = filled)
            }
        }

        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            label.isEmpty() -> Unit
                            label == "←" -> PinKey(
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Backspace,
                                        contentDescription = "Backspace"
                                    )
                                },
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onBackspace()
                                }
                            )

                            else -> PinKey(
                                content = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                },
                                onClick = {
                                    if (digitsEntered < maxDigits) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDigit(label.toInt())
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinDot(filled: Boolean) {
    val color = if (filled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    val scale by animateFloatAsState(
        targetValue = if (filled) 1.1f else 1f,
        animationSpec = SpringButtonPress,
        label = "pinDotScale"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun PinKey(
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseColor = MaterialTheme.colorScheme.surface
    val lightShadow = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.7f)
    val darkShadow = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.15f)

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = SpringButtonPress,
        label = "pinKeyScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .neumorphicBackground(
                baseColor = baseColor,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                pressed = pressed,
                isDark = isDark
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val success = tryAwaitRelease()
                        pressed = false
                        if (success) {
                            onClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

