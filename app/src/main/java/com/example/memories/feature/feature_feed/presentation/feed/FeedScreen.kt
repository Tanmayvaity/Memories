package com.example.memories.feature.feature_feed.presentation.feed

import android.R.attr.dialogTitle
import android.R.attr.mode
import android.R.attr.onClick
import android.R.attr.text
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.FilterActionSheet
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.util.PermissionHelper
import com.example.memories.core.util.TAG
import com.example.memories.core.util.mapContentUriToType
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.OrderByType
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.domain.model.toIndex
import com.example.memories.feature.feature_feed.presentation.feed.components.ChipRow
import com.example.memories.feature.feature_feed.presentation.feed.components.CustomFloatingActionButton
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItemCard
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun FeedRoot(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>(),
    themeViewModel: ThemeViewModel = hiltViewModel<ThemeViewModel>(),
    onCameraClick: (AppScreen.Camera) -> Unit = {},
    onNavigateToImageEdit: (AppScreen.MediaEdit) -> Unit,
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit,
    onNavigateToMemoryCreate: (AppScreen.Memory) -> Unit,
    onBottomBarVisibilityToggle: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dataLoadingState by viewModel.isDataLoading.collectAsStateWithLifecycle()
    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()



    FeedScreen(
        state = state,
        onEvent = viewModel::onEvent,
        loadState = dataLoadingState,
        onCameraClick = onCameraClick,
        onNavigateToImageEdit = onNavigateToImageEdit,
        isDarkModeEnabled = isDarkModeEnabled,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
        onNavigateToMemoryCreate = onNavigateToMemoryCreate,
        onBottomBarVisibilityToggle = onBottomBarVisibilityToggle
    )

    LaunchedEffect(Unit) {
//        viewModel.onEvent(FeedEvents.FetchFeed)
    }


}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedScreen(
    state: FeedState,
    loadState: Boolean,
    onEvent: (FeedEvents) -> Unit,
    onCameraClick: (AppScreen.Camera) -> Unit = {},
    onNavigateToImageEdit: (AppScreen.MediaEdit) -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    onNavigateToMemoryCreate: (AppScreen.Memory) -> Unit = {},
    isDarkModeEnabled: Boolean = false,
    onBottomBarVisibilityToggle: (Boolean) -> Unit = {}
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var currentItem by remember { mutableStateOf<MemoryWithMediaModel?>(null) }
    var expandFab by rememberSaveable() { mutableStateOf(false) }
    var currentItemIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var selectedChipIndex by remember { mutableStateOf<Int>(0) }
    val lazyListState = rememberLazyListState()
    var currentScrollValue by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentMemoryEntryMode: MemoryEntryMode? = null

    var isScrollingUp by remember { mutableStateOf(true) } // Start with FAB visible

// This effect will update isScrollingUp based on the scroll direction
    LaunchedEffect(lazyListState) {
        var previousScrollOffset = 0
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { currentScrollOffset ->
                if (previousScrollOffset < currentScrollOffset) {
                    // Scrolling down
                    isScrollingUp = false
                } else if (previousScrollOffset > currentScrollOffset) {
                    // Scrolling up
                    isScrollingUp = true
                }
                if (isScrollingUp != currentScrollValue) {
                    currentScrollValue = isScrollingUp
                }
                previousScrollOffset = currentScrollOffset
            }

    }

    LaunchedEffect(currentScrollValue) {
        onBottomBarVisibilityToggle(currentScrollValue)
    }


    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uriList ->


        if (uriList != null && uriList.size <= 5) {

            val uriWrapperList = uriList.map { uri ->
                UriType(
                    uri = uri.toString(),
                    type = uri.mapContentUriToType(context)
                )
            }

            if (currentMemoryEntryMode != null && uriWrapperList.isNotEmpty()) {
                when (currentMemoryEntryMode) {
                    MemoryEntryMode.EditImage -> {
                        onNavigateToImageEdit(AppScreen.MediaEdit(uriWrapperList[0]))
                    }

                    MemoryEntryMode.ChooseImageAndCreate -> {
                        onNavigateToMemoryCreate(AppScreen.Memory(null, uriWrapperList))
                    }

                    else -> {}
                }
            }

        }
        if (uriList == null) {
            Log.e(TAG, "FeedScreen: Uri List is NULL")
            return@rememberLauncherForActivityResult
        }

        if (uriList.size > 5) {
            Log.e(TAG, "FeedScreen: uri list size  is greater than 5")
            return@rememberLauncherForActivityResult
        }


    }

    val currentFontSize = lerp(
        start = 24.sp,
        stop = 20.sp,
        fraction = scrollBehavior.state.collapsedFraction
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(

                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        "Your Posts",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                showDivider = false,
                showAction = true,
                actionContent = {
                    IconItem(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        color = MaterialTheme.colorScheme.primary,
                        drawableRes = R.drawable.ic_camera,
                        contentDescription = "Camera",
                        onClick = {
                            Log.d("FeedScreen", "camera clicked")
                            onCameraClick(AppScreen.Camera)
                        }
                    )
                },
                showNavigationIcon = false,
                navigationContent = {
                    IconItem(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        color = MaterialTheme.colorScheme.primary,
                        drawableRes = R.drawable.ic_filter_list,
                        contentDescription = "Camera",
                        onClick = {
                            showSheet = true
                        }
                    )
                }
            )
        },
        floatingActionButton = {
//            FloatingActionButton(
//                elevation = FloatingActionButtonDefaults.elevation(0.dp),
//                onClick = {
////                            navController.navigate(AppScreen.Camera)
//                    mediaLauncher.launch(
//                        PickVisualMediaRequest(
//                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
//                        )
//                    )
//                },
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                containerColor = MaterialTheme.colorScheme.primaryContainer
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Create Memory"
//                )
//            }


//            CustomFloatingActionButton(
//                expandable = true,
//                actionList = listOf(
//                    Triple(MemoryEntryMode.ChooseImageAndCreate, Icons.Default.AddCircle,"Choosing Media"),
//                    Triple(MemoryEntryMode.EditImage, Icons.Outlined.Edit,"Edit Media"),
//                    Triple(MemoryEntryMode.DirectCreate, Icons.Outlined.Add,"Without Media"),
//
//                ),
//                onFabClick = {mode ->
//                    currentMemoryEntryMode = mode
//                    if(mode != MemoryEntryMode.DirectCreate){
//                        mediaLauncher.launch(
//                            PickVisualMediaRequest(
//                                ActivityResultContracts.PickVisualMedia.ImageOnly
//                            )
//                        )
//                    }
//
//                }
//
//            )

            val items = listOf(
                Triple(
                    MemoryEntryMode.ChooseImageAndCreate,
                    Icons.Default.AddCircle,
                    "Choosing Media"
                ),
                Triple(MemoryEntryMode.EditImage, Icons.Outlined.Edit, "Edit Media"),
                Triple(MemoryEntryMode.DirectCreate, Icons.Outlined.Add, "Without Media"),
            )



            FloatingActionButtonMenu(
                modifier = Modifier.animateFloatingActionButton(
                    visible = isScrollingUp,
                    alignment = Alignment.TopCenter,
                ),
                expanded = expandFab,
                button = {
                    ToggleFloatingActionButton(
                        checked = expandFab,
                        onCheckedChange = {
                            expandFab = it
                        }
                    ) {
                        Icon(
                            imageVector = if (!expandFab) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Create Memory",
                            tint = if (expandFab) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
            ) {
                items.forEachIndexed { index, item ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            expandFab = false
                            currentMemoryEntryMode = item.first
                            if (currentMemoryEntryMode != MemoryEntryMode.DirectCreate) {
                                mediaLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        },
                        text = { Text(item.third) },
                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = null
                            )
                        },
                    )
                }
            }

        }
    ) { innerPadding ->
        Log.d("FeedScreen", "FeedScreen: ${state.memories.isEmpty()}")

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .background(
                    if (isDarkModeEnabled) Color.Black
                    else MaterialTheme.colorScheme.background
                ),
            state = lazyListState
        ) {

//            stickyHeader() {
//                ChipRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colorScheme.surface),
//                    selectedItemIndex = state.type.toIndex(),
//                    items = listOf<MenuItem>(
//                        MenuItem(
//                            title = "All",
//                            onClick = {
////
//                                onEvent(FeedEvents.ChangeFetchType(FetchType.ALL))
//                                onEvent(FeedEvents.FetchFeed)
//                                selectedChipIndex = 0
//                            },
//                            iconContentDescription = "",
//                            icon = -1
//                        ),
//                        MenuItem(
//                            title = "Favorite",
//                            onClick = {
//                                selectedChipIndex = 1
////                                onEvent(FeedEvents.FetchFeed(FetchType.FAVORITE))
//                                onEvent(FeedEvents.ChangeFetchType(FetchType.FAVORITE))
//                                onEvent(FeedEvents.FetchFeed)
//                            },
//                            iconContentDescription = "",
//                            icon = R.drawable.ic_favourite_filled
//                        ),
//                        MenuItem(
//                            title = "Hidden",
//                            onClick = {
//                                selectedChipIndex = 2
////                                onEvent(FeedEvents.FetchFeed(FetchType.HIDDEN))
//                                onEvent(FeedEvents.ChangeFetchType(FetchType.HIDDEN))
//                                onEvent(FeedEvents.FetchFeed)
//                            },
//                            iconContentDescription = "",
//                            icon = R.drawable.ic_hidden
//                        ),
//
//
//                        )
//                )
//            }


            itemsIndexed(
//                key = {index : Int,it : MemoryWithMediaModel -> it.memory.memoryId },
                items = state.memories,
            ) { index, it ->
                MemoryItemCard(
                    modifier = Modifier
                        .animateItem()
                        .padding(16.dp),
                    memoryItem = it,
                    onClick = {
                        onNavigateToMemoryDetail(
                            AppScreen.MemoryDetail(memoryId = it.memory.memoryId)
                        )
                        expandFab = false
                    },
                    onFavouriteButtonClick = {
                        if (it == null) {
                            Log.e("FeedScreen", "FeedScreen: item is null")
                            return@MemoryItemCard
                        }
                        onEvent(
                            FeedEvents.ToggleFavourite(
                                it.memory.memoryId,
                                !it.memory.favourite
                            )
                        )
                    },
                    onDeleteButtonClick = {
                        currentItem = it
                        Log.d("FeedScreen", "FeedScreen: ${currentItem!!.memory.toString()}")
                        showDeleteDialog = true

                    },
                    onHideButtonClick = {
                        onEvent(
                            FeedEvents.ToggleHidden(
                                it.memory.memoryId,
                                !it.memory.hidden
                            )
                        )
                    }

                )
            }
        }

        if (state.isLoading) {
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

        if (showDeleteDialog && currentItem != null) {
            GeneralAlertDialog(
                title = "Delete Memory Alert",
                text = "Are you sure you want to delete this memory",
                onDismiss = {
                    showDeleteDialog = false
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            showDeleteDialog = false
                            onEvent(
                                FeedEvents.Delete(
                                    currentItem!!.memory,
                                    currentItem!!.mediaList.map { it -> it.uri })
                            )
                        }
                    ) {
                        Text(
                            text = "Delete",
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showDeleteDialog = false
                        }

                    ) {
                        Text(
                            text = stringResource(R.string.dismiss),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }

        if (state.memories.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Memories Created"
                )
            }
        }

        if (showSheet) {
            FilterActionSheet(
                onDismiss = {
                    showSheet = false
                },
                onReset = {
                    onEvent(FeedEvents.ResetFilterState)
                },
                onApplyFilter = {
                    onEvent(FeedEvents.FetchFeed)
                    showSheet = false
                },
                state = state,
                title = "Filter & Sort Posts",
                showActionList = listOf(
                    MenuItem(
                        title = "All",
                        icon = R.drawable.ic_feed,
                        iconContentDescription = "Feed Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeFetchType(FetchType.ALL))
                        }
                    ),
                    MenuItem(
                        title = "Favorite",
                        icon = R.drawable.ic_favourite_filled,
                        iconContentDescription = "Favorite Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeFetchType(FetchType.FAVORITE))
                        }
                    ),
                    MenuItem(
                        title = "Hidden",
                        icon = R.drawable.ic_hidden,
                        iconContentDescription = "Hidden Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeFetchType(FetchType.HIDDEN))
                        }
                    )
                ),
                sortByActionList = listOf(
                    MenuItem(
                        title = "Created For Date",
                        icon = R.drawable.ic_calender,
                        iconContentDescription = "Calendar Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeSortType(SortType.CreatedForDate))
                        }
                    ),
                    MenuItem(
                        title = "Date Added",
                        icon = R.drawable.ic_timer,
                        iconContentDescription = "timer Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeSortType(SortType.DateAdded))
                        }
                    ),
                    MenuItem(
                        title = "Title",
                        icon = R.drawable.ic_title,
                        iconContentDescription = "Title Icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeSortType(SortType.Title))
                        }
                    )
                ),
                orderByActionList = listOf(
                    MenuItem(
                        title = "Ascending",
                        icon = R.drawable.ic_up,
                        iconContentDescription = "up icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeOrderByType(OrderByType.Ascending))
                        }
                    ),
                    MenuItem(
                        title = "Descending",
                        icon = R.drawable.ic_down,
                        iconContentDescription = "down icon",
                        onClick = {
                            onEvent(FeedEvents.ChangeOrderByType(OrderByType.Descending))
                        }
                    )
                )
            )
        }
    }
}


@Preview
@Composable
fun FeedScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        FeedScreen(
            state = FeedState(
                memories = List(30) { MemoryWithMediaModel() }
//                memories = emptyList()
            ),
            onEvent = {},
            loadState = false
        )
    }
}

