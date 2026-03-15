package com.lockedin.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
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
import com.lockedin.app.core.ui.components.EmptyStateView
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.core.ui.components.SkeletonLoader
import com.lockedin.app.domain.usecase.password.SortPasswordsUseCase
import com.lockedin.app.presentation.home.components.IssueType
import com.lockedin.app.presentation.home.components.PasswordGridItem
import com.lockedin.app.presentation.home.components.PasswordItemCallbacks
import com.lockedin.app.presentation.home.components.PasswordListItem
import com.lockedin.app.presentation.home.components.QuickActionType
import com.lockedin.app.presentation.home.components.QuickActionsCallbacks
import com.lockedin.app.presentation.home.components.QuickActionsRow
import com.lockedin.app.presentation.home.components.SearchCallbacks
import com.lockedin.app.presentation.home.components.SearchOverlay
import com.lockedin.app.presentation.home.components.SecurityScoreCallbacks
import com.lockedin.app.presentation.home.components.SecurityScoreCard
import com.lockedin.app.presentation.home.components.SortFilterCallbacks
import com.lockedin.app.presentation.home.components.SortFilterSheet

data class HomeCallbacks(
    val onNavigateToGenerator: () -> Unit,
    val onNavigateToAddPassword: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSearch: () -> Unit,
    val onNavigateToSecurityReport: (IssueType?) -> Unit,
    val onNavigateToDetail: (Long) -> Unit,
    val onCopyPassword: (String) -> Unit
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    callbacks: HomeCallbacks
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = {
                    Text(
                        text = "LockedIn",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /* lock action wired in NavHost later */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Lock"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            SkeletonLoader(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            if (state.passwords.isEmpty()) {
                EmptyStateView(
                    title = "Your vault is empty",
                    message = "Add your first password to get started.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            } else {
                HomeContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    state = state,
                    viewModel = viewModel,
                    callbacks = callbacks,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    state: HomeUiState,
    viewModel: HomeViewModel,
    callbacks: HomeCallbacks,
    snackbarHostState: SnackbarHostState
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SecurityScoreCard(
                    report = state.securityReport,
                    callbacks = SecurityScoreCallbacks { issue ->
                        callbacks.onNavigateToSecurityReport(issue)
                    }
                )
            }

            item {
                QuickActionsRow(
                    callbacks = QuickActionsCallbacks { type ->
                        when (type) {
                            QuickActionType.GENERATE -> callbacks.onNavigateToGenerator()
                            QuickActionType.ADD_NEW -> callbacks.onNavigateToAddPassword()
                            QuickActionType.SEARCH -> callbacks.onNavigateToSearch()
                            QuickActionType.HISTORY -> callbacks.onNavigateToHistory()
                        }
                    }
                )
            }

            item {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Vault",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(onClick = { /* view mode toggle wired later */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Change view"
                        )
                    }
                }
            }

            items(state.filteredPasswords, key = { it.id }) { entry ->
                when (state.viewMode) {
                    VaultViewMode.LIST, VaultViewMode.CATEGORY -> {
                        PasswordListItem(
                            entry = entry,
                            callbacks = PasswordItemCallbacks(
                                onClick = { callbacks.onNavigateToDetail(entry.id) },
                                onCopyPassword = {
                                    callbacks.onCopyPassword(entry.password)
                                    LaunchedEffect(entry.id) {
                                        snackbarHostState.showSnackbar("Password copied")
                                    }
                                },
                                onLongPress = { /* context menu sheet later */ }
                            )
                        )
                    }

                    VaultViewMode.GRID -> {
                        PasswordGridItem(
                            entry = entry,
                            callbacks = PasswordItemCallbacks(
                                onClick = { callbacks.onNavigateToDetail(entry.id) },
                                onCopyPassword = {
                                    callbacks.onCopyPassword(entry.password)
                                    LaunchedEffect(entry.id) {
                                        snackbarHostState.showSnackbar("Password copied")
                                    }
                                },
                                onLongPress = { }
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (state.isSearchActive || state.searchQuery.isNotBlank()) {
            SearchOverlay(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                query = state.searchQuery,
                callbacks = SearchCallbacks(
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onClear = { viewModel.clearSearch() }
                )
            )
        }

        if (state.errorMessage != null) {
            LaunchedEffect(state.errorMessage) {
                snackbarHostState.showSnackbar(state.errorMessage)
            }
        }
    }
}

