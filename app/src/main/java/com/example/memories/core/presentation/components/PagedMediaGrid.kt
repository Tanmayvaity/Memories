package com.example.memories.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

@Composable
fun <T : Any> PagedMediaGrid(
    items: LazyPagingItems<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    columns: Int = 3,
    emptyText: String = "No media found",
    itemContent: @Composable (T) -> Unit
) {
    val refresh = items.loadState.refresh

    when {
        refresh is LoadState.Loading && items.itemCount == 0 -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator(showText = true)
            }
        }

        refresh is LoadState.Error && items.itemCount == 0 -> {
            ErrorStateCard(reasonText = refresh.error.message.toString()) {
                items.retry()
            }
        }

        items.itemCount == 0 -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = emptyText)
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                state = gridState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey { key(it) }
                ) { index ->
                    items[index]?.let { item ->
                        itemContent(item)
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AppendStateFooter(
                        appendState = items.loadState.append,
                        onRetry = { items.retry() }
                    )
                }
            }
        }
    }
}