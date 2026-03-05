package com.example.memories.feature.feature_feed.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.memories.core.presentation.components.LoadingIndicator

@Composable
fun <T : Any> PagedListContainer(
    items: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    emptyContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No memories found")
        }
    },
    errorContent: @Composable (Throwable) -> Unit = {
        ErrorStateCard(
            reasonText = "Unknown error occurred while fetching memories"
        ) { }
    },
    itemKey: ((index: Int) -> Any)? = null,
    itemContentType: ((index: Int) -> Any?)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    val refresh = items.loadState.refresh

    when {
        refresh is LoadState.Loading && items.itemCount == 0 -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(showText = true)
            }
        }

        refresh is LoadState.Error && items.itemCount == 0 -> {
            errorContent(refresh.error)
        }

        items.itemCount == 0 -> {
            emptyContent()
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                state = lazyListState,
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = verticalArrangement,
            ) {
                items(
                    count = items.itemCount,
                    key = itemKey,
                    contentType = itemContentType ?: { null }
                ) { index ->
                    items[index]?.let { item -> itemContent(item) }
                }

                item {
                    AppendStateFooter(
                        appendState = items.loadState.append,
                        onRetry = { items.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppendStateFooter(
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