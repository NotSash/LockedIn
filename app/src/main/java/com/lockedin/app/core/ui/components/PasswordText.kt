package com.lockedin.app.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.lockedin.app.core.ui.theme.JetBrainsMono

/**
 * Displays password characters with per-character coloring and subtle scale animation.
 *
 * - Uppercase: Accent Secondary
 * - Lowercase: Text Primary
 * - Numbers: Accent Primary
 * - Symbols: Error color
 */
@Composable
fun PasswordText(
    password: String,
    modifier: Modifier = Modifier
) {
    val chars = password.toCharArray()
    val scales = remember(chars.size) {
        mutableStateListOf<Float>().apply { repeat(chars.size) { add(1f) } }
    }

    Row(modifier = modifier.wrapContentWidth()) {
        chars.forEachIndexed { index, c ->
            val scale = scales.getOrNull(index) ?: 1f
            Text(
                text = c.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = JetBrainsMono
                ),
                color = characterColor(c),
                modifier = Modifier.scale(scale)
            )
        }
    }
}

private fun characterColor(c: Char): Color {
    return when {
        c.isUpperCase() -> Color(0xFF00E5FF) // Accent Secondary
        c.isLowerCase() -> Color(0xFFF1F5F9) // Text Primary (dark theme default)
        c.isDigit() -> Color(0xFF7C4DFF)     // Accent Primary
        else -> Color(0xFFFF5252)           // Symbols -> Error
    }
}

