package com.example.memories.feature.feature_feed.presentation.search

import android.widget.ProgressBar
import android.widget.SearchView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun SearchRoot(
    modifier: Modifier = Modifier,
    viewModel : SearchViewModel = hiltViewModel(),
    onNavigateToMemoryDetail : (AppScreen.MemoryDetail) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.inputText.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        state = state,
        searchText = searchText,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state : SearchState = SearchState(),
    searchText : String = "",
    onEvent : (SearchEvents) -> Unit = {},
    onNavigateToMemoryDetail : (AppScreen.MemoryDetail) -> Unit = {}
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize()
    ){
        SearchBar(
            tonalElevation = 10.dp,
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = { it: String ->
                        text = it
                        onEvent(SearchEvents.InputTextChange(it))
                    },
                    onSearch = {},
                    expanded = expanded,
                    onExpandedChange = { it: Boolean ->
                        expanded = it
                    },
                    placeholder = {
                        Text("Search")
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                   onEvent(SearchEvents.ClearInput)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "",
                                )
                            }
                        }
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                if(expanded){
                                    expanded = false
                                }

                            }
                        ) {
                            if(expanded){
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "arrow back icon",
                                )
                            }else{
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search icon",
                                )
                            }

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            expanded = expanded,
            onExpandedChange = { it: Boolean ->
                expanded = it
            },
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                if(state.isLoading){
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    items(state.data) { item ->
                        MemoryItem(
                            memoryItem = item,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            onNavigateToMemoryDetail(AppScreen.MemoryDetail(item.memory.memoryId))
                        }
                    }
                }
            }
        }
    }


}
@Preview
@Composable
fun SearchScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        SearchScreen(

        )
    }
}

