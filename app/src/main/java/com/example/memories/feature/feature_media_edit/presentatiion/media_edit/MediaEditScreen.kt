package com.example.memories.feature.feature_media_edit.presentation.media_edit

import android.R.attr.type
import android.graphics.RuntimeShader
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.R
import com.example.memories.core.domain.model.MediaActionType
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.model.Video
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.UiState
import com.example.memories.core.presentation.components.ActionSelectorBottomSheet
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.MediaCaptureHost
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.startChooser
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.EditTool
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.EditorState
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaEditOneTimeEvents
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaEvents
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaViewModel
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.RotationDirection
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.flowOf

private const val MAX_MEDIA_PAGES = 5

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MediaEditRoot(
    modifier: Modifier = Modifier,
    viewModel: MediaViewModel = hiltViewModel(),
    onBackPress: () -> Unit = {},
    onNextClick: (AppScreen.Memory) -> Unit = {},
    onNavigateToCamera: (AppScreen.Camera) -> Unit = {},
    takenUri: String? = null,
    preloadUri: String? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remoteImages =  viewModel.remoteImages.collectAsLazyPagingItems()
    val remoteVideos = viewModel.remoteVideos.collectAsLazyPagingItems()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Media opened from the "Manage Media" screen lands directly on the Adjust tool.
    LaunchedEffect(preloadUri) {
        if (preloadUri != null) {
            viewModel.onEvent(
                MediaEvents.AddMediaUri(
                    UriType(preloadUri, Type.fromUri(preloadUri.toUri(), context)),
                    0
                )
            )
            viewModel.onEvent(MediaEvents.UpdateCurrentPosition(0))
            viewModel.onEvent(MediaEvents.EditToolStateChange(EditTool.ADJUST))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.downloadEvents.collect { event ->
            when (event) {
                is MediaEditOneTimeEvents.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.value,
                        duration = SnackbarDuration.Short
                    )
                }

                is MediaEditOneTimeEvents.ShowShareChooser -> {
                    context.startChooser(event.value)
                }

                is MediaEditOneTimeEvents.NavigateToMemory -> {
                    onNextClick(AppScreen.Memory(null, event.value))
                }
            }
        }
    }

    LaunchedEffect(takenUri) {
        if (takenUri != null &&
            state.currentPosition != null &&
            state.mediaActionType == MediaActionType.CUSTOM_APP_CAMERA_FEATURE
        ) {
            viewModel.onEvent(
                MediaEvents.AddMediaUri(
                    UriType(takenUri, Type.fromUri(takenUri.toUri(), context)),
                    state.currentPosition!!
                )
            )
            viewModel.onEvent(MediaEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }

    MediaEditScreen(
        state = state,
        remoteImages = remoteImages,
        remoteVideos = remoteVideos,
        onEvent = viewModel::onEvent,
        onBackPress = onBackPress,
        onNextClick = onNextClick,
        onNavigateToCamera = onNavigateToCamera,
        snackBarHostState = snackBarHostState
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEditScreen(
    state: EditorState = EditorState(),
    remoteImages : LazyPagingItems<Photo>,
    remoteVideos : LazyPagingItems<Video>,
    onEvent: (MediaEvents) -> Unit = {},
    onBackPress: () -> Unit = {},
    onNextClick: (AppScreen.Memory) -> Unit = {},
    onNavigateToCamera: (AppScreen.Camera) -> Unit = {},
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var showMediaPickerSheet by remember { mutableStateOf(false) }
    var showMediaTypeSheet by remember { mutableStateOf(false) }
    var loadingItemIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val pagerState = rememberPagerState(initialPage = 0) { MAX_MEDIA_PAGES }
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
//    var targetRotation by remember { mutableFloatStateOf(0f) }
    val targetRotation = state.adjustStateMap[currentPage]?.rotationDegrees ?: 0f
    val animatedRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )
    val contentScale by remember(targetRotation) {
        derivedStateOf {
            val normalizedRotation = ((targetRotation % 360) + 360) % 360
            when (normalizedRotation.toInt()) {
                90, 270 -> ContentScale.FillHeight  // Rotated sideways
                else -> ContentScale.FillWidth      // Normal orientation (0, 180)
            }
        }
    }
    val adjustListState = rememberLazyListState()
    val filterListState = rememberLazyListState()

    val isImagePage = state.isImageAt(currentPage)
    val hasMediaOnPage = state.hasMediaAt(currentPage)
    val pageUri = state.uriMap[currentPage]?.uri

    val shaderCode = state.adjustStateMap[currentPage]?.shaderStep?.shaderCode
    val runtimeShader =
        remember(shaderCode, isImagePage) {
            if (!isImagePage) null
            else shaderCode?.let { RuntimeShader(it) }
        }
    val shaderNeedsResolution = remember(shaderCode) {
        shaderCode?.contains("resolution") == true
    }

//    val filterEffect = if (runtimeShader != null) {
//        android.graphics.RenderEffect.createRuntimeShaderEffect(
//            runtimeShader,
//            "inputShader"
//        )
//    } else {
//        null
//    }

//    val adjustmentEffect = if(adjustmentShader != null){
//       android.graphics.RenderEffect.createRuntimeShaderEffect(
//            adjustmentShader,
//            "inputShader"
//        )
//    }else{
//        null
//    }


//    val chainEffect = when {
//        filterEffect != null && adjustmentEffect != null ->
//            android.graphics.RenderEffect.createChainEffect(filterEffect,adjustmentEffect)
//
//        adjustmentEffect != null -> adjustmentEffect
//        filterEffect != null -> filterEffect
//        else -> null
//    }

    // Scroll to current adjustment/filter when page or selection changes
    LaunchedEffect(currentPage, state.adjustStateMap[currentPage]?.currentAdjustType) {
        val item = state.adjustStateMap[currentPage]?.currentAdjustType?.ordinal ?: 0
        adjustListState.animateScrollToItem(item)
    }

    LaunchedEffect(currentPage, state.adjustStateMap[currentPage]?.filterType) {
        filterListState.animateScrollToItem(
            state.adjustStateMap[currentPage]?.filterType?.ordinal ?: 0
        )
    }

    LaunchedEffect(state.isDownloading) {
        if (!state.isDownloading) {
            showEditBottomSheet = false
        }
    }

    LaunchedEffect(state.isSharing) {
        if (!state.isSharing) {
            showEditBottomSheet = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .consumeWindowInsets(WindowInsets.navigationBars)
            .animateContentSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            MediaEditTopBar(
                onBackPress = onBackPress,
                onNextClick = {
                    onEvent(MediaEvents.SaveMultipleImages)
                },
                enabled = state.uriMap.isNotEmpty(),
                isLoading = state.isDownloadingForNavigation
            )
        },
        bottomBar = {
            MediaEditBottomBar(
                activeTool = state.initialActiveTool,
                isEnabled = isImagePage && hasMediaOnPage,
                onToolSelected = { tool ->
                    onEvent(MediaEvents.EditToolStateChange(tool))
                    if (tool == EditTool.MORE) showEditBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            MediaPager(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                uris = (0 until MAX_MEDIA_PAGES).map { state.uriMap[it] ?: UriType(null, null) },
                imageContentScale = ContentScale.Crop,
                pagerState = pagerState,
                isActive = true,
                imageModifier = {
                    Modifier.graphicsLayer {
                        if (runtimeShader != null) {
                            // `resolution` must always be set (else the RuntimeShader throws),
                            // so fall back to 1px while the layer is still being measured.
                            if (shaderNeedsResolution) {
                                runtimeShader.setFloatUniform(
                                    "resolution",
                                    maxOf(size.width, 1f),
                                    maxOf(size.height, 1f)
                                )
                            }
                            val effect = android.graphics.RenderEffect.createRuntimeShaderEffect(
                                runtimeShader,
                                "inputShader"
                            )
                            renderEffect = effect.asComposeRenderEffect()
                        }

                        rotationZ = animatedRotation
                    }
                },
                emptyPageContent = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentPage in state.downloadingPositions) {
                            LoadingIndicator(showText = false)
                        } else {
                            IconItem(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add media",
                                alpha = 0.3f,
                                color = MaterialTheme.colorScheme.onSurface,
                                onClick = {
                                    onEvent(MediaEvents.UpdateCurrentPosition(currentPage))
                                    showMediaPickerSheet = true
                                }
                            )
                        }
                    }
                },
                pageOverlay = { page ->
                    IconItem(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove media",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        alpha = 0.3f,
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onEvent(MediaEvents.RemoveMediaUri(page))
                        }
                    )
                }
            )

            AdjustmentPanel(
                visible = state.isFilterOrAdjustOrRotateType(state.initialActiveTool),
                state = state,
                currentPage = currentPage,
                hasMedia = hasMediaOnPage,
                isImagePage = isImagePage,
                adjustListState = adjustListState,
                filterListState = filterListState,
                onEvent = onEvent,
                onLeftClick = {
                    onEvent(
                        MediaEvents.ChangeRotation(
                            value = 90f,
                            direction = RotationDirection.LEFT,
                            page = currentPage
                        )
                    )
                },
                onRightClick = {
                    onEvent(
                        MediaEvents.ChangeRotation(
                            value = 90f,
                            direction = RotationDirection.RIGHT,
                            page = currentPage
                        )
                    )
                }

            )
        }
    }

    if (showEditBottomSheet || state.isDownloading || state.isSharing) {
        MoreOptionsSheet(
            sheetState = sheetState,
            onDismiss = { showEditBottomSheet = false },
            loadingItemIndex = loadingItemIndex,
            loadingState = state.isDownloading || state.isSharing,
            onDownloadImage = { index ->
                loadingItemIndex = index
                onEvent(
                    MediaEvents.DownloadMedia(
                        uri = pageUri?.toUri(),
                        page = currentPage,
                        degrees = targetRotation
                    )
                )

            },
            onSharingImage = { index ->
                loadingItemIndex = index
                if (state.isImageAt(currentPage)) {
                    onEvent(
                        MediaEvents.ShareImage(
                            uri = pageUri?.toUri(),
                            page = currentPage,
                            degrees = targetRotation
                        )
                    )
                }

            }
        )
    }

    MediaCaptureHost(
        tempMediaUri = state.tempMediaUri,
        mediaActionType = state.mediaActionType,
        mediaType = state.mediaType,
        currentPosition = state.currentPosition,
        showPickerSheet = showMediaPickerSheet,
        showTypeSheet = showMediaTypeSheet,
        onPickerSheetDismiss = { showMediaPickerSheet = false },
        onTypeSheetDismiss = { showMediaTypeSheet = false },
        onShowTypeSheet = { showMediaTypeSheet = true },
        onRequestDeviceCameraUri = { type -> onEvent(MediaEvents.OpenDeviceCamera(type)) },
        onUpdateMediaActionType = { type -> onEvent(MediaEvents.UpdateMediaActionType(type)) },
        onMediaSelected = { uriType, position ->
            onEvent(MediaEvents.AddMediaUri(uriType, position))
        },
        onWebMediaSelected = { url, isVideo, position ->
            onEvent(MediaEvents.SelectWebMedia(url, isVideo, position))
        },
        onNavigateToCamera = { onNavigateToCamera(AppScreen.Camera) },
        remoteImages = remoteImages,
        remoteVideos = remoteVideos

    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaEditTopBar(
    onBackPress: () -> Unit,
    onNextClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
) {
    AppTopBar(
        showNavigationIcon = true,
        onNavigationIconClick = onBackPress,
        title = { Text("Edit") },
        showDivider = false,
        showAction = true,
        actionContent = {

            Button(
                onClick = onNextClick,
                modifier = Modifier.padding(end = 8.dp),
                enabled = enabled
            ) {
                if (!isLoading) {
                    Text(
                        text = "Next",
                    )
                } else {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }

            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MediaEditBottomBar(
    activeTool: EditTool,
    isEnabled: Boolean,
    onToolSelected: (EditTool) -> Unit,
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        EditTool.entries.forEach { tool ->
            val enabled = when (tool) {
                EditTool.ADJUST, EditTool.FILTER, EditTool.ROTATE, EditTool.CROP -> isEnabled
                else -> true
            }
            NavigationBarItem(
                selected = activeTool == tool,
                enabled = enabled,
                onClick = { onToolSelected(tool) },
                icon = {
                    Icon(
                        painter = painterResource(tool.icon),
                        contentDescription = tool.name
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Adjustment Panel
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdjustmentPanel(
    visible: Boolean,
    state: EditorState,
    currentPage: Int,
    hasMedia: Boolean,
    isImagePage: Boolean,
    adjustListState: LazyListState,
    filterListState: LazyListState,
    onEvent: (MediaEvents) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    val isAdjustMode = state.isAdjustType

    AnimatedVisibility(
        visible = visible,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 16.dp,
                bottomStart = 16.dp
            ),
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AdjustmentHeader(
                    isAdjustMode = isAdjustMode,
                    state = state,
                    currentPage = currentPage,
                    onEvent = onEvent,
                    hasMedia = hasMedia
                )
                Spacer(Modifier.height(16.dp))
                ToolSelector(
                    activeTool = state.initialActiveTool,
                    state = state,
                    currentPage = currentPage,
                    adjustListState = adjustListState,
                    filterListState = filterListState,
                    onEvent = onEvent,
                    onLeftClick = onLeftClick,
                    onRightClick = onRightClick,
                    hasMedia = hasMedia,
                    isImagePage = isImagePage,
                )
            }
        }
    }
}

@Composable
private fun AdjustmentHeader(
    isAdjustMode: Boolean,
    state: EditorState,
    currentPage: Int,
    hasMedia: Boolean,
    onEvent: (MediaEvents) -> Unit,
) {
    AnimatedContent(
        targetState = isAdjustMode,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
        label = "HeaderSwitch"
    ) { isAdjust ->
        val tool = state.initialActiveTool
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isAdjust) {
                    Icon(
                        painter = painterResource(
                            state.adjustStateMap[currentPage]?.currentAdjustType?.icon
                                ?: R.drawable.ic_brightness_2
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                }

                val textValue = when {
                    state.isAdjustType -> {
                        state.adjustStateMap[currentPage]?.currentAdjustType?.name
                            ?: AdjustType.BRIGHTNESS.adjustTypeName
                    }

                    state.isFilterType -> {
                        state.adjustStateMap[currentPage]?.filterType?.displayName
                            ?: FilterType.ORIGINAL.displayName

                    }

                    state.isRotateType -> {
                        "Rotate"
                    }

                    else -> ""
                }
                Text(
                    text = textValue,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isAdjust) {
                    ValueBadge(
                        value = state.getAdjustmentValue(
                            state.adjustStateMap[currentPage]?.currentAdjustType
                                ?: AdjustType.BRIGHTNESS, currentPage
                        )
                            .toInt(),
                        onClick = {
                            onEvent(MediaEvents.OnAdjustTypeValueClick(currentPage))
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            if (isAdjust) {

                val currentAdjustType =
                    state.adjustStateMap[currentPage]?.currentAdjustType ?: AdjustType.BRIGHTNESS
                val minValue = currentAdjustType.min
                val maxValue = currentAdjustType.max

                AdjustmentSlider(
                    value = state.getAdjustmentValue(currentAdjustType, currentPage),
                    range = minValue..maxValue,
                    enabled = hasMedia,
                    onValueChange = {
                        onEvent(MediaEvents.AdjustTypeValueChange(it, currentPage))
                    }
                )
            }
        }
    }
}

@Composable
private fun ValueBadge(
    value: Int,
    onClick: () -> Unit,
) {
    Text(
        text = "$value",
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdjustmentSlider(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = range,
        enabled = enabled,
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState,
                modifier = Modifier.height(5.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
                    inactiveTrackColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else Color.LightGray
                )
            )
        },
        thumb = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Tool Selectors (Adjust / Filter)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ToolSelector(
    activeTool: EditTool,
    state: EditorState,
    currentPage: Int,
    adjustListState: LazyListState,
    filterListState: LazyListState,
    onEvent: (MediaEvents) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    hasMedia: Boolean,
    isImagePage: Boolean,
) {
    Crossfade(
        targetState = activeTool,
        animationSpec = tween(300),
        label = "ToolCrossfade",
        modifier = Modifier.animateContentSize(animationSpec = tween(300))
    ) { tool ->
        when (tool) {
            EditTool.ROTATE -> {
                Log.d("MediaEditScreen", "ToolSelector: isRotateType ${state.isRotateType}")
                MediaRotateHandler(
                    onLeftClick = onLeftClick,
                    onRightClick = onRightClick,
                    enabled = hasMedia
                )
            }

            EditTool.ADJUST -> {
                if (!isImagePage) {
                    VideoUnsupportedNotice()
                } else {
                    AdjustTypeSelector(
                        selectedType = state.adjustStateMap[currentPage]?.currentAdjustType
                            ?: AdjustType.BRIGHTNESS,
                        listState = adjustListState,
                        onSelect = { onEvent(MediaEvents.AdjustTypeStateChange(it, currentPage)) }
                    )
                }
            }

            EditTool.FILTER -> {
                if (!isImagePage) {
                    VideoUnsupportedNotice()
                } else {
                    FilterTypeSelector(
                        selectedType = state.adjustStateMap[currentPage]?.filterType
                            ?: FilterType.ORIGINAL,
                        listState = filterListState,
                        onSelect = { type ->
                            onEvent(MediaEvents.FilterTypeStateChange(type, currentPage))
                            onEvent(MediaEvents.ApplyFilter(currentPage, type))
                        }
                    )
                }
            }

            else -> {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun VideoUnsupportedNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Filters and adjustments aren't available for videos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AdjustTypeSelector(
    selectedType: AdjustType,
    listState: LazyListState,
    onSelect: (AdjustType) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        items(AdjustType.entries) { type ->
            SelectableChip(
                selected = type == selectedType,
                label = type.adjustTypeName,
                leadingIcon = type.icon,
                onClick = { onSelect(type) }
            )
        }
    }
}

@Composable
private fun FilterTypeSelector(
    selectedType: FilterType,
    listState: LazyListState,
    onSelect: (FilterType) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        items(FilterType.entries) { type ->
            SelectableChip(
                selected = type == selectedType,
                label = type.displayName,
                onClick = { onSelect(type) }
            )
        }
    }
}

@Composable
fun MediaRotateHandler(
    modifier: Modifier = Modifier,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp,
    enabled: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconItem(
            drawableRes = R.drawable.ic_rotate_clockwise_left,
            contentDescription = "left rotate",
            alpha = 0f,
            onClick = onLeftClick,
            color = color,
            iconSize = size,
            modifier = Modifier
                .border(1.dp, color, CircleShape)
                .padding(10.dp),
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconItem(
            drawableRes = R.drawable.ic_rotate_clockwise_right,
            contentDescription = "right rotate",
            alpha = 0f,
            onClick = onRightClick,
            color = color,
            iconSize = size,
            modifier = Modifier
                .border(1.dp, color, CircleShape)
                .padding(10.dp),
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SelectableChip(
    selected: Boolean,
    label: String,
    leadingIcon: Int? = null,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = Modifier.height(36.dp),
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = leadingIcon?.let {
            { Icon(painter = painterResource(it), contentDescription = null) }
        },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptionsSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDownloadImage: (Int) -> Unit,
    onSharingImage: (Int) -> Unit,
    loadingState: Boolean,
    loadingItemIndex: Int? = null,
) {
    ActionSelectorBottomSheet(
        onDismiss = onDismiss,
        sheetState = sheetState,
        sheetTitle = "More Options",
        showLoading = loadingState,
        loadingItemIndex = loadingItemIndex,
        items = listOf(
            MenuItem(
                title = "Save to Device",
                icon = R.drawable.ic_download,
                content = "Save a copy to your local gallery",
                onClick = {
                    onDownloadImage(0)
                }
            ),
            MenuItem(
                title = "Share to Social",
                icon = R.drawable.ic_share,
                content = "Share this with your friends without saving to your gallery",
                onClick = {
                    onSharingImage(1)
                }
            )
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun MediaEditScreenPreview() {
    MemoriesTheme {
        MediaEditScreen(
            state = EditorState(
                initialActiveTool = EditTool.ADJUST,),
            remoteImages = flowOf(PagingData.from(emptyList<Photo>())).collectAsLazyPagingItems(),
            remoteVideos = flowOf(PagingData.from(emptyList<Video>())).collectAsLazyPagingItems()
        )
    }
}