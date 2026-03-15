package com.lockedin.app.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.components.GlassmorphicSurface

enum class QuickActionType {
    GENERATE,
    ADD_NEW,
    SEARCH,
    HISTORY
}

data class QuickAction(
    val type: QuickActionType,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class QuickActionsCallbacks(
    val onActionClick: (QuickActionType) -> Unit
)

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier,
    callbacks: QuickActionsCallbacks
) {
    val actions = listOf(
        QuickAction(QuickActionType.GENERATE, "Generate", Icons.Rounded.Key),
        QuickAction(QuickActionType.ADD_NEW, "Add New", Icons.Rounded.Add),
        QuickAction(QuickActionType.SEARCH, "Search", Icons.Rounded.Search),
        QuickAction(QuickActionType.HISTORY, "History", Icons.Rounded.History)
    )

    LazyRow(
        modifier = modifier
            .height(130.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(actions) { action ->
            QuickActionCard(
                action = action,
                onClick = { callbacks.onActionClick(action.type) }
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit
) {
    GlassmorphicSurface(
        modifier = Modifier
            .height(120.dp)
            .width(100.dp)
            .clickableWithScale(onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun Modifier.clickableWithScale(onClick: () -> Unit): Modifier =
    this.then(
        androidx.compose.foundation.clickable(
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    )

