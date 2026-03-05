package com.example.memories.feature.feature_feed.presentation.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.LocalTheme
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.search.components.ExploreByCategorySection
import com.example.memories.feature.feature_feed.presentation.search.components.MemorySearchBar
import com.example.memories.feature.feature_feed.presentation.search.components.RecentSearchSection
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.feature.feature_feed.presentation.search.components.OnThisDaySection
import com.example.memories.feature.feature_feed.presentation.search.components.RecentMemoriesSection
import com.example.memories.feature.feature_feed.presentation.search.components.memoriesForTagSection
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@Composable
fun SearchRoot(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel<SearchViewModel>(),
    onNavigate: (AppScreen) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val memoriesForTag = viewModel.memoriesForTag.collectAsLazyPagingItems()
    val searchText by viewModel.inputText.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        state = state,
        searchText = searchText,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigate,
        onNavigateToMemoryCreate = onNavigate,
        onNavigateToTagCreate = onNavigate,
        memoriesForTag = memoriesForTag
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState(),
    searchText: String = "",
    onEvent: (SearchEvents) -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    onNavigateToMemoryCreate: (AppScreen.Memory) -> Unit = {},
    onNavigateToTagCreate: (AppScreen.Tags) -> Unit = {},
    memoriesForTag: LazyPagingItems<MemoryWithMediaModel>
) {
    val theme = LocalTheme.current
    var tagClickIndex by rememberSaveable { mutableStateOf(0) }
    val screenWidth = LocalWindowInfo.current.containerDpSize.width
//    val allMemories = state.onThisDay.flatMap { it.memories }
    val carouselState = rememberCarouselState() { state.onThisDay.size }
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyStaggeredGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val focusRequester = remember { FocusRequester() }
    val tagListState = rememberLazyListState()

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

    LaunchedEffect(state.selectedTag) {
        if(state.tags is SectionState.Success && state.tags.data.isNotEmpty() && state.selectedTag != null){
            Log.d("SearchScreen", "SearchScreen: ${state.selectedTag} ")
            tagListState.animateScrollToItem(state.tags.data.indexOf(state.selectedTag))
        }
    }



    Scaffold(
        topBar = {
            MemorySearchBar(
                modifier = Modifier.focusRequester(focusRequester),
                searchText = searchText,
                onQueryChange = { it ->
                    onEvent(SearchEvents.InputTextChange(it))
                },
                onClearInput = {
                    onEvent(SearchEvents.ClearInput)
                },
                onCrossClick = { id ->
                    onEvent(SearchEvents.DeleteSearch(id))
                },
                state = state,
                onItemClick = { memoryId ->
                    Log.d("SearchScreen", "SearchScreen: clicked")
                    onEvent(SearchEvents.AddSearch(memoryId))
                    onNavigateToMemoryDetail(AppScreen.MemoryDetail(memoryId))
                }

            )

        },
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            state = lazyGridState,
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                RecentSearchSection(
                    onRecentSearchItemClick = { id ->
                        onEvent(SearchEvents.AddSearch(id))
                        onNavigateToMemoryDetail(
                            AppScreen.MemoryDetail(id)
                        )
                    },
                    onExploreClick = {
                        focusRequester.requestFocus()
                    },
                    onClearAllClick = {
                        onEvent(SearchEvents.DeleteAllSearch)
                    },
                    onDeleteSearchClick = { id ->
                        onEvent(SearchEvents.DeleteSearch(id))
                    },
                    searchState = state.recentSearches,
                )
            }

            if (state.onThisDay.isNotEmpty()) {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    OnThisDaySection(
                        onThisDayMemories = state.onThisDay,
                        carouselState = carouselState,
                        screenWidth = screenWidth,
                        onMemoryClick = { id ->
                            onNavigateToMemoryDetail(AppScreen.MemoryDetail(id))
                        }
                    )
                }
            }
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                RecentMemoriesSection(
                    recentMemoriesState = state.recentMemories,
                    rainbowColorsBrush = rainbowColorsBrush,
                    onItemClick = { id ->
                        onNavigateToMemoryDetail(AppScreen.MemoryDetail(id))
                    },
                    onNavigateToMemoryCreate = {
                        onNavigateToMemoryCreate(AppScreen.Memory())
                    }
                )
            }
            item(
                key = "tags",
                span = StaggeredGridItemSpan.FullLine
            ) {
                ExploreByCategorySection(
                    tagsState = state.tags,
                    selectedIndex = tagClickIndex,
                    lazyListState = tagListState,
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
                    onCreateTagClick = {
                        onNavigateToTagCreate(AppScreen.Tags)
                    }
                )
            }
            if (state.tags is SectionState.Success && state.tags.data.isNotEmpty()) {
                memoriesForTagSection(
                    memoriesForTag = memoriesForTag,
                    onMemoryClick = { id ->
                        onNavigateToMemoryDetail(AppScreen.MemoryDetail(id))
                    },
                    onNavigateToMemoryCreate = {
                        onNavigateToMemoryCreate(AppScreen.Memory())
                    }
                )
            }
        }


    }


}

@Preview
@Composable
fun SearchScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        val previewMemories = List(30) { MemoryWithMediaModel() }
        SearchScreen(
            state = SearchState(
//                recentSearches = SectionState.Success(previewMemories),
//                tags = SectionState.Success(
//                    listOf(
//                        TagModel(label = "Anime"),
//                        TagModel(label = "Another")
//                    )
//                ),
//                recentMemories = SectionState.Success(previewMemories),
//                recentSearch = emptyList()

                recentSearches = SectionState.Empty,
                tags = SectionState.Empty,
                recentMemories = SectionState.Empty

            ),
            memoriesForTag = flowOf(PagingData.from(previewMemories)).collectAsLazyPagingItems()
        )
    }
}

