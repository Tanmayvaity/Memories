package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.R.attr.onClick
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toUri
import com.example.memories.R
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.MediaCreationType
import com.example.memories.core.presentation.components.MediaPageIndicatorLine
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.core.util.startChooser
import com.example.memories.feature.feature_feed.presentation.feed.FeedEvents
import com.example.memories.feature.feature_feed.presentation.feed_detail.components.FullScreenImageDialog
import com.example.memories.feature.feature_memory.presentation.components.TagRow
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.launch

@Composable
fun MemoryDetailRoot(
    modifier: Modifier = Modifier,
    viewmodel: MemoryDetailViewModel = hiltViewModel<MemoryDetailViewModel>(),
    onBack: () -> Unit = {},
    onNavigateToMemory: (AppScreen) -> Unit = {},
    onTagClick: (AppScreen.TagWithMemories) -> Unit,
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val isLoading by viewmodel.isLoading.collectAsStateWithLifecycle()
    val isDeleting by viewmodel.isDeleting.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewmodel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.toString(), Toast.LENGTH_SHORT).show()
                    when (event.type) {
                        UiEvent.ToastType.DELETE -> {
                            onBack()
                        }

                        else -> {}

                    }
                }

                is UiEvent.Error -> {
                    snackBarState.showSnackbar(
                        message = event.message.toString()
                    )
                }

                is UiEvent.ShowShareChooser -> {
                    context.startChooser(event.value)
                }


            }
        }
    }

    MemoryDetailScreen(
        state = state,
        onEvent = viewmodel::onEvent,
        onBack = onBack,
        onNavigateToMemory = onNavigateToMemory,
        isLoading = isLoading,
        isDeleting = isDeleting,
        onTagClick = onTagClick,
        snackBarState = snackBarState
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemoryDetailScreen(
    modifier: Modifier = Modifier,
    state: MemoryDetailState = MemoryDetailState(),
    isLoading: Boolean = false,
    isDeleting: Boolean = false,
    onEvent: (MemoryDetailEvents) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToMemory: (AppScreen) -> Unit = {},
    onTagClick: (AppScreen.TagWithMemories) -> Unit = {},
    snackBarState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val previewMode = LocalInspectionMode.current
    val pagerState =
        rememberPagerState { if (previewMode) 5 else state.memory?.mediaList?.size ?: 0 }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    var showContentSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Top,
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showImageDetail by rememberSaveable { mutableStateOf(false) }
    var expandToolBar by remember { mutableStateOf(false) }
    val memory = state.memory

    val isScrollingDown by remember {
        var previousScrollOffset = 0
        derivedStateOf {
            val currentOffset = scrollState.value
            val scrollingDown = currentOffset > previousScrollOffset
            previousScrollOffset = currentOffset
            scrollingDown
        }
    }





    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "go to the previous screen"
                        )
                    }

                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            if (memory != null && !isLoading) {
                val item = memory.memory
                if (memory.mediaList.isNotEmpty()) {
//                    MediaPager(
//                        mediaUris = null,
//                        pagerState = pagerState,
//                        readOnlyMediaUriList = memory.mediaList.map { it -> it.uri },
//                        type = MediaCreationType.SHOW
//                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.height(300.dp)
                    ) { page ->
                        val itemUri = memory.mediaList[page].uri
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    if (previewMode) R.drawable.ic_launcher_background else itemUri
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = "Media item $page",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showImageDetail = true
                                },
                            contentScale = ContentScale.FillWidth
                        )
                    }

                }


                Box(
                    modifier = Modifier
//                        .background(BlueishBlack)
                        .padding(10.dp)

                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = item.title,
                            modifier = Modifier,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = item.memoryForTimeStamp!!.formatTime(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TagRow(
                            totalTags = memory.tagsList,
                            showAdd = false,
                            onAddClick = {},
                            modifier = Modifier.padding(top = 5.dp),
                            onTagClick = { id, label ->
                                onTagClick(
                                    AppScreen.TagWithMemories(
                                        tagLabel = label,
                                        id = id
                                    )
                                )
                            }

                        )
                        Text(
                            text = item.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)


                        )
                    }

                }

            }

        }
        AnimatedVisibility(
            !isLoading && !isScrollingDown,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 10 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 10 })
        ) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                HorizontalFloatingToolbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(end = 5.dp),
                    scrollBehavior = toolbarScrollBehavior,
                    expandedShadowElevation = 16.dp,
                    expanded = true,
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                if (memory == null) return@FloatingActionButton
                                onNavigateToMemory(
                                    AppScreen.Memory(
                                        memory.memory.memoryId,
                                        emptyList()
                                    )
                                )
//                                expandToolBar = !expandToolBar
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    }
                ) {

                    if (memory == null) return@HorizontalFloatingToolbar
                    val actionsItems = listOf(
                        Triple(R.drawable.ic_delete, "Delete", { showDeleteDialog = true }),
                        Triple(
                            first = if (memory.memory.hidden) R.drawable.ic_hidden else R.drawable.ic_not_hidden,
                            second = if (memory.memory.hidden) "UnHide" else "Hide",
                            third = {
                                onEvent(
                                    MemoryDetailEvents.HiddenToggle(
                                        memory.memory.memoryId,
                                        !memory.memory.hidden
                                    )
                                )
                            }
                        ),
                        Triple(
                            first = if (memory.memory.favourite) R.drawable.ic_favourite_filled else R.drawable.ic_favourite,
                            second = if (memory.memory.favourite) "UnFavourite" else "Favourite",
                            third = {
                                onEvent(
                                    MemoryDetailEvents.FavoriteToggle(
                                        id = memory.memory.memoryId,
                                        isFavourite = memory.memory.favourite
                                    )
                                )
                            }

                        )
                    )
                    actionsItems.forEach { item ->
                        IconButton(
                            onClick = item.third
                        ) {
                            Icon(
                                painter = painterResource(item.first),
                                contentDescription = item.second
                            )
                        }
                    }


                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center

            ) {
                LoadingIndicator(
                    text = "Fetching Memory"
                )
            }
        }
    }
    if (showDeleteDialog || isDeleting) {
        GeneralAlertSheet(
            title = "Delete Memory Alert",
            content = "Are you sure you want to delete this memory",
            onDismiss = {
                showDeleteDialog = false
            },
            state = sheetState,
            onConfirm = {
                showDeleteDialog = false
                onEvent(MemoryDetailEvents.Delete)
            },
            isLoading = isDeleting
        )
    }

    if (showImageDetail && memory != null) {
        val itemUri = memory.mediaList[pagerState.currentPage].uri.toUri()
        FullScreenImageDialog(
            onDismiss = {
                showImageDetail = false
            },
            onConfirm = {
                showImageDetail = false
            },
            uriList = memory?.mediaList?.map { it.uri } ?: emptyList(),
            memoryTitle = memory.memory.title,
            memoryTime = memory.memory.memoryForTimeStamp!!,
            page = pagerState.currentPage,
            isDownloading = state.isDownloading,
            isSharing = state.isSharing,
            onDownload = {
                onEvent(
                    MemoryDetailEvents.DownloadImage(
                        uri = itemUri
                    )
                )
            },
            onShare = {
                onEvent(
                    MemoryDetailEvents.ShareImage(
                        uri = itemUri
                    )
                )
            }
        )
    }


}

@PreviewLightDark
@Preview
@Composable
fun MemoryDetailScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryDetailScreen(
            state = MemoryDetailState()
        )
    }
}
