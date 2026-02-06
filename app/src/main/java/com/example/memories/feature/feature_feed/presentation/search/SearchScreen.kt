package com.example.memories.feature.feature_feed.presentation.search

import android.R.attr.top
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.memories.LocalTheme
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.ShimmerLayoutForImage
import com.example.memories.feature.feature_feed.presentation.search.components.ExploreByCategorySection
import com.example.memories.feature.feature_feed.presentation.search.components.MemoryItemForCategory
import com.example.memories.feature.feature_feed.presentation.search.components.MemorySearchBar
import com.example.memories.feature.feature_feed.presentation.search.components.RecentSearchSection
import com.example.memories.feature.feature_feed.presentation.tags_with_memory.MemoryCard
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import org.jetbrains.annotations.Async
import com.example.memories.R
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.core.util.isImageFile
import com.example.memories.feature.feature_feed.presentation.feed.components.OnThisDayCard
import com.google.common.collect.Multimaps.index
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchRoot(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val memoriesForTag = viewModel.memoriesForTag.collectAsLazyPagingItems()
    val searchText by viewModel.inputText.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        state = state,
        searchText = searchText,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
        memoriesForTag = memoriesForTag
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState(),
    searchText: String = "",
    onEvent: (SearchEvents) -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    memoriesForTag: LazyPagingItems<MemoryWithMediaModel>
) {
    val theme = LocalTheme.current
    var tagClickIndex by rememberSaveable { mutableStateOf(0) }
    val screenWidth = LocalWindowInfo.current.containerDpSize.width
    val allMemories = state.onThisDateMemories.flatMap { it.memories }
    val carouselState = rememberCarouselState() { allMemories.size }
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyStaggeredGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }



    Scaffold(
        topBar = {
            MemorySearchBar(
                modifier = Modifier.padding(vertical = 10.dp),
                searchText = searchText,
                onQueryChange = { it ->
                    onEvent(SearchEvents.InputTextChange(it))
                },
                onClearInput = {
                    onEvent(SearchEvents.ClearInput)
                },
                onNavigateToMemoryDetail = {},
                state = state,
                onItemClick = { memoryId ->
                    Log.d("SearchScreen", "SearchScreen: clicked")
                    onEvent(SearchEvents.AddSearch(memoryId))
                    onNavigateToMemoryDetail(AppScreen.MemoryDetail(memoryId))
                }

            )

        },
    ) { innerPadding ->

        if (state.recentSearch.isEmpty() && state.tags.isEmpty() && state.recentMemories.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,

                ) {
                Text(
                    text = "No Memories have been created. Why not make one from today",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                state = lazyGridState ,
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                if (!state.recentSearch.isEmpty()) {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        RecentSearchSection(
                            state = state,
                            theme = theme,
                            onRecentSearchItemClick = { id ->
                                onEvent(SearchEvents.AddSearch(id))
                                onNavigateToMemoryDetail(
                                    AppScreen.MemoryDetail(id)
                                )
                            },
                            onClearAllClick = {
                                onEvent(SearchEvents.DeleteAllSearch)
                            },
                            onDeleteSearchClick = { id ->
                                onEvent(SearchEvents.DeleteSearch(id))
                            }
                        )
                    }
                }

                if (state.onThisDateMemories.isNotEmpty()) {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 5.dp)
                            ) {
                                Text(
                                    text = "On This Day",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f))
                                        .padding(8.dp)
                                ){
                                    Text(
                                        text = "${carouselState.currentItem + 1}/${allMemories.size}",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }



                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalMultiBrowseCarousel(
                                state = carouselState,
                                preferredItemWidth = screenWidth,
                                itemSpacing = 5.dp,
                                modifier = Modifier.height(250.dp)
                            ) { index ->
                                val memory = allMemories[index]
                                val uri = memory.mediaList.firstOrNull()?.uri
                                if (uri != null && isImageFile(uri)) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    onNavigateToMemoryDetail(
                                                        AppScreen.MemoryDetail(
                                                            memory.memory.memoryId
                                                        )
                                                    )
                                                },
                                            contentScale = ContentScale.Crop,
                                            placeholder = painterResource(R.drawable.ic_launcher_background),
                                            error = painterResource(R.drawable.ic_launcher_background),
                                            contentDescription = "",
                                            model = memory.mediaList.firstOrNull()?.uri
                                        )
                                        Text(
                                            text = memory.memory.memoryForTimeStamp!!.formatTime("dd MMM yyyy"),
                                            color = Color.White,
                                            style = TextStyle(
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(2f, 2f),
                                                    blurRadius = 4f,
                                                ),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 32.sp
                                            ),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(16.dp)
                                        )
                                    }
                                } else {
                                    OnThisDayCard(
                                        time = memory.memory.memoryForTimeStamp!!,
                                        title = memory.memory.title,
                                        onClick = {
                                            onNavigateToMemoryDetail(
                                                AppScreen.MemoryDetail(
                                                    memory.memory.memoryId
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.recentMemories.isNotEmpty()) {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Recent Memoires",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(state.recentMemories) { index, item ->
                                    val isMediaValid = item.mediaList.isNotEmpty()
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .border(
                                                BorderStroke(
                                                    width = 3.dp,
                                                    brush = rainbowColorsBrush,
                                                ),
                                                CircleShape
                                            )
                                            .clickable {
                                                onNavigateToMemoryDetail(
                                                    AppScreen.MemoryDetail(
                                                        item.memory.memoryId
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isMediaValid) {
                                            AsyncImage(
                                                model = item.mediaList[0].uri,
                                                contentDescription = "recent memories image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        BorderStroke(
                                                            width = 3.dp,
                                                            brush = rainbowColorsBrush,
                                                        ),
                                                        CircleShape
                                                    )
                                            )
                                        } else {
                                            Text(
                                                text = item.memory.timeStamp.formatTime(format = "MMM dd"),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }


                if (state.tags.isNotEmpty()) {
                    item(
                        key = "tags",
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        ExploreByCategorySection(
                            tags = state.tags,
                            selectedIndex = tagClickIndex,
                            onTagSelected = { index, tag ->
                                tagClickIndex = index
                                onEvent(SearchEvents.SelectTag(tag))
                                scope.launch {
                                    lazyGridState.animateScrollToItem(
                                        index = lazyGridState.layoutInfo.visibleItemsInfo
                                            .firstOrNull { it.key == "tags" }?.index
                                            ?: 0
                                    )
                                }
                            },
                        )


                    }
                }
                if (state.tags.isNotEmpty()) {
                    if (memoriesForTag.loadState.append == LoadState.Loading) {
                        items(6) {
//                            ShimmerLayoutForImage(
//                                isLoading = state.isMemoriesTagLoading
//                            ) { }

                            LoadingIndicator()
                        }
                    } else if (memoriesForTag.itemCount > 0) {
                        items(
                            key = { index -> memoriesForTag[index]?.memory?.memoryId ?: index },
                            count = memoriesForTag.itemCount
                        ) { index ->
//                            MemoryItemForCategory(
//                                item = item,
//                                onClick = { id ->
//                                    onNavigateToMemoryDetail(
//                                        AppScreen.MemoryDetail(id)
//                                    )
//                                }
//                            )
                            val item = memoriesForTag[index]
                            if (item == null) return@items
                            MemoryCard(
                                memory = item,
                                onClick = {
                                    onNavigateToMemoryDetail(
                                        AppScreen.MemoryDetail(
                                            memoryId = item.memory.memoryId
                                        )
                                    )
                                }
                            )

                        }

                    } else {
                        item(
                            span = StaggeredGridItemSpan.FullLine
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No Memories have been created for this tag. Why not make one from today",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                }


            }
        }


    }


}

@PreviewDynamicColors
@PreviewLightDark
@Preview
@Composable
fun SearchScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        val previewMemories = List(30) { MemoryWithMediaModel() }
        SearchScreen(
            state = SearchState(
                recentSearch = listOf(
                    MemoryWithMediaModel(
                        memory = MemoryModel(
                            title = "Hello",
                            content = "Hello Again "
                        )
                    )
                ),
                tags = listOf(
                    TagModel(label = "Anime"),
                    TagModel(label = "Another")
                ),
                memories = listOf(
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                    MemoryWithMediaModel(),
                )

//                recentSearch = emptyList()

            ),
            memoriesForTag = flowOf(PagingData.from(previewMemories)).collectAsLazyPagingItems()
        )
    }
}

