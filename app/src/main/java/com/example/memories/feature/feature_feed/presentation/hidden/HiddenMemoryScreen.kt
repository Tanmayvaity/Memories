package com.example.memories.feature.feature_feed.presentation.hidden


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.MemoryDeleteBottomSheet
import com.example.memories.core.util.hideWithCallback
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction
import com.example.memories.feature.feature_feed.presentation.components.PagedListContainer
import com.example.memories.feature.feature_feed.presentation.components.SearchTextField
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItemCard
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.flowOf

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
            SearchTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = inputText,
                onValueChange = { input ->
                    onEvent(HiddenMemoryEvents.InputTextChange(input))
                }
            )
            PagedListContainer(
                items = memories,
                lazyListState = lazyListState,
                itemKey = { index -> memories[index]?.memory?.memoryId ?: index },
                itemContentType = { "memory_item" }
            ) { memory ->
                MemoryItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    memoryItem = memory,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    elevation = 0,
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onNavigateToMemoryDetail(AppScreen.MemoryDetail(memory.memory.memoryId))
                    },
                    onHideButtonClick = {
                        onEvent(
                            HiddenMemoryEvents.Action(
                                MemoryAction.ToggleHidden(
                                    memory.memory.memoryId,
                                    memory.memory.hidden
                                )
                            )
                        )
                    },
                    onFavouriteButtonClick = {
                        onEvent(
                            HiddenMemoryEvents.Action(
                                MemoryAction.ToggleFavourite(
                                    memory.memory.memoryId,
                                    memory.memory.favourite
                                )
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

        if (showDeleteSheet && currentItem != null) {
            val hideSheet = {
                sheetState.hideWithCallback(scope) { showDeleteSheet = false }
            }

            MemoryDeleteBottomSheet(
                onDismiss = {
                    hideSheet()
                },
                onConfirm = {

                    onEvent(
                        HiddenMemoryEvents.Action(
                            MemoryAction.Delete(
                                currentItem!!.memory,
                                currentItem!!.mediaList.map { it -> it.uri })
                        )
                    )
                    hideSheet()
                },
                state = sheetState,
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