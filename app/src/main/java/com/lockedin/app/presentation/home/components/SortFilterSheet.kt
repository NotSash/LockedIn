package com.lockedin.app.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lockedin.app.domain.usecase.password.SortPasswordsUseCase
import kotlinx.coroutines.launch

data class SortFilterCallbacks(
    val onSortSelected: (SortPasswordsUseCase.SortMode) -> Unit,
    val onDismiss: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterSheet(
    currentSort: SortPasswordsUseCase.SortMode,
    callbacks: SortFilterCallbacks
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                callbacks.onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Sort & Filter",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            SortOption(
                label = "Name A–Z",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.NAME_ASC
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.NAME_ASC)
                callbacks.onDismiss()
            }
            SortOption(
                label = "Name Z–A",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.NAME_DESC
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.NAME_DESC)
                callbacks.onDismiss()
            }
            SortOption(
                label = "Last Modified (Newest)",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.MODIFIED_NEWEST
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.MODIFIED_NEWEST)
                callbacks.onDismiss()
            }
            SortOption(
                label = "Last Modified (Oldest)",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.MODIFIED_OLDEST
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.MODIFIED_OLDEST)
                callbacks.onDismiss()
            }
            SortOption(
                label = "Most Used",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.MOST_USED
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.MOST_USED)
                callbacks.onDismiss()
            }
            SortOption(
                label = "Weakest Passwords First",
                isSelected = currentSort == SortPasswordsUseCase.SortMode.WEAKEST_FIRST
            ) {
                callbacks.onSortSelected(SortPasswordsUseCase.SortMode.WEAKEST_FIRST)
                callbacks.onDismiss()
            }
        }
    }
}

@Composable
private fun SortOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
        )
    }
}

