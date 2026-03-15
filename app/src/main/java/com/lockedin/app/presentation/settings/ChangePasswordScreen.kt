package com.lockedin.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.security.MasterKeyManager
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.GradientButton
import com.lockedin.app.core.ui.components.NeumorphicTextField

data class ChangePasswordCallbacks(
    val onBack: () -> Unit
)

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    masterKeyManager: MasterKeyManager,
    callbacks: ChangePasswordCallbacks
) {
    val current = remember { mutableStateOf("") }
    val newPass = remember { mutableStateOf("") }
    val confirm = remember { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = "Change Master Password",
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NeumorphicTextField(
                value = current.value,
                onValueChange = { current.value = it },
                label = "Current Password",
                isPassword = true
            )
            NeumorphicTextField(
                value = newPass.value,
                onValueChange = { newPass.value = it },
                label = "New Password",
                isPassword = true
            )
            NeumorphicTextField(
                value = confirm.value,
                onValueChange = { confirm.value = it },
                label = "Confirm New Password",
                isPassword = true
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Update Password",
                onClick = {
                    val currentChars = current.value.toCharArray()
                    if (!masterKeyManager.verifyMasterPassword(currentChars)) {
                        error.value = "Current password is incorrect"
                        return@GradientButton
                    }
                    if (newPass.value != confirm.value || newPass.value.isBlank()) {
                        error.value = "New passwords do not match"
                        return@GradientButton
                    }
                    masterKeyManager.setMasterPassword(newPass.value.toCharArray())
                    error.value = "Master password updated"
                }
            )

            if (error.value != null) {
                LaunchedEffect(error.value) {
                    snackbarHostState.showSnackbar(error.value!!)
                }
            }
        }
    }
}

