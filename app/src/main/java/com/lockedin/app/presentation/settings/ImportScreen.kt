package com.lockedin.app.presentation.settings

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockedin.app.domain.model.ExportFormat
import com.lockedin.app.domain.usecase.data.ImportVaultUseCase

data class ImportScreenCallbacks(
    val onBack: () -> Unit,
    val onPickFile: (ExportFormat) -> Unit
)

@Composable
fun ImportScreen(
    modifier: Modifier = Modifier,
    importVaultUseCase: ImportVaultUseCase,
    callbacks: ImportScreenCallbacks,
    pendingImportUri: Uri?,
    pendingFormat: ExportFormat?
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val message = remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Import Passwords",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Select a LockedIn backup or CSV file from another password manager.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { callbacks.onPickFile(ExportFormat.ENCRYPTED_JSON) }
            ) {
                Text("Import Encrypted Backup (.lockedin)")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { callbacks.onPickFile(ExportFormat.CSV) }
            ) {
                Text("Import CSV")
            }
        }

        if (pendingImportUri != null && pendingFormat != null) {
            LaunchedEffect(pendingImportUri, pendingFormat) {
                // Actual InputStream opening is handled in Activity; this composable only
                // triggers use case via a higher-level coordinator in a later phase.
                message.value = "Import started..."
            }
        }

        if (message.value != null) {
            LaunchedEffect(message.value) {
                snackbarHostState.showSnackbar(message.value!!)
            }
        }
    }
}

