package com.lockedin.app.presentation.generator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.components.NeumorphicSwitch
import com.lockedin.app.presentation.generator.RandomGeneratorConfig

data class CharacterToggleCallbacks(
    val onToggleUpper: () -> Unit,
    val onToggleLower: () -> Unit,
    val onToggleNumbers: () -> Unit,
    val onToggleSymbols: () -> Unit
)

@Composable
fun CharacterTypeToggles(
    modifier: Modifier = Modifier,
    config: RandomGeneratorConfig,
    callbacks: CharacterToggleCallbacks
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Character Types",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ToggleRow(
                label = "Uppercase (A–Z)",
                checked = config.includeUpper,
                onCheckedChange = callbacks.onToggleUpper
            )
            ToggleRow(
                label = "Lowercase (a–z)",
                checked = config.includeLower,
                onCheckedChange = callbacks.onToggleLower
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ToggleRow(
                label = "Numbers (0–9)",
                checked = config.includeNumbers,
                onCheckedChange = callbacks.onToggleNumbers
            )
            ToggleRow(
                label = "Symbols (!@#)",
                checked = config.includeSymbols,
                onCheckedChange = callbacks.onToggleSymbols
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        NeumorphicSwitch(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}

