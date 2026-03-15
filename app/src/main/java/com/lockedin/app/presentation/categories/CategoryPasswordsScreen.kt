package com.lockedin.app.presentation.categories

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.security.SecureClipboardManager
import com.lockedin.app.core.ui.components.GlassmorphicTopBar
import com.lockedin.app.presentation.home.components.PasswordItemCallbacks
import com.lockedin.app.presentation.home.components.PasswordListItem

data class CategoryPasswordsCallbacks(
    val onBack: () -> Unit,
    val onPasswordClick: (Long) -> Unit
)

@Composable
fun CategoryPasswordsScreen(
    modifier: Modifier = Modifier,
    categoryName: String,
    viewModel: CategoriesViewModel = hiltViewModel(),
    secureClipboardManager: SecureClipboardManager,
    clipboardTimeoutMillis: Long,
    callbacks: CategoryPasswordsCallbacks
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val passwords = state.passwords.filter { it.category == categoryName }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassmorphicTopBar(
                title = categoryName,
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
        if (passwords.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No passwords in this category yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(passwords, key = { it.id }) { entry ->
                    PasswordListItem(
                        entry = entry,
                        callbacks = PasswordItemCallbacks(
                            onClick = { callbacks.onPasswordClick(entry.id) },
                            onCopyPassword = {
                                val text = entry.password.concatToString()
                                secureClipboardManager.copyWithTimeout(
                                    id = entry.id * 100 + 1,
                                    text = text,
                                    timeoutMillis = clipboardTimeoutMillis
                                )
                            },
                            onLongPress = { }
                        )
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

