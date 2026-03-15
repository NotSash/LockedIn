package com.lockedin.app.presentation.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WarningBanners(
    modifier: Modifier = Modifier,
    isWeak: Boolean,
    isReused: Boolean,
    isOld: Boolean,
    isBreached: Boolean
    ) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (isWeak) {
            Banner(
                text = "Weak Password — consider generating a stronger one.",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        if (isReused) {
            Banner(
                text = "Reused Password — used on multiple sites.",
                color = MaterialTheme.colorScheme.error
            )
        }
        if (isOld) {
            Banner(
                text = "Old Password — consider rotating this credential.",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        if (isBreached) {
            Banner(
                text = "Breached Password — change this password immediately.",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun Banner(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

