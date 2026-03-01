package com.example.memories.feature.feature_feed.presentation.hidden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.feature.feature_feed.presentation.feed.FeedEvents
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItemCard
import com.example.memories.feature.feature_feed.presentation.feed.components.OnThisDayCard
import com.example.memories.feature.feature_feed.presentation.tags.TagEvents
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun HiddenMemoryRoot(
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    onBack: () -> Unit,
    viewModel: HiddenMemoryViewModel = hiltViewModel()
) {
    val memories = viewModel.memories.collectAsLazyPagingItems()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    HiddenMemoryScreen(
        memories = memories,
        inputText = inputText,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenMemoryScreen(
    modifier: Modifier = Modifier,
    inputText: String = "",
    memories: LazyPagingItems<MemoryWithMediaModel>,
    onEvent: (HiddenMemoryEvents) -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    onBack: () -> Unit = {}
) {

    val focusManager = LocalFocusManager.current
    var showDeleteSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentItem by remember { mutableStateOf<MemoryWithMediaModel?>(null) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = "Hidden Memory Screen",
                        fontWeight = FontWeight.SemiBold,

                        )
                },
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = {
                    onBack()
                }

            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = inputText,
                onValueChange = { it ->
                    onEvent(HiddenMemoryEvents.InputTextChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = "Search. . .",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(inputText.isNotEmpty()) {
                        IconItem(
                            imageVector = Icons.Default.Close,
                            onClick = {
                                onEvent(HiddenMemoryEvents.InputTextChange(""))
                            },
                            contentDescription = "remove text",
                            alpha = 0f,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus(force = true)
                    }
                ),
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )


//            when (memories.loadState.append) {
            if (memories.loadState.refresh is LoadState.Loading && memories.itemCount == 0) {
                Box(
                    modifier = Modifier
                        //.padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.memories.core.presentation.components.LoadingIndicator()
                }
            } else if (memories.loadState.refresh is LoadState.Error && memories.itemCount == 0) {
                val error = (memories.loadState.refresh as LoadState.Error).error
                Box(
                    modifier = Modifier
                        //                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unknown Error occurred while fetching memories"
                    )
                }
            }
            // Success
            else {

                if (memories.itemCount <= 0) {
                    Box(
                        modifier = Modifier
                            //                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No memories found",
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            //                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        state = lazyListState
                    ) {
                        items(
                            count = memories.itemCount,
                            key = { index -> memories[index]?.memory?.memoryId ?: index },
                            contentType = { "memory_item" }
                        ) { index ->
                            memories[index]?.let { memory ->
                                MemoryItemCard(
                                    modifier = Modifier.animateItem(),
                                    memoryItem = memory,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                    elevation = 0,
                                    shape = RoundedCornerShape(16.dp),
                                    onClick = {
                                        onNavigateToMemoryDetail(
                                            AppScreen.MemoryDetail(memory.memory.memoryId)
                                        )
                                    },
                                    onHideButtonClick = {
                                        onEvent(
                                            HiddenMemoryEvents.ToggleHidden(
                                                memory.memory.memoryId,
                                                memory.memory.hidden
                                            )
                                        )
                                    },
                                    onFavouriteButtonClick = {
                                        onEvent(
                                            HiddenMemoryEvents.ToggleFavourite(
                                                memory.memory.memoryId,
                                                memory.memory.favourite
                                            )
                                        )
                                    },
                                    onDeleteButtonClick = {
                                        currentItem = memory
                                        showDeleteSheet = true
                                    }

                                )
                            }
                        }
                        // Append (next page) loading/error
                        item {
                            when (memories.loadState.append) {
                                is LoadState.Loading -> {
                                    com.example.memories.core.presentation.components.LoadingIndicator(
                                        showText = false
                                    )
                                }

                                is LoadState.Error -> {
                                    val error =
                                        (memories.loadState.append as LoadState.Error).error
                                    Text(
                                        text = error.message ?: "Failed to load more",
                                        modifier = Modifier.clickable {
                                            memories.retry()
                                        }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
//            }
        }

        if (showDeleteSheet && currentItem != null) {
            GeneralAlertSheet(
                title = "Delete Memory Alert",
                content = "Are you sure you want to delete this memory",
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        showDeleteSheet = false
                    }

                },
                state = sheetState,
                onConfirm = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        showDeleteSheet = false
                    }
                    onEvent(HiddenMemoryEvents.Delete(currentItem!!))
                },
                isLoading = false
            )
        }
    }
}


@Preview
@Composable
private fun HiddenMemoryScreenPreview() {
    val previewMemories = List(30) { MemoryWithMediaModel() }
    MemoriesTheme {
        HiddenMemoryScreen(
            memories = flowOf(PagingData.from(previewMemories)).collectAsLazyPagingItems()
        )
    }
}