package com.lockedin.app.presentation.addEdit

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.AnimatedStrengthMeter
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.GradientButton
import com.lockedin.app.core.ui.components.NeumorphicTextField

data class AddEditCallbacks(
    val onBack: () -> Unit,
    val onSaved: (Long) -> Unit
)

@Composable
fun AddEditScreen(
    modifier: Modifier = Modifier,
    passwordId: Long?,
    viewModel: AddEditViewModel = hiltViewModel(),
    callbacks: AddEditCallbacks
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(passwordId) {
        if (passwordId != null && passwordId != 0L) {
            viewModel.loadForEdit(passwordId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = if (state.isEditMode) "Edit Password" else "Add Password",
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NeumorphicTextField(
                value = state.siteName,
                onValueChange = viewModel::onSiteNameChange,
                label = "Site / App Name",
                placeholder = "Example: Gmail"
            )

            NeumorphicTextField(
                value = state.siteUrl,
                onValueChange = viewModel::onSiteUrlChange,
                label = "Website URL",
                placeholder = "https://example.com"
            )

            NeumorphicTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                label = "Username / Email",
                placeholder = "you@example.com"
            )

            NeumorphicTextField(
                value = state.password,
                onValueChange = { value ->
                    // Basic strength heuristic: length * 4, capped.
                    val score = (value.length * 4).coerceIn(0, 100)
                    viewModel.onPasswordChange(value, score)
                },
                label = "Password",
                placeholder = "Enter or generate a password",
                isPassword = true
            )

            AnimatedStrengthMeter(
                modifier = Modifier.fillMaxWidth(),
                strengthScore = state.strengthScore
            )

            NeumorphicTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Notes",
                placeholder = "Add notes, security questions, recovery info..."
            )

            NeumorphicTextField(
                value = state.totpSecret,
                onValueChange = viewModel::onTotpSecretChange,
                label = "TOTP / 2FA Secret",
                placeholder = "Enter TOTP secret manually"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tags",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicTextField(
                    modifier = Modifier.weight(1f),
                    value = state.tagInput,
                    onValueChange = viewModel::onTagInputChange,
                    label = "Add tag",
                    placeholder = "press enter to add"
                )
                Spacer(modifier = Modifier.width(8.dp))
                GradientButton(
                    text = "Add",
                    onClick = { viewModel.addTagFromInput() }
                )
            }

            if (state.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.tags.forEach { tag ->
                        com.lockedin.app.core.ui.components.Chip(
                            text = tag,
                            onRemove = { viewModel.removeTag(tag) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Custom Fields",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            state.customFields.forEachIndexed { index, field ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeumorphicTextField(
                        modifier = Modifier.weight(1f),
                        value = field.label,
                        onValueChange = { viewModel.updateCustomFieldLabel(index, it) },
                        label = "Label"
                    )
                    NeumorphicTextField(
                        modifier = Modifier.weight(1f),
                        value = field.value,
                        onValueChange = { viewModel.updateCustomFieldValue(index, it) },
                        label = "Value"
                    )
                    IconButton(onClick = { viewModel.removeCustomField(index) }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Remove field"
                        )
                    }
                }
            }

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Add Custom Field",
                onClick = { viewModel.addCustomField() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                text = if (state.isSaving) "Saving..." else "Save",
                enabled = state.canSave && !state.isSaving,
                onClick = {
                    viewModel.save { id ->
                        callbacks.onSaved(id)
                    }
                }
            )

            if (state.errorMessage != null) {
                LaunchedEffect(state.errorMessage) {
                    snackbarHostState.showSnackbar(state.errorMessage!!)
                }
            }
        }
    }
}

