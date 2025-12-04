package com.example.memories.feature.feature_feed.presentation.feed

import android.R.attr.dialogTitle
import android.R.attr.onClick
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.memories.core.presentation.components.ContentActionSheet
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.PermissionHelper
import com.example.memories.core.util.TAG
import com.example.memories.core.util.mapContentUriToType
import com.example.memories.feature.feature_feed.domain.model.FetchType
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
    onNavigateToMemoryCreate : (AppScreen.Memory) -> Unit,

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
        onNavigateToMemoryCreate = onNavigateToMemoryCreate
    )

    LaunchedEffect(Unit) {
//        viewModel.onEvent(FeedEvents.FetchFeed)
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    loadState: Boolean,
    onEvent: (FeedEvents) -> Unit,
    onCameraClick: (AppScreen.Camera) -> Unit = {},
    onNavigateToImageEdit: (AppScreen.MediaEdit) -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen.MemoryDetail) -> Unit = {},
    onNavigateToMemoryCreate : (AppScreen.Memory) -> Unit = {},
    isDarkModeEnabled: Boolean = false
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var currentItem by remember { mutableStateOf<MemoryWithMediaModel?>(null) }
    var currentItemIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedChipIndex by remember { mutableStateOf<Int>(0) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentMemoryEntryMode : MemoryEntryMode? = null


    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uriList ->

        if (uriList != null) {

            val uriWrapperList = uriList.map { uri ->
                UriType(
                    uri = uri.toString(),
                    type = uri.mapContentUriToType(context)
                )
            }

            if(currentMemoryEntryMode!=null && uriWrapperList.isNotEmpty()){
                when(currentMemoryEntryMode){
                    MemoryEntryMode.EditImage -> {
                        onNavigateToImageEdit(AppScreen.MediaEdit(uriWrapperList[0]))
                    }

                    MemoryEntryMode.ChooseImageAndCreate -> {
                        onNavigateToMemoryCreate(AppScreen.Memory(uriWrapperList))
                    }

                    else -> {}
                }
            }

        }

    }

    val currentFontSize = lerp(
        start = 24.sp,
        stop = 20.sp,
        fraction = scrollBehavior.state.collapsedFraction
    )

    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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

            CustomFloatingActionButton(
                expandable = true,
                actionList = listOf(
                    Triple(MemoryEntryMode.ChooseImageAndCreate, Icons.Default.AddCircle,"Choosing Media"),
                    Triple(MemoryEntryMode.EditImage, Icons.Outlined.Edit,"Edit Media"),
                    Triple(MemoryEntryMode.DirectCreate, Icons.Outlined.Add,"Without Media"),

                ),
                onFabClick = {mode ->
                    currentMemoryEntryMode = mode
                    if(mode != MemoryEntryMode.DirectCreate){
                        mediaLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }

                }

            )
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
        ) {

            item {
                ChipRow(
                    modifier = Modifier.padding(10.dp),
                    selectedItemIndex = state.type.toIndex(),
                    items = listOf<MenuItem>(
                        MenuItem(
                            title = "All",
                            onClick = {
//
                                onEvent(FeedEvents.ChangeFetchType(FetchType.ALL))
                                onEvent(FeedEvents.FetchFeed)
                                selectedChipIndex = 0
                            },
                            iconContentDescription = "",
                            icon = -1
                        ),
                        MenuItem(
                            title = "Favorite",
                            onClick = {
                                selectedChipIndex = 1
//                                onEvent(FeedEvents.FetchFeed(FetchType.FAVORITE))
                                onEvent(FeedEvents.ChangeFetchType(FetchType.FAVORITE))
                                onEvent(FeedEvents.FetchFeed)
                            },
                            iconContentDescription = "",
                            icon = R.drawable.ic_favourite_filled
                        ),
                        MenuItem(
                            title = "Hidden",
                            onClick = {
                                selectedChipIndex = 2
//                                onEvent(FeedEvents.FetchFeed(FetchType.HIDDEN))
                                onEvent(FeedEvents.ChangeFetchType(FetchType.HIDDEN))
                                onEvent(FeedEvents.FetchFeed)
                            },
                            iconContentDescription = "",
                            icon = R.drawable.ic_hidden
                        ),


                        )
                )
            }


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
                            onEvent(FeedEvents.Delete(currentItem!!.memory))
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

        if (state.memories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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
                memories = List(30) { MemoryWithMediaModel() }
//                memories = emptyList()
            ),
            onEvent = {},
            loadState = false
        )
    }
}

