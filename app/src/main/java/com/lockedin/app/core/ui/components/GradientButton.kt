package com.lockedin.app.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.background
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.animation.SpringButtonPress
import com.lockedin.app.core.ui.theme.AccentGradientEndDark
import com.lockedin.app.core.ui.theme.AccentGradientEndLight
import com.lockedin.app.core.ui.theme.AccentGradientStartDark
import com.lockedin.app.core.ui.theme.AccentGradientStartLight

/**
 * Primary CTA button with accent gradient, used for strongest actions.
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val gradient = Brush.linearGradient(
        colors = if (isDark) {
            listOf(AccentGradientStartDark, AccentGradientEndDark)
        } else {
            listOf(AccentGradientStartLight, AccentGradientEndLight)
        }
    )

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = SpringButtonPress,
        label = "gradientButtonScale"
    )

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .scale(scale)
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
            .pointerInput(enabled) {
                detectTapGestures(
                    onPress = {
                        if (!enabled) return@detectTapGestures
                        pressed = true
                        val success = tryAwaitRelease()
                        pressed = false
                        if (success) onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                text = text,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

