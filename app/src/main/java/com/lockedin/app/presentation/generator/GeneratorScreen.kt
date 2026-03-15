package com.lockedin.app.presentation.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.security.SecureClipboardManager
import com.lockedin.app.core.ui.components.AnimatedStrengthMeter
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.GradientButton
import com.lockedin.app.presentation.generator.components.CharacterToggleCallbacks
import com.lockedin.app.presentation.generator.components.CharacterTypeToggles
import com.lockedin.app.presentation.generator.components.LengthSliderControl
import com.lockedin.app.presentation.generator.components.PassphraseCallbacks
import com.lockedin.app.presentation.generator.components.PassphraseControls
import com.lockedin.app.presentation.generator.components.PasswordDisplayCard
import com.lockedin.app.presentation.generator.components.PasswordHistoryCallbacks
import com.lockedin.app.presentation.generator.components.PasswordHistorySheet
import kotlinx.coroutines.launch

data class GeneratorCallbacks(
    val onSaveToVault: (String) -> Unit
)

@Composable
fun GeneratorScreen(
    modifier: Modifier = Modifier,
    viewModel: GeneratorViewModel = hiltViewModel(),
    secureClipboardManager: SecureClipboardManager,
    clipboardTimeoutMillis: Long,
    callbacks: GeneratorCallbacks
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = {
                    Text(
                        text = "Generator",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.generate() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Regenerate"
                        )
                    }
                    IconButton(onClick = { viewModel.showHistorySheet(true) }) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = "History"
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientButton(
                    modifier = Modifier.weight(1f),
                    text = "Random",
                    onClick = { viewModel.setMode(GeneratorMode.RANDOM) },
                    enabled = state.mode == GeneratorMode.RANDOM
                )
                Spacer(modifier = Modifier.width(12.dp))
                GradientButton(
                    modifier = Modifier.weight(1f),
                    text = "Passphrase",
                    onClick = { viewModel.setMode(GeneratorMode.PASSPHRASE) },
                    enabled = state.mode == GeneratorMode.PASSPHRASE
                )
            }

            PasswordDisplayCard(
                password = state.currentPassword,
                isGenerating = state.isGenerating
            )

            AnimatedStrengthMeter(
                modifier = Modifier.fillMaxWidth(),
                strengthScore = state.currentPassword?.strengthScore ?: 0
            )

            when (state.mode) {
                GeneratorMode.RANDOM -> {
                    LengthSliderControl(
                        length = state.randomConfig.length,
                        onLengthChanged = viewModel::updateRandomLength
                    )

                    CharacterTypeToggles(
                        config = state.randomConfig,
                        callbacks = CharacterToggleCallbacks(
                            onToggleUpper = {
                                viewModel.toggleRandomFlag { it.copy(includeUpper = !it.includeUpper) }
                            },
                            onToggleLower = {
                                viewModel.toggleRandomFlag { it.copy(includeLower = !it.includeLower) }
                            },
                            onToggleNumbers = {
                                viewModel.toggleRandomFlag { it.copy(includeNumbers = !it.includeNumbers) }
                            },
                            onToggleSymbols = {
                                viewModel.toggleRandomFlag { it.copy(includeSymbols = !it.includeSymbols) }
                            }
                        )
                    )
                }

                GeneratorMode.PASSPHRASE -> {
                    PassphraseControls(
                        config = state.passphraseConfig,
                        callbacks = PassphraseCallbacks(
                            onWordCountChanged = { viewModel.updatePassphraseConfig { cfg -> cfg.copy(wordCount = it) } },
                            onSeparatorChanged = { sep ->
                                viewModel.updatePassphraseConfig { cfg -> cfg.copy(separatorMode = sep) }
                            },
                            onCapitalizeChanged = { enabled ->
                                viewModel.updatePassphraseConfig { cfg -> cfg.copy(capitalizeWords = enabled) }
                            },
                            onIncludeNumberChanged = { enabled ->
                                viewModel.updatePassphraseConfig { cfg -> cfg.copy(includeTrailingNumber = enabled) }
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val current = state.currentPassword ?: return@IconButton
                        val text = String(current.value)
                        secureClipboardManager.copyWithTimeout(
                            id = System.currentTimeMillis(),
                            text = text,
                            timeoutMillis = clipboardTimeoutMillis
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied! Clipboard will clear automatically.")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy"
                    )
                }

                GradientButton(
                    modifier = Modifier.weight(1f),
                    text = "Save to Vault",
                    onClick = {
                        val current = state.currentPassword ?: return@GradientButton
                        val text = String(current.value)
                        callbacks.onSaveToVault(text)
                        viewModel.markCurrentSavedToVault()
                    }
                )
            }

            if (state.errorMessage != null) {
                LaunchedEffect(state.errorMessage) {
                    snackbarHostState.showSnackbar(state.errorMessage!!)
                }
            }
        }
    }

    if (state.isHistorySheetVisible) {
        val scopeSheet = rememberCoroutineScope()
        PasswordHistorySheet(
            history = state.history,
            scope = scopeSheet,
            callbacks = PasswordHistoryCallbacks(
                onDismiss = { viewModel.showHistorySheet(false) },
                onClearAll = { /* implemented later with dedicated use case */ },
                onCopy = { history ->
                    val text = String(history.password)
                    secureClipboardManager.copyWithTimeout(
                        id = history.id,
                        text = text,
                        timeoutMillis = clipboardTimeoutMillis
                    )
                },
                onDelete = { /* delete from history can be added via new use case later */ }
            )
        )
    }
}

