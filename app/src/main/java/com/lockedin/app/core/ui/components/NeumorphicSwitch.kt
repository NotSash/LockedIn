package com.lockedin.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Neumorphic toggle switch with springy thumb and cross-faded track.
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseColor = MaterialTheme.colorScheme.surface
    val lightShadow = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.7f)
    val darkShadow = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.15f)

    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        animationSpec = spring(),
        label = "switchTrackColor"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = spring(),
        label = "switchThumbOffset"
    )

    Box(
        modifier = modifier
            .width(46.dp)
            .height(26.dp)
            .clip(CircleShape)
            .neumorphicBackground(
                baseColor = baseColor,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                pressed = true,
                isDark = isDark
            )
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange(!checked)
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(trackColor.copy(alpha = 0.4f), CircleShape)
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset, y = 2.dp)
                .width(22.dp)
                .height(22.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = if (isDark) 0.95f else 1f))
        )
    }
}

