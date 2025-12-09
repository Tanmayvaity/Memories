package com.example.memories.feature.feature_feed.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.LocalTheme
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.ShimmerLayoutForImage
import com.example.memories.feature.feature_feed.presentation.search.components.ExploreByCategorySection
import com.example.memories.feature.feature_feed.presentation.search.components.MemoryItemForCategory
import com.example.memories.feature.feature_feed.presentation.search.components.MemorySearchBar
import com.example.memories.feature.feature_feed.presentation.search.components.RecentSearchSection
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun SearchRoot(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.inputText.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        state = state,
        searchText = searchText,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
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
) {
    val theme = LocalTheme.current
    var tagClickIndex by rememberSaveable { mutableStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val configuration = LocalConfiguration.current
    Scaffold(
        topBar = {
            MemorySearchBar(
                modifier = Modifier.padding(vertical = 10.dp)
                ,
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
                    onEvent(SearchEvents.AddSearch(memoryId))
                    onNavigateToMemoryDetail(AppScreen.MemoryDetail(memoryId))
                }

            )

        },
    ) { innerPadding ->

        if (state.recentSearch.isEmpty()) {
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
            LazyVerticalGrid(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                ,
                columns = GridCells.Fixed(
                    if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                        4
                    }else{
                        2
                    }
                ),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                if (!state.recentSearch.isEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        RecentSearchSection(
                            state = state,
                            theme = theme
                        ) { id ->
                            onEvent(SearchEvents.AddSearch(id))
                            onNavigateToMemoryDetail(
                                AppScreen.MemoryDetail(id)
                            )
                        }
                    }
                }
                if (state.tags.isNotEmpty()) {
                    stickyHeader(
                        key = "tags"
                    ) {
                        ExploreByCategorySection(
                            tags = state.tags,
                            selectedIndex = tagClickIndex,
                            onTagSelected = { index, tag ->
                                tagClickIndex = index
                                onEvent(SearchEvents.SelectTag(tag))
                            }
                        )


                    }
                }
                if (state.tags.isNotEmpty()) {
                    if(state.isMemoriesTagLoading){
                        items(6){
                            ShimmerLayoutForImage(
                                isLoading = state.isMemoriesTagLoading
                            ) { }
                        }
                    }
                    else if (state.memories.isNotEmpty()) {
                        items(state.memories) { item ->
                            MemoryItemForCategory(
                                item = item,
                                onClick = { id ->
                                    onNavigateToMemoryDetail(
                                        AppScreen.MemoryDetail(id)
                                    )
                                }
                            )

                        }

                    } else {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
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
                    MemoryWithMediaModel(), MemoryWithMediaModel(),
                    MemoryWithMediaModel(),

                    )

//                recentSearch = emptyList()

            )
        )
    }
}

