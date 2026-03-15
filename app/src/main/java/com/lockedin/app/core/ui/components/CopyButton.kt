package com.lockedin.app.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Check
import androidx.compose.material3.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Copy button with copy->check morph animation, rotation, and optional snackbar support.
 *
 * SECURITY:
 * - Actual clipboard operations and auto-clear timers must be handled by
 *   a SecureClipboardManager at the security layer.
 */
@Composable
fun CopyButton(
    onCopy: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var copied by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 400),
        label = "copyButtonRotation"
    )

    LaunchedEffect(copied) {
        if (copied) {
            snackbarHostState?.showSnackbar("Copied! Clearing in 30s")
            delay(2000L)
            copied = false
        }
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.MediumImpact)
                rotation += 360f
                onCopy()
                copied = true
            },
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = copied, label = "copyCheck") { isCopied ->
            if (isCopied) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Copied",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.rotate(animatedRotation)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    modifier = Modifier.rotate(animatedRotation)
                )
            }
        }
    }
}

