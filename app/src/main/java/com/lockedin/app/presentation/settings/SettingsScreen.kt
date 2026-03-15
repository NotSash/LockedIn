package com.lockedin.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.GradientButton

data class SettingsCallbacks(
    val onBack: () -> Unit,
    val onChangeMasterPassword: () -> Unit,
    val onChangePin: () -> Unit,
    val onOpenExport: () -> Unit,
    val onOpenImport: () -> Unit,
    val onOpenExcludedApps: () -> Unit
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    callbacks: SettingsCallbacks
) {
    val state by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = callbacks.onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Change Master Password",
                onClick = callbacks.onChangeMasterPassword
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Change PIN",
                onClick = callbacks.onChangePin
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Block screenshots", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Prevent screenshots and screen recording.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.blockScreenshots,
                    onCheckedChange = { viewModel.updateBlockScreenshots(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Haptic feedback", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = state.hapticsEnabled,
                    onCheckedChange = { viewModel.updateHaptics(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Export Vault",
                onClick = callbacks.onOpenExport
            )
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Import Passwords",
                onClick = callbacks.onOpenImport
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Autofill",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Excluded Apps",
                onClick = callbacks.onOpenExcludedApps
            )
        }

        if (state.errorMessage != null && !state.isLoading) {
            LaunchedEffect(state.errorMessage) {
                snackbarHostState.showSnackbar(state.errorMessage!!)
            }
        }
    }
}

