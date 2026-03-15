package com.lockedin.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.lockedin.app.core.ui.components.GlassmorphicCard
import com.lockedin.app.domain.model.PasswordEntry

@Composable
fun PasswordGridItem(
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
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FaviconGrid(
                siteName = entry.siteName,
                faviconUrl = entry.faviconUrl
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.siteName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = entry.username,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FaviconGrid(
    siteName: String,
    faviconUrl: String?
) {
    val bgColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        if (!faviconUrl.isNullOrBlank()) {
            AsyncImage(
                model = faviconUrl,
                contentDescription = siteName,
                modifier = Modifier
                    .size(40.dp)
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

