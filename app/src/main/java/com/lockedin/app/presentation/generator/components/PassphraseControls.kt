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
import com.lockedin.app.core.ui.components.NeumorphicButton
import com.lockedin.app.core.ui.components.NeumorphicSlider
import com.lockedin.app.core.ui.components.NeumorphicSwitch
import com.lockedin.app.domain.usecase.generator.GeneratePassphraseUseCase
import com.lockedin.app.presentation.generator.PassphraseConfig

data class PassphraseCallbacks(
    val onWordCountChanged: (Int) -> Unit,
    val onSeparatorChanged: (GeneratePassphraseUseCase.SeparatorMode) -> Unit,
    val onCapitalizeChanged: (Boolean) -> Unit,
    val onIncludeNumberChanged: (Boolean) -> Unit
)

@Composable
fun PassphraseControls(
    modifier: Modifier = Modifier,
    config: PassphraseConfig,
    callbacks: PassphraseCallbacks
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Passphrase Settings",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Words",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${config.wordCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            NeumorphicSlider(
                value = config.wordCount.toFloat(),
                onValueChange = { callbacks.onWordCountChanged(it.toInt().coerceIn(3, 10)) },
                valueRange = 3f..10f
            )
        }

        Text(
            text = "Separator",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SeparatorChip(
                label = "-",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.HYPHEN
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.HYPHEN) }
            SeparatorChip(
                label = ".",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.PERIOD
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.PERIOD) }
            SeparatorChip(
                label = "space",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.SPACE
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.SPACE) }
            SeparatorChip(
                label = "_",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.UNDERSCORE
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.UNDERSCORE) }
            SeparatorChip(
                label = ",",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.COMMA
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.COMMA) }
            SeparatorChip(
                label = "num",
                selected = config.separatorMode == GeneratePassphraseUseCase.SeparatorMode.NUMBER
            ) { callbacks.onSeparatorChanged(GeneratePassphraseUseCase.SeparatorMode.NUMBER) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Capitalize words",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            NeumorphicSwitch(
                checked = config.capitalizeWords,
                onCheckedChange = { callbacks.onCapitalizeChanged(!config.capitalizeWords) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Include number",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            NeumorphicSwitch(
                checked = config.includeTrailingNumber,
                onCheckedChange = { callbacks.onIncludeNumberChanged(!config.includeTrailingNumber) }
            )
        }
    }
}

@Composable
private fun SeparatorChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NeumorphicButton(
        text = label,
        onClick = onClick
    )
}

