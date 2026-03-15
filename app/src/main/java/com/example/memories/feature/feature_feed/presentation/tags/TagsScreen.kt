package com.example.memories.feature.feature_feed.presentation.tags

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.R
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.TagDeleteBottomSheet
import com.example.memories.core.util.hideWithCallback
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.SectionStateContainer
import com.example.memories.feature.feature_feed.presentation.common.isEmpty
import com.example.memories.feature.feature_feed.presentation.components.ErrorStateCard
import com.example.memories.feature.feature_feed.presentation.components.SearchTextField
import com.example.memories.feature.feature_feed.presentation.search.components.EmptyResultPlaceHolder
import com.example.memories.feature.feature_feed.presentation.tags.components.CreateTagBottomSheet
import com.example.memories.feature.feature_feed.presentation.tags.components.SortTagsBottomSheet
import com.example.memories.feature.feature_feed.presentation.tags.components.TagCard
import com.example.memories.navigation.AppScreen

@Composable
fun TagsRoot(
    modifier: Modifier = Modifier,
    viewmodel: TagsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigateToTagWithMemory: (AppScreen.TagWithMemories) -> Unit
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val tags by viewmodel.tags.collectAsStateWithLifecycle()
    val inputText by viewmodel.inputText.collectAsStateWithLifecycle()

    TagsScreen(
        state = state,
        tagsState = tags,
        onBack = onBack,
        onEvent = viewmodel::onEvent,
        onNavigateToTagWithMemory = onNavigateToTagWithMemory,
        inputText = inputText
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    modifier: Modifier = Modifier,
    state: TagsState = TagsState(),
    tagsState: SectionState<List<TagWithMemoryCountModel>>,
    onBack: () -> Unit = {},
    onEvent: (TagEvents) -> Unit = {},
    onNavigateToTagWithMemory: (AppScreen.TagWithMemories) -> Unit = {},
    inputText: String = ""
) {
    var showSortBySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteTagSheet by remember { mutableStateOf(false) }
    var tagItem: TagWithMemoryCountModel? = null
    var showTagSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                showNavigationIcon = true,
                title = { HeadingText(title = "Your Tags") },
                onNavigationIconClick = onBack,
                showDivider = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                SearchTextField(
                    value = inputText,
                    onValueChange = { input ->
                        onEvent(TagEvents.InputTextChange(input))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Search Tags"
                )

                Button(
                    onClick = {
                        showTagSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create New Tag")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "All Tags",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                AnimatedVisibility(
                    visible = !tagsState.isEmpty()
                ) {
                    TextButton(onClick = {
                        showSortBySheet = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter_list),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Sort By Count")
                    }
                }
            }
            SectionStateContainer(
                state = tagsState,
                loadingContent = {
                    LoadingIndicator(
                        showText = true,
                        text = "Loading Tags"
                    )
                },

                emptyContent = {
                    EmptyResultPlaceHolder(
                        modifier = Modifier.fillMaxSize(),
                        showTextOnly = true,
                        emptyText = "No Tags Created"
                    )
                },
                errorContent = { error ->
                    ErrorStateCard(
                        onRetryClick = {}
                    )
                },
                successContent = { tags ->
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tags) { tag ->
                            TagCard(
                                label = tag.tagLabel,
                                memoryCount = tag.memoryCount,
                                onClick = {
                                    onNavigateToTagWithMemory(
                                        AppScreen.TagWithMemories(
                                            id = tag.tagId,
                                            tagLabel = tag.tagLabel
                                        )
                                    )
                                },
                                onLongClick = {
                                    tagItem = tag
                                    showDeleteTagSheet = true
                                }
                            )
                        }
                    }
                },
            )

        }


        if (showSortBySheet) {
            SortTagsBottomSheet(
                onDismiss = {
                    showSortBySheet = false
                },
                onApply = {
                    onEvent(TagEvents.ApplyFilter)
                    showSortBySheet = false
                },
                state = state,
                onOrderBy = { sortOrder ->
                    onEvent(TagEvents.ChangeSortOrderBy(sortOrder))
                },
                onSortBy = {
                    onEvent(TagEvents.ChangeSortBy(it))
                }
            )
        }
        if (showTagSheet) {
            CreateTagBottomSheet(
                onDismiss = { showTagSheet = false },
                isLoading = state.isTagInserting,
                onCreateTag = { name ->
                    if (name.isEmpty() || name.isBlank()) {
                        Toast.makeText(
                            context,
                            "Tag name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@CreateTagBottomSheet
                    }
                    onEvent(TagEvents.CreateTag(name))
                    showTagSheet = false
                }
            )
        }

        if (showDeleteTagSheet && tagItem != null) {
            val hideSheet = {
                sheetState.hideWithCallback(scope) { showDeleteTagSheet = false }
            }
            TagDeleteBottomSheet(
                tagLabel = tagItem!!.tagLabel,
                state = sheetState,
                onDismiss = {
                    hideSheet()
                    tagItem = null
                },
                onConfirm = {
                    onEvent(TagEvents.DeleteTag(tagItem!!.tagId))
                    hideSheet()
                }
            )
        }

    }
}


@PreviewLightDark
@Composable
fun TagsScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        TagsScreen(
            state = TagsState(
                orderByType = SortOrder.Ascending,
                sortByType = SortBy.Count,
                isTagInserting = false
            ),
//            tagsState = SectionState.Success(
//                listOf(
//                    TagWithMemoryCountModel(
//                        tagId = 1.toString(),
//                        tagLabel = "Memories",
//                        memoryCount = 23
//                    )
//                )
//            )
            tagsState = SectionState.Error()
        )
    }
}