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
import com.lockedin.app.core.security.PinManager
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.GradientButton
import com.lockedin.app.core.ui.components.NeumorphicTextField

data class ChangePinCallbacks(
    val onBack: () -> Unit
)

@Composable
fun ChangePinScreen(
    modifier: Modifier = Modifier,
    pinManager: PinManager,
    callbacks: ChangePinCallbacks
) {
    val current = remember { mutableStateOf("") }
    val newPin = remember { mutableStateOf("") }
    val confirm = remember { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = "Change PIN",
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
                onValueChange = { current.value = it.filter { ch -> ch.isDigit() } },
                label = "Current PIN",
                isPassword = true
            )
            NeumorphicTextField(
                value = newPin.value,
                onValueChange = { newPin.value = it.filter { ch -> ch.isDigit() }.take(8) },
                label = "New PIN (8 digits)",
                isPassword = true
            )
            NeumorphicTextField(
                value = confirm.value,
                onValueChange = { confirm.value = it.filter { ch -> ch.isDigit() }.take(8) },
                label = "Confirm New PIN",
                isPassword = true
            )

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Update PIN",
                onClick = {
                    val currentChars = current.value.toCharArray()
                    if (!pinManager.verifyPin(currentChars)) {
                        error.value = "Current PIN is incorrect"
                        return@GradientButton
                    }
                    if (newPin.value != confirm.value || newPin.value.length != 8) {
                        error.value = "PINs do not match or are not 8 digits"
                        return@GradientButton
                    }
                    pinManager.setPin(newPin.value.toCharArray())
                    error.value = "PIN updated"
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

