package com.lockedin.app.presentation.generator.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockedin.app.core.ui.components.GlassmorphicSurface
import com.lockedin.app.core.ui.components.PasswordText
import com.lockedin.app.domain.model.GeneratedPassword

@Composable
fun PasswordDisplayCard(
    modifier: Modifier = Modifier,
    password: GeneratedPassword?,
    isGenerating: Boolean
) {
    GlassmorphicSurface(
        modifier = modifier.fillMaxWidth()
    ) {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            contentAlignment = Alignment.CenterStart
        ) {
            if (password == null) {
                Text(
                    text = "Tap generate to create a secure password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val textSize = when {
                    password.length <= 24 -> 24.sp
                    password.length <= 40 -> 18.sp
                    else -> 16.sp
                }
                PasswordText(
                    value = password.value,
                    fontSize = textSize,
                    isAnimating = isGenerating
                )
            }
        }
    }
}

