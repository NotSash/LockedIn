package com.lockedin.app.presentation.auth.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.AnimatedStrengthMeter
import com.lockedin.app.core.ui.components.GlassmorphicSurface
import com.lockedin.app.core.ui.components.NeumorphicTextField
import com.lockedin.app.core.ui.components.GradientButton

data class MasterPasswordCallbacks(
    val onCompleted: () -> Unit
)

@Composable
fun MasterPasswordSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: SetupViewModel = hiltViewModel(),
    callbacks: MasterPasswordCallbacks
) {
    val state = viewModel.uiState.value

    if (state.authState != com.lockedin.app.presentation.auth.AuthState.SetupRequired) {
        // Setup completed; move forward in the flow.
        callbacks.onCompleted()
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Create Master Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This is the only password you'll need to remember.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            NeumorphicTextField(
                value = state.masterPassword,
                onValueChange = viewModel::onMasterPasswordChanged,
                label = "Master Password",
                placeholder = "Enter master password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            val score = calculateStrengthScore(state.requirements)
            AnimatedStrengthMeter(
                modifier = Modifier.fillMaxWidth(),
                strengthScore = score
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequirementsChecklist(state.requirements)

            Spacer(modifier = Modifier.height(16.dp))

            NeumorphicTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = "Confirm Password",
                placeholder = "Re-enter master password",
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val enabled = state.canSubmit && !state.isSubmitting
        val alpha by animateFloatAsState(
            targetValue = if (enabled) 1f else 0.4f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            label = "createVaultAlpha"
        )

        GradientButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp)
                .alpha(alpha),
            text = if (state.isSubmitting) "Creating..." else "Create My Vault",
            onClick = { viewModel.submitMasterPassword() },
            enabled = enabled
        )

        AnimatedVisibility(visible = state.isSubmitting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Securing your vault...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RequirementsChecklist(requirements: MasterPasswordRequirements) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RequirementRow(
            met = requirements.minLength,
            text = "At least 12 characters"
        )
        RequirementRow(
            met = requirements.hasUppercase,
            text = "Contains uppercase letter (A-Z)"
        )
        RequirementRow(
            met = requirements.hasLowercase,
            text = "Contains lowercase letter (a-z)"
        )
        RequirementRow(
            met = requirements.hasDigit,
            text = "Contains a number (0-9)"
        )
        RequirementRow(
            met = requirements.hasSymbol,
            text = "Contains a symbol (!@#\$%...)"
        )
    }
}

@Composable
private fun RequirementRow(
    met: Boolean,
    text: String
) {
    val color: Color = if (met) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    val icon = if (met) "✓" else "○"
    val scale by animateFloatAsState(
        targetValue = if (met) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "requirementScale"
    )

    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            color = color,
            modifier = Modifier
                .padding(end = 8.dp)
                .scale(scale)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

private fun calculateStrengthScore(requirements: MasterPasswordRequirements): Int {
    var score = 0
    if (requirements.minLength) score += 30
    if (requirements.hasUppercase) score += 15
    if (requirements.hasLowercase) score += 15
    if (requirements.hasDigit) score += 20
    if (requirements.hasSymbol) score += 20
    return score.coerceIn(0, 100)
}

