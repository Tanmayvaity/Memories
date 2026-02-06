package com.example.memories.feature.feature_feed.presentation.tags_with_memory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItemCard
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun TagWithMemoryRoot(
    modifier: Modifier = Modifier,
    viewmodel : TagWithMemoryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToMemory : (AppScreen.MemoryDetail) -> Unit
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val memories =  viewmodel.memoriesForTag.collectAsLazyPagingItems()
    TagWithMemoryScreen(
        state = state,
        onEvent = viewmodel::onEvent,
        onBack = onBack,
        onNavigateToMemory = onNavigateToMemory,
        memories = memories
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagWithMemoryScreen(
    modifier: Modifier = Modifier,
    state : TagWithMemoryState = TagWithMemoryState(),
    onEvent : (TagWithMemoryEvents) -> Unit = {},
    onBack : () -> Unit = {},
    onNavigateToMemory: (AppScreen.MemoryDetail) -> Unit = {},
    memories : LazyPagingItems<MemoryWithMediaModel>
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = state.label ?: "Tag"
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack
            )
        }
    ) { innerPadding ->

        if(memories.loadState.append == LoadState.Loading){
            LoadingIndicator(
                showText = true
            )
        }

        if(memories.loadState.append != LoadState.Loading && memories.itemCount <=0){
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "No Memory for this tag",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            if (memories.loadState.append != LoadState.Loading && memories.itemCount > 0) {
                items(count = memories.itemCount) { index ->
                    val item = memories[index] ?: return@items
                    MemoryCard(
                        memory = item,
                        onClick = {
                            onNavigateToMemory(
                                AppScreen.MemoryDetail(
                                    memoryId = item.memory.memoryId
                                )
                            )
                        }
                    )
                }
            }
        }


    }
}

@PreviewLightDark
@Composable
fun TagWithMemoryScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        val previewMemories = List(30) { MemoryWithMediaModel() }
        TagWithMemoryScreen(
            state = TagWithMemoryState(
                memories = listOf(
                    MemoryWithMediaModel(
                        memory = MemoryModel(
                            title = "Party Time",
                            content = "Some Content"
                        )
                    ),
                )
            ),
            memories = flowOf(PagingData.from(previewMemories)).collectAsLazyPagingItems()

        )
    }
}