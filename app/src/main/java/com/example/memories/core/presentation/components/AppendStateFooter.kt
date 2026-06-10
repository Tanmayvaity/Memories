package com.example.memories.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState

@Composable
fun AppendStateFooter(
    appendState: LoadState,
    onRetry: () -> Unit
) {
    when (appendState) {
        is LoadState.Loading -> {
            LoadingIndicator(showText = false)
        }

        is LoadState.Error -> {
            Text(
                text = appendState.error.message ?: "Failed to load more",
                modifier = Modifier.clickable(onClick = onRetry),
                color = MaterialTheme.colorScheme.error
            )
        }

        else -> {}
    }
}