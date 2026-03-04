package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.presentation.tags_with_memory.MemoryCard

fun LazyStaggeredGridScope.memoriesForTagSection(
    memoriesForTag: LazyPagingItems<MemoryWithMediaModel>,
    onMemoryClick: (String) -> Unit,
    onNavigateToMemoryCreate: () -> Unit = {}
) {
//    if (memoriesForTag.loadState.append == LoadState.Loading || memoriesForTag.loadState.refresh == LoadState.Loading) {
//        item(span = StaggeredGridItemSpan.FullLine) {
//            LoadingIndicator(
//                modifier = Modifier
//                    .height(300.dp)
//                    .fillMaxWidth()
//            )
//        }
//    }
     if (memoriesForTag.itemCount > 0) {
        items(
            key = { index -> memoriesForTag[index]?.memory?.memoryId ?: index },
            count = memoriesForTag.itemCount
        ) { index ->
            val item = memoriesForTag[index] ?: return@items
            MemoryCard(
                memory = item,
                onClick = { onMemoryClick(item.memory.memoryId) }
            )
        }
    } else {
        item(span = StaggeredGridItemSpan.FullLine) {
            EmptyResultPlaceHolder(
                emptyText = "No Memories have been created for this tag. Why not make one from today",
                buttonText = "Create",
                onButtonClick = onNavigateToMemoryCreate,
                height = 300.dp
            )
        }
    }
}