package com.example.memories.feature.feature_feed.presentation.feed

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.ContentActionSheet
import com.example.memories.core.util.PermissionHelper
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun FeedRoot(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>()

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dataLoadingState by viewModel.isDataLoading.collectAsStateWithLifecycle()




    FeedScreen(
        state = state,
        onEvent = viewModel::onEvent,
        loadState = dataLoadingState
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(FeedEvents.FetchFeed)
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    loadState : Boolean,
    onEvent: (FeedEvents) -> Unit
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var currentItem by rememberSaveable { mutableStateOf<MemoryWithMediaModel?>(null) }
    var currentItemIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Your Posts",
                showDivider = true
            )
        }
    ) { innerPadding ->
        Log.d("FeedScreen", "FeedScreen: ${state.memories.isEmpty()}")

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
        ){


            itemsIndexed(state.memories) { index, it ->
                MemoryItem(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        fadeOutSpec = tween(500),
                    ),
                    memoryItem = it,
                    onClick = {},
                    onOverflowButtonClick = {
                        showSheet = true
                        currentItem = it
                        currentItemIndex = index
                    },
                    onFavouriteButtonClick = {
                        if (it == null) {
                            Log.e("FeedScreen", "FeedScreen: item is null")
                            return@MemoryItem
                        }
                        onEvent(FeedEvents.ToggleFavourite(it.memory.memoryId))
                    }

                )
            }
        }



        if (showSheet && currentItem != null && currentItemIndex != null) {
            val item = state.memories[currentItemIndex!!].memory
            ContentActionSheet(
                onDismiss = {
                    showSheet = false
                },
                sheetState = sheetState,

                title = currentItem!!.memory.title,
                actionList =
                    listOf(
                        MenuItem(
                            title = "Like",
                            icon = if (item.favourite) R.drawable.ic_favourite_filled else R.drawable.ic_favourite,
                            iconContentDescription = "Like",
                            onClick = {
                                if (currentItem == null) {
                                    Log.e("FeedScreen", "FeedScreen: currentItem is null")
                                    return@MenuItem
                                }
                                onEvent(FeedEvents.ToggleFavourite(currentItem!!.memory.memoryId))
                                showSheet = false
                            }
                        ),
                        MenuItem(
                            title = "Hide",
                            icon = if (item.hidden) R.drawable.ic_not_hidden else R.drawable.ic_hidden,
                            iconContentDescription = "Hide",
                            onClick = {
                                if (currentItem == null) {
                                    Log.e("FeedScreen", "FeedScreen: currentItem is null")
                                    return@MenuItem
                                }
                                onEvent(FeedEvents.ToggleHidden(currentItem!!.memory.memoryId))
                                showSheet = false
                            }
                        ),
                        MenuItem(
                            title = "Delete",
                            icon = R.drawable.ic_delete,
                            iconContentDescription = "Delete icon",
                            onClick = {}


                        )
                    )
            )
        }

        if(state.memories.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "No Memories Created"
                )
            }
        }
    }
}


@Preview
@Composable
fun FeedScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        FeedScreen(
            state = FeedState(
//                memories = List(30) { MemoryWithMediaModel() }
                memories = emptyList()
            ),
            onEvent = {},
            loadState = false
        )
    }
}

