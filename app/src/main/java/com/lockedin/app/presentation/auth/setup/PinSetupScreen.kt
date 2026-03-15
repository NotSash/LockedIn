package com.lockedin.app.presentation.auth.setup

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.NeumorphicButton
import com.lockedin.app.core.ui.components.NeumorphicPinPad

data class PinSetupCallbacks(
    val onCompleted: () -> Unit
)

@Composable
fun PinSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: SetupViewModel = hiltViewModel(),
    callbacks: PinSetupCallbacks
) {
    val state = viewModel.uiState.value

    LaunchedEffect(state.authState) {
        if (state.authState is com.lockedin.app.presentation.auth.AuthState.Unlocked) {
            callbacks.onCompleted()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set up Quick Unlock",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Use an 8-digit PIN for faster access.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val total = 8
            val filled = if (!state.isOnPinConfirmStep) {
                state.pinFirstEntry.length
            } else {
                state.pinConfirmEntry.length
            }
            repeat(total) { index ->
                val isFilled = index < filled
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .height(16.dp)
                        .weight(1f)
                ) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        color = if (isFilled) MaterialTheme.colorScheme.primary else
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        radius = radius
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = state.pinError != null) {
            Text(
                text = state.pinError.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (!state.isOnPinConfirmStep) {
                "Enter an 8-digit PIN"
            } else {
                "Confirm your PIN"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        NeumorphicPinPad(
            digitsEntered = if (!state.isOnPinConfirmStep) {
                state.pinFirstEntry.length
            } else {
                state.pinConfirmEntry.length
            },
            onDigit = { digit ->
                viewModel.onPinDigitEntered(digit)
                val currentLen = if (!state.isOnPinConfirmStep) {
                    viewModel.uiState.value.pinFirstEntry.length
                } else {
                    viewModel.uiState.value.pinConfirmEntry.length
                }
                if (currentLen == 8) {
                    viewModel.onPinStageComplete()
                }
            },
            onBackspace = { viewModel.onPinBackspace() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        NeumorphicButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            text = "Skip for now",
            onClick = { viewModel.skipPinSetup() }
        )
    }
}

