package com.lockedin.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lockedin.app.core.ui.components.CopyButton
import com.lockedin.app.core.ui.components.GlassmorphicCard
import com.lockedin.app.domain.model.PasswordEntry

data class PasswordItemCallbacks(
    val onClick: (PasswordEntry) -> Unit,
    val onCopyPassword: (PasswordEntry) -> Unit,
    val onLongPress: (PasswordEntry) -> Unit
)

@Composable
fun PasswordListItem(
    modifier: Modifier = Modifier,
    entry: PasswordEntry,
    callbacks: PasswordItemCallbacks
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { callbacks.onClick(entry) }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Favicon(
                    siteName = entry.siteName,
                    faviconUrl = entry.faviconUrl,
                    colorHex = entry.colorLabelHex
                )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = entry.siteName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.username,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = entry.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CopyButton(
                onCopy = { callbacks.onCopyPassword(entry) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Modified ${formatRelativeTime(entry.updatedAtMillis)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Favicon(
    siteName: String,
    faviconUrl: String?
) {
    val bgColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (!faviconUrl.isNullOrBlank()) {
            AsyncImage(
                model = faviconUrl,
                contentDescription = siteName,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = siteName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.bodyLarge,
                color = bgColor
            )
        }
    }
}

private fun formatRelativeTime(updatedAtMillis: Long): String {
    // Placeholder humanized timestamp; can be replaced with DateTimeUtils later.
    return "recently"
}

