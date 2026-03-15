package com.lockedin.app.presentation.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.presentation.home.components.PasswordItemCallbacks
import com.lockedin.app.presentation.home.components.PasswordListItem

data class SecurityReportCallbacks(
    val onBack: () -> Unit,
    val onPasswordClick: (Long) -> Unit
)

@Composable
fun SecurityReportScreen(
    modifier: Modifier = Modifier,
    viewModel: SecurityReportViewModel = hiltViewModel(),
    callbacks: SecurityReportCallbacks
) {
    val state by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = "Security Report",
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
        val report = state.report

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Analyzing your vault...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else if (report == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage ?: "No data available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Overall score: ${report.overallScore}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${report.totalPasswords} passwords analyzed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                val tabs = listOf(
                    "All" to SecurityIssueFilter.ALL,
                    "Weak" to SecurityIssueFilter.WEAK,
                    "Reused" to SecurityIssueFilter.REUSED,
                    "Old" to SecurityIssueFilter.OLD,
                    "Breached" to SecurityIssueFilter.BREACHED
                )
                val selectedIndex = tabs.indexOfFirst { it.second == state.filter }.coerceAtLeast(0)

                TabRow(selectedTabIndex = selectedIndex) {
                    tabs.forEachIndexed { index, pair ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { viewModel.setFilter(pair.second) },
                            text = { Text(pair.first) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // For now, we only display counts; full filtered lists will be wired
                // when integrating with the password repository in a later phase.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Weak passwords: ${report.weakCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Reused passwords: ${report.reusedCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Old passwords: ${report.oldCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Breached passwords: ${report.breachedCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        if (state.errorMessage != null && !state.isLoading) {
            LaunchedEffect(state.errorMessage) {
                snackbarHostState.showSnackbar(state.errorMessage!!)
            }
        }
    }
}

