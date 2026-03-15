package com.lockedin.app.presentation.auth.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.lockedin.app.core.security.BiometricHelper

data class BiometricSetupCallbacks(
    val onCompleted: () -> Unit
)

@Composable
fun BiometricSetupScreen(
    modifier: Modifier = Modifier,
    activity: FragmentActivity,
    biometricHelper: BiometricHelper,
    callbacks: BiometricSetupCallbacks
) {
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val canUse = remember { biometricHelper.canUseBiometric() }

    LaunchedEffect(canUse) {
        if (!canUse) {
            callbacks.onCompleted()
        }
    }

    if (!canUse) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enable Biometric Unlock",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Use your fingerprint or face to unlock your vault instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = {
                biometricHelper.authenticate(
                    activity = activity,
                    title = "Enable Biometric Unlock",
                    subtitle = "Confirm your identity to secure the key",
                    onSuccess = {
                        callbacks.onCompleted()
                    },
                    onError = { error ->
                        errorMessage.value = error
                    }
                )
            }
        ) {
            Text("Enable")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = { callbacks.onCompleted() }
        ) {
            Text("Skip for now")
        }

        AnimatedVisibility(visible = errorMessage.value != null) {
            Text(
                text = errorMessage.value.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}

