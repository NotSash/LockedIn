package com.lockedin.app.presentation.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.security.BiometricHelper
import com.lockedin.app.core.ui.components.GlassmorphicSurface
import com.lockedin.app.core.ui.components.NeumorphicButton
import com.lockedin.app.core.ui.components.NeumorphicPinPad
import com.lockedin.app.core.ui.components.NeumorphicTextField
import kotlinx.coroutines.delay

data class LoginCallbacks(
    val onAuthenticated: () -> Unit
)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    activity: FragmentActivity,
    biometricHelper: BiometricHelper,
    viewModel: LoginViewModel = hiltViewModel(),
    callbacks: LoginCallbacks
) {
    val state = viewModel.uiState.value

    LaunchedEffect(Unit) {
        // Slight delay before prompting biometric so that the screen has rendered.
        delay(500)
        if (biometricHelper.canUseBiometric()) {
            biometricHelper.authenticate(
                activity = activity,
                title = "Unlock LockedIn",
                subtitle = "Use biometrics to unlock your vault",
                onSuccess = {
                    viewModel.onBiometricSuccess()
                },
                onError = { error ->
                    viewModel.onBiometricError(error)
                }
            )
        }
    }

    LaunchedEffect(state.authState) {
        if (state.authState is com.lockedin.app.presentation.auth.AuthState.Unlocked) {
            callbacks.onAuthenticated()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        GlassmorphicSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\uD83D\uDD10",
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LockedIn",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isPinAvailable) {
                NeumorphicButton(
                    modifier = Modifier.weight(1f),
                    text = "Use PIN",
                    onClick = { viewModel.setMethod(LoginMethod.PIN) }
                )
            }
            Spacer(modifier = Modifier.height(0.dp))
            NeumorphicButton(
                modifier = Modifier.weight(1f),
                text = "Use Master Password",
                onClick = { viewModel.setMethod(LoginMethod.MASTER_PASSWORD) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = state.method == LoginMethod.PIN && state.isPinAvailable) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter your 8-digit PIN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                NeumorphicPinPad(
                    digitsEntered = state.pinDigits.length,
                    onDigit = { viewModel.onPinDigit(it) },
                    onBackspace = { viewModel.onPinBackspace() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        AnimatedVisibility(visible = state.method == LoginMethod.MASTER_PASSWORD) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeumorphicTextField(
                    value = state.masterPassword,
                    onValueChange = viewModel::onMasterPasswordChanged,
                    label = "Master Password",
                    placeholder = "Enter master password",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeumorphicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    text = if (state.isSubmitting) "Unlocking..." else "Unlock",
                    onClick = { viewModel.submitMasterPassword() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = state.errorMessage != null) {
            Text(
                text = state.errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        AnimatedVisibility(visible = state.isCooldownActive) {
            Text(
                text = "Too many attempts. Try again in ${state.cooldownSecondsRemaining}s.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Text(
            text = "Attempts remaining: ${state.attemptsRemaining}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

