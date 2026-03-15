package com.lockedin.app.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.security.SecureClipboardManager
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.presentation.detail.components.DetailFieldCallbacks
import com.lockedin.app.presentation.detail.components.DetailFieldRow
import com.lockedin.app.presentation.detail.components.TotpDisplay
import com.lockedin.app.presentation.detail.components.WarningBanners

data class DetailCallbacks(
    val onBack: () -> Unit,
    val onDeleted: () -> Unit,
    val onEdit: (Long) -> Unit
)

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    passwordId: Long,
    viewModel: DetailViewModel = hiltViewModel(),
    secureClipboardManager: SecureClipboardManager,
    clipboardTimeoutMillis: Long,
    callbacks: DetailCallbacks
) {
    val state by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(passwordId) {
        viewModel.load(passwordId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = state.entry?.siteName ?: "Details",
                navigationIcon = {
                    IconButton(onClick = callbacks.onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.delete { callbacks.onDeleted() } }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val entry = state.entry
        if (entry == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage ?: "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                WarningBanners(
                    isWeak = entry.strengthBucket == com.lockedin.app.domain.model.PasswordStrength.WEAK,
                    isReused = false,
                    isOld = false,
                    isBreached = entry.isCompromised
                )

                DetailFieldRow(
                    label = "Username",
                    value = entry.username,
                    callbacks = DetailFieldCallbacks(
                        onCopy = {
                            secureClipboardManager.copyWithTimeout(
                                id = entry.id * 10 + 1,
                                text = entry.username,
                                timeoutMillis = clipboardTimeoutMillis
                            )
                        }
                    )
                )

                DetailFieldRow(
                    label = "Password",
                    value = entry.password.concatToString(),
                    masked = !state.isPasswordVisible,
                    callbacks = DetailFieldCallbacks(
                        onCopy = {
                            val text = entry.password.concatToString()
                            secureClipboardManager.copyWithTimeout(
                                id = entry.id * 10 + 2,
                                text = text,
                                timeoutMillis = clipboardTimeoutMillis
                            )
                        }
                    )
                )

                DetailFieldRow(
                    label = "Website",
                    value = entry.siteUrl,
                    callbacks = DetailFieldCallbacks(
                        onCopy = {
                            secureClipboardManager.copyWithTimeout(
                                id = entry.id * 10 + 3,
                                text = entry.siteUrl,
                                timeoutMillis = clipboardTimeoutMillis
                            )
                        }
                    )
                )

                if (!entry.notes.isNullOrBlank()) {
                    DetailFieldRow(
                        label = "Notes",
                        value = entry.notes,
                        callbacks = DetailFieldCallbacks()
                    )
                }

                if (!entry.totpSecret.isNullOrBlank()) {
                    TotpDisplay(
                        code = "000000",
                        progress = 0.5f
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Created: ${entry.createdAtMillis}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Last Modified: ${entry.updatedAtMillis}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (state.errorMessage != null && state.entry != null) {
            LaunchedEffect(state.errorMessage) {
                snackbarHostState.showSnackbar(state.errorMessage!!)
            }
        }
    }
}

