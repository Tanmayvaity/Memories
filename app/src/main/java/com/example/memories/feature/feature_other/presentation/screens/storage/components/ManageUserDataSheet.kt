package com.example.memories.feature.feature_other.presentation.screens.storage.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.paging.compose.LazyPagingItems
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.SectionStateContainer
import com.example.memories.feature.feature_feed.presentation.components.PagedListContainer
import com.example.memories.feature.feature_feed.presentation.components.SearchTextField
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_memory.presentation.components.ImageContainer
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_other.presentation.viewmodels.StorageEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.R
import com.example.memories.feature.feature_feed.presentation.history.components.AnimatedSegmentedRow
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserDataSheet(
    onDismiss: () -> Unit,
    onNavigateToMemoryDetail: (String) -> Unit,
    memoryQuery: String,
    tagQuery: String,
    tags: SectionState<List<TagWithMemoryCountModel>>,
    recentSearches: SectionState<List<MemoryWithMediaModel>>,
    latestMemories: LazyPagingItems<MemoryWithMediaModel>,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    pagerState: PagerState = rememberPagerState(pageCount = { 2 }),
    onMediaQueryChange: (String) -> Unit = {},
    onDeleteMemories: (List<MemoryWithMediaModel>) -> Unit = {},
    onDeleteRecentSearch: (String) -> Unit = {},
    onClearAllRecentSearches: () -> Unit = {},
    onTagQueryChange: (String) -> Unit = {},
    onDeleteTags: (List<String>) -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "close sheet"
                    )
                }
                Text(
                    text = "Manage User Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            AnimatedSegmentedRow(
                selectedIndex = selectedTab,
                onSelect = {
                    selectedTab = it
                    scope.launch {
                        pagerState.animateScrollToPage(selectedTab)
                    }
                },
                options = listOf("Memories", "Searches & Tags"),
                pagerPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState
            ) { page ->

                when (page) {
                    0 -> MemoriesTab(
                        query = memoryQuery,
                        memories = latestMemories,
//                        onQueryChange = { onEvent(StorageEvents.MemoryQueryChange(it)) },
                        onQueryChange = { query ->
                            onMediaQueryChange(query)
                        },
                        onMemoryClick = { id ->
                            onNavigateToMemoryDetail(id)
                            onDismiss()
                        },
                        onDeleteMemories = { memories ->
//                            onEvent(StorageEvents.DeleteMemories(memories))
                            onDeleteMemories(memories)
                        }
                    )

                    else -> SearchesAndTagsTab(
                        tagQuery = tagQuery,
                        tags = tags,
                        recentSearches = recentSearches,
                        onTagQueryChange = { query ->
//                            onEvent(StorageEvents.TagQueryChange(it))
                            onTagQueryChange(query)
                        },
                        onDeleteTags = { ids ->
//                            onEvent(StorageEvents.DeleteTags(ids))
                            onDeleteTags(ids)
                        },
                        onDeleteRecentSearch = { id ->
//                            onEvent(StorageEvents.DeleteRecentSearch(id))
                            onDeleteRecentSearch(id)
                        },
                        onClearAllRecentSearches = {
//                            onEvent(StorageEvents.ClearAllRecentSearches)
                            onClearAllRecentSearches()
                        },
                        onRecentSearchClick = { id ->
                            onNavigateToMemoryDetail(id)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

/* ---------------------------- Memories tab ---------------------------- */

@Composable
private fun MemoriesTab(
    query: String,
    memories: LazyPagingItems<MemoryWithMediaModel>,
    onQueryChange: (String) -> Unit,
    onMemoryClick: (String) -> Unit,
    onDeleteMemories: (List<MemoryWithMediaModel>) -> Unit
) {
    val selected = remember { mutableStateMapOf<String, MemoryWithMediaModel>() }
    val selectionMode = selected.isNotEmpty()

    fun rowClick(item: MemoryWithMediaModel) {
        val id = item.memory.memoryId
        if (selectionMode) {
            if (selected.containsKey(id)) selected.remove(id) else selected[id] = item
        } else {
            onMemoryClick(id)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = selectionMode) {
            SelectionActionBar(
                count = selected.size,
                onClear = { selected.clear() },
                onDelete = {
                    onDeleteMemories(selected.values.toList())
                    selected.clear()
                }
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        SearchTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Search memories"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Single paged source: all memories when the query is blank, search results otherwise.
        PagedListContainer(
            items = memories,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemKey = { it.memory.memoryId },
            emptyContent = {
                CenterMessage(
                    if (query.isBlank()) "No memories yet" else "No memories found"
                )
            }
        ) { item ->
            SelectableMemoryRow(
                item = item,
                selected = selected.containsKey(item.memory.memoryId),
                selectionMode = selectionMode,
                onClick = { rowClick(item) },
                onLongClick = { selected[item.memory.memoryId] = item }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableMemoryRow(
    item: MemoryWithMediaModel,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val media = item.mediaList.firstOrNull()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 100.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (media?.uri != null) {
                ImageContainer(
                    modifier = Modifier.padding(5.dp),
                    uri = media.uri.toUri(),
                    size = 75,
                    isImage = media.type.isImageFile()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.memory.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.memory.content,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = (item.memory.memoryForTimeStamp ?: 0L).formatTime(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (selectionMode) {
                Spacer(modifier = Modifier.width(8.dp))
                SelectionMarker(selected = selected)
            }
        }
    }
}

@Composable
private fun SelectionMarker(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(percent = 50)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/* ----------------------- Searches & Tags tab ----------------------- */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchesAndTagsTab(
    tagQuery: String,
    tags: SectionState<List<TagWithMemoryCountModel>>,
    recentSearches: SectionState<List<MemoryWithMediaModel>>,
    onTagQueryChange: (String) -> Unit,
    onDeleteTags: (List<String>) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onRecentSearchClick: (String) -> Unit
) {
    val selectedTags = remember { mutableStateMapOf<String, Boolean>() }
    val selectionMode = selectedTags.isNotEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AnimatedVisibility(visible = selectionMode) {
                SelectionActionBar(
                    count = selectedTags.size,
                    onClear = { selectedTags.clear() },
                    onDelete = {
                        onDeleteTags(selectedTags.keys.toList())
                        selectedTags.clear()
                    }
                )
            }
        }

        item {
            SearchTextField(
                value = tagQuery,
                onValueChange = onTagQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Search tags"
            )
        }

        item {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            SectionStateContainer(
                state = tags,
                loadingContent = {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        showText = false
                    )
                },
                emptyContent = { CenterMessage("No tags found", height = 120.dp) },
                errorContent = { CenterMessage("Something went wrong", height = 120.dp) },
                successContent = { tagList ->
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tagList.forEach { tag ->
                            val isSelected = selectedTags.containsKey(tag.tagId)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedTags.remove(tag.tagId)
                                    else selectedTags[tag.tagId] = true
                                },
                                label = { Text("${tag.tagLabel} (${tag.memoryCount})") },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                val showClearAll =
                    recentSearches is SectionState.Success && recentSearches.data.isNotEmpty()
                AnimatedVisibility(visible = showClearAll) {
                    TextButton(onClick = onClearAllRecentSearches) {
                        Text("Clear All")
                    }
                }
            }
        }

        when (val rs = recentSearches) {
            is SectionState.Success -> {
                items(rs.data, key = { it.memory.memoryId }) { item ->
                    MemoryItem(
                        modifier = Modifier.animateItem(),
                        type = item.mediaList.firstOrNull()?.type,
                        title = item.memory.title,
                        content = item.memory.content,
                        imageUri = item.mediaList.firstOrNull()?.uri,
                        memoryForTimeStamp = item.memory.memoryForTimeStamp ?: 0L,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { onRecentSearchClick(item.memory.memoryId) },
                        onIconClick = { onDeleteRecentSearch(item.memory.memoryId) }
                    )
                }
            }

            is SectionState.Loading -> item {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    showText = false
                )
            }

            else -> item { CenterMessage("No recent searches", height = 120.dp) }
        }
    }
}

/* ----------------------------- shared ----------------------------- */

@Composable
private fun SelectionActionBar(
    count: Int,
    onClear: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClear) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear selection")
        }
        Text(
            text = "$count selected",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete selected",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CenterMessage(text: String, height: Dp = 200.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@PreviewLightDark
@Composable
private fun ManageUserDataSheetPreview() {
    MemoriesTheme {
        val previewMemories = List(30) { MemoryWithMediaModel() }
        ManageUserDataSheet(
            onDismiss = {},
            onNavigateToMemoryDetail = {},
            memoryQuery = "",
            tagQuery = "",
            tags = SectionState.Success(
                listOf(
                    TagWithMemoryCountModel(
                        tagId = 1.toString(),
                        tagLabel = "Memories",
                        memoryCount = 23
                    )
                )
            ),
            recentSearches = SectionState.Success(previewMemories),
            latestMemories = flowOf(PagingData.from(previewMemories)).collectAsLazyPagingItems(),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            pagerState = rememberPagerState(pageCount = { 2 }),
        )
    }

}
