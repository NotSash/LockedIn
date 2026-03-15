package com.lockedin.app.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockedin.app.core.ui.components.GlassmorphicSurface
import com.lockedin.app.core.ui.components.SecurityScoreGauge
import com.lockedin.app.domain.model.SecurityReport

data class SecurityScoreCallbacks(
    val onChipClick: (IssueType) -> Unit
)

enum class IssueType {
    WEAK,
    REUSED,
    OLD,
    BREACHED
}

@Composable
fun SecurityScoreCard(
    modifier: Modifier = Modifier,
    report: SecurityReport?,
    callbacks: SecurityScoreCallbacks
) {
    GlassmorphicSurface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecurityScoreGauge(
                modifier = Modifier.weight(1f),
                score = report?.overallScore ?: 0
            )

            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your Security Score",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IssueChip(
                        label = "${report?.weakCount ?: 0} Weak",
                        isCritical = (report?.weakCount ?: 0) > 0,
                        onClick = { callbacks.onChipClick(IssueType.WEAK) }
                    )
                    IssueChip(
                        label = "${report?.reusedCount ?: 0} Reused",
                        isCritical = (report?.reusedCount ?: 0) > 0,
                        onClick = { callbacks.onChipClick(IssueType.REUSED) }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IssueChip(
                        label = "${report?.oldCount ?: 0} Old",
                        isCritical = (report?.oldCount ?: 0) > 0,
                        onClick = { callbacks.onChipClick(IssueType.OLD) }
                    )
                    IssueChip(
                        label = "${report?.breachedCount ?: 0} Breached",
                        isCritical = (report?.breachedCount ?: 0) > 0,
                        onClick = { callbacks.onChipClick(IssueType.BREACHED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IssueChip(
    label: String,
    isCritical: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        isCritical && label.contains("Breached") ->
            MaterialTheme.colorScheme.error.copy(alpha = 0.2f)

        isCritical ->
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)

        else ->
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = when {
        isCritical && label.contains("Breached") ->
            MaterialTheme.colorScheme.error

        isCritical ->
            MaterialTheme.colorScheme.primary

        else ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    androidx.compose.material3.Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

