package com.example.memories.feature.feature_memory.presentation

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.videoFramePercent
import com.example.memories.R
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.MediaResult
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.PlayButton
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.ActionSelectorBottomSheet
import com.example.memories.feature.feature_memory.domain.model.MediaSlot
import com.example.memories.feature.feature_memory.presentation.components.CustomTextField
import com.example.memories.feature.feature_memory.presentation.components.ReminderDatePickerDialog
import com.example.memories.feature.feature_memory.presentation.components.SelectTagBottomSheet
import com.example.memories.feature.feature_memory.presentation.components.TagRow
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val MEDIA_GRID_SLOTS = 5

@Composable
fun MemoryRoot(
    modifier: Modifier = Modifier,
    viewModel: MemoryViewModel = hiltViewModel<MemoryViewModel>(),
    onBackPress: () -> Unit,
    onGoToHomeScreen: (TopLevelScreen.Feed) -> Unit,
    onTagClick: (AppScreen.TagWithMemories) -> Unit,
    onNavigateToCamera: (AppScreen.Camera) -> Unit,
    uriList: List<UriType>,
    takenUri: String?,
) {
    val state by viewModel.memoryState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (state.creationState == CreationState.CREATE && uriList.isNotEmpty()) {
            viewModel.onEvent(MemoryEvents.UpdateList(uriList))
        }
    }

    LaunchedEffect(takenUri) {
        if (takenUri != null && state.currentPosition != null && state.type == MediaActionType.CUSTOM_APP_CAMERA_FEATURE) {
            viewModel.onEvent(
                MemoryEvents.AddMediaUri(
                    UriType(takenUri, Type.fromUri(takenUri.toUri(), context)),
                    state.currentPosition!!
                )
            )
            viewModel.onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.mediaResultChannel.collect { result ->
            when (result) {
                is MediaResult.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = result.message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is MediaResult.Success<CreationState> -> {
                    when (result.data) {
                        CreationState.CREATE -> onGoToHomeScreen(TopLevelScreen.Feed)
                        CreationState.UPDATE -> onBackPress()
                    }
                }
            }
        }
    }

    MemoryScreen(
        onBackPress = onBackPress,
        onEvent = viewModel::onEvent,
        state = state,
        onTagClick = onTagClick,
        onNavigateToCamera = onNavigateToCamera,
        snackbarHostState = snackbarHostState
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onBackPress: () -> Unit = {},
    onEvent: (MemoryEvents) -> Unit = {},
    state: MemoryState,
    onTagClick: (AppScreen.TagWithMemories) -> Unit = {},
    onNavigateToCamera: (AppScreen.Camera) -> Unit = {},
    snackbarHostState: SnackbarHostState,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val titleInteractionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTagBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showTagDeleteDialog by remember { mutableStateOf(false) }
    var showMediaPickerSelectorSheet by remember { mutableStateOf(false) }
    var showMediaTypeSelectorSheet by remember { mutableStateOf(false) }
    var tagItem by remember { mutableStateOf<TagModel?>(null) }

    val tagBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Lifecycle observer
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> lifecycle = event }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Camera / media launchers
    val onCaptureSuccess: (Boolean) -> Unit = { successful ->
        if (successful && state.tempMediaUri != null && state.currentPosition != null && state.type == MediaActionType.DEVICE_CAMERA) {
            onEvent(
                MemoryEvents.AddMediaUri(
                    UriType(state.tempMediaUri, Type.fromUri(state.tempMediaUri.toUri(), context)),
                    state.currentPosition
                )
            )
            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }

    val openDeviceCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = onCaptureSuccess
    )

    val openDeviceVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = onCaptureSuccess
    )

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && state.currentPosition != null && state.type == MediaActionType.PHOTO_PICKER) {
            onEvent(
                MemoryEvents.AddMediaUri(
                    UriType(uri.toString(), Type.fromUri(uri, context)),
                    position = state.currentPosition
                )
            )
            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }

    LaunchedEffect(state.tempMediaUri) {
        val uri = state.tempMediaUri ?: return@LaunchedEffect
        if (state.type != MediaActionType.DEVICE_CAMERA) return@LaunchedEffect

        when (state.mediaType) {
            MediaType.IMAGE -> openDeviceCameraLauncher.launch(uri.toUri())
            MediaType.VIDEO -> openDeviceVideoLauncher.launch(uri.toUri())
            else -> {}
        }
    }

    fun captureMedia(type: MediaType) {
        onEvent(MemoryEvents.UpdateMediaType(type))
        onEvent(MemoryEvents.OpenDeviceCamera(type))
        showMediaPickerSelectorSheet = false
        showMediaTypeSelectorSheet = false
    }

    // Main content
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MemoryTopBar(
                state = state,
                onBackPress = onBackPress,
                onEvent = onEvent
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(scrollState),
            ) {
                MediaGrid(
                    state = state,
                    onEvent = onEvent,
                    onAddClick = { index ->
                        onEvent(MemoryEvents.UpdateCurrentPosition(index))
                        showMediaPickerSelectorSheet = true
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                DateField(
                    timestamp = state.memoryForTimeStamp,
                    onDatePickerToggle = { showDatePicker = !showDatePicker }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TagRow(
                    totalTags = state.tagsSelectedForThisMemory,
                    showAdd = true,
                    onAddClick = { showTagBottomSheet = true },
                    onTagClick = { id, label ->
                        onTagClick(AppScreen.TagWithMemories(id = id, tagLabel = label))
                    }
                )

                CustomTextField(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    isHintVisible = state.isTitleHintVisible,
                    hintContent = state.titleHintContent,
                    content = state.title,
                    onValueChange = { onEvent(MemoryEvents.TitleChanged(it)) },
                    onFocusChanged = { onEvent(MemoryEvents.TitleFocusChanged(it)) },
                    interactionSource = titleInteractionSource,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )

                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    isHintVisible = state.isContentHintVisible,
                    hintContent = state.contentHintContent,
                    content = state.content,
                    onValueChange = { onEvent(MemoryEvents.ContentChanged(it)) },
                    onFocusChanged = { onEvent(MemoryEvents.ContentFocusChanged(it)) },
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom sheets & dialogs
        if (showMediaPickerSelectorSheet) {
            MediaPickerSheet(
                onDismiss = { showMediaPickerSelectorSheet = false },
                onDeviceCamera = {
                    onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.DEVICE_CAMERA))
                    showMediaPickerSelectorSheet = false
                    showMediaTypeSelectorSheet = true
                },
                onCustomCamera = {
                    onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.CUSTOM_APP_CAMERA_FEATURE))
                    onNavigateToCamera(AppScreen.Camera)
                    showMediaPickerSelectorSheet = false
                },
                onGallery = {
                    onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.PHOTO_PICKER))
                    mediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                    showMediaPickerSelectorSheet = false
                }
            )
        }

        if (showMediaTypeSelectorSheet) {
            MediaTypeSelectorSheet(
                onDismiss = { showMediaTypeSelectorSheet = false },
                onSelectType = ::captureMedia
            )
        }

        if (showDatePicker) {
            ReminderDatePickerDialog(
                onDismiss = {
                    showDatePicker = false
                    focusManager.clearFocus()
                },
                onConfirm = { dateInMillis ->
                    onEvent(MemoryEvents.DateChanged(dateInMillis))
                    focusManager.clearFocus()
                    showDatePicker = false
                },
                datePickerState = rememberDatePickerState(
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis <= System.currentTimeMillis()
                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun isSelectableYear(year: Int): Boolean {
                            return year <= LocalDate.now().year
                        }
                    }
                )
            )
        }

        if (showTagBottomSheet) {
            SelectTagBottomSheet(
                onDismiss = {
                    scope.launch {
                        tagBottomSheetState.hide()
                        showTagBottomSheet = false
                    }
                },
                tagQuery = state.tagTextFieldValue,
                selectedTags = state.tagsSelectedForThisMemory,
                onTagQueryChange = { newText ->
                    if (!newText.contains("\n")) {
                        onEvent(MemoryEvents.TagsTextFieldContentChanged(newText))
                    }
                },
                onCreateTagClick = { tag ->
                    if (tag.isNotEmpty()) {
                        onEvent(MemoryEvents.AddTag(tag))
                        onEvent(MemoryEvents.TagsTextFieldContentChanged(""))
                    }
                },
                onTagItemClick = { tag ->
                    if (tag in state.tagsSelectedForThisMemory) {
                        onEvent(MemoryEvents.RemoveTagsFromTextField(tag))
                    } else {
                        onEvent(MemoryEvents.UpdateTagsInTextField(tag))
                    }
                },
                onCrossClick = { tag ->
                    tagItem = tag
                    showTagDeleteDialog = true
                },
                onResetCLick = { onEvent(MemoryEvents.Reset) },
                onClearTextClick = { onEvent(MemoryEvents.TagsTextFieldContentChanged("")) },
                savedTags = state.totalNumberOfTags,
                sheetState = tagBottomSheetState
            )
        }

        if (showTagDeleteDialog && tagItem != null) {
            GeneralAlertSheet(
                title = "Delete \"${tagItem!!.label}\" ?",
                content = "Are you sure you want to delete this tag? This will not delete the memories associated with it, only the tag itself.",
                state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismiss = {
                    showTagDeleteDialog = false
                    tagItem = null
                },
                onConfirm = {
                    onEvent(MemoryEvents.TagDelete(tagItem!!.tagId))
                    onEvent(MemoryEvents.RemoveTagsFromTextField(tagItem!!))
                    showTagDeleteDialog = false
                }
            )
        }
    }
}

// ── Extracted composables ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoryTopBar(
    state: MemoryState,
    onBackPress: () -> Unit,
    onEvent: (MemoryEvents) -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (state.creationState == CreationState.CREATE) "Create Memory" else "Update Memory",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            TextButton(
                onClick = {
                    when (state.creationState) {
                        CreationState.CREATE -> {
                            onEvent(
                                MemoryEvents.CreateMemory(
                                    uriList = state.uriMap.map { it.value },
                                    title = state.title,
                                    content = state.content
                                )
                            )
                        }

                        CreationState.UPDATE -> {
                            val slots = state.uriMap.map { it.value }.map { uriType ->
                                val existing = state.originalMediaList.find { it.uri == uriType.uri }
                                if (existing != null) MediaSlot.Existing(existing)
                                else MediaSlot.New(uriType)
                            }
                            onEvent(MemoryEvents.UpdateMemory(orderedMediaSlots = slots))
                        }
                    }
                }
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = if (state.creationState == CreationState.CREATE) "Create" else "Update",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    )
}

@Composable
private fun MediaGrid(
    state: MemoryState,
    onEvent: (MemoryEvents) -> Unit,
    onAddClick: (Int) -> Unit,
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(MEDIA_GRID_SLOTS) { index ->
            MediaGridItem(
                item = state.uriMap[index],
                onRemove = { onEvent(MemoryEvents.RemoveMediaUri(index)) },
                onAdd = { onAddClick(index) }
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    item: UriType?,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
) {
    val context = LocalContext.current
    val itemType = item?.uri?.let { Type.fromUri(it.toUri(), context) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(25.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(25.dp))
    ) {
        AnimatedContent(targetState = item?.uri, label = "media_slot") { uri ->
            if (uri != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(uri)
                            .videoFramePercent(0.5)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )

                    IconItem(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove media",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        alpha = 0f,
                        onClick = onRemove,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (itemType != null && itemType.isVideoFile()) {
                        PlayButton(modifier = Modifier.align(Alignment.Center))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onAdd),
                ) {
                    IconItem(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add media",
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        alpha = 0f,
                        onClick = onAdd,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DateField(
    timestamp: Long?,
    onDatePickerToggle: () -> Unit,
) {
    OutlinedTextField(
        value = timestamp?.formatTime(format = "dd/MM/YYYY") ?: "",
        onValueChange = {},
        label = { Text("Date") },
        placeholder = { Text("DD/MM/YYYY") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Transparent,
            errorCursorColor = Color.Transparent
        ),
        readOnly = true,
        maxLines = 1,
        trailingIcon = {
            IconButton(onClick = onDatePickerToggle) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaPickerSheet(
    onDismiss: () -> Unit,
    onDeviceCamera: () -> Unit,
    onCustomCamera: () -> Unit,
    onGallery: () -> Unit,
) {
    ActionSelectorBottomSheet(
        onDismiss = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetTitle = "Media Picker",
        showLoading = false,
        loadingItemIndex = null,
        items = listOf(
            MenuItem(
                title = "Use Device Camera",
                icon = R.drawable.ic_camera,
                content = "Open system default camera app",
                onClick = onDeviceCamera
            ),
            MenuItem(
                title = "Use Memories Camera",
                icon = R.drawable.ic_aperture,
                content = "Capture with Memories' custom camera",
                onClick = onCustomCamera
            ),
            MenuItem(
                title = "Choose from Gallery",
                icon = R.drawable.ic_feed,
                content = "Select from your device photos",
                onClick = onGallery
            ),
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaTypeSelectorSheet(
    onDismiss: () -> Unit,
    onSelectType: (MediaType) -> Unit,
) {
    ActionSelectorBottomSheet(
        onDismiss = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetTitle = "What would you like to capture?",
        showLoading = false,
        loadingItemIndex = null,
        items = listOf(
            MenuItem(
                title = "Take a Photo",
                icon = R.drawable.ic_camera,
                content = "Snap a quick picture",
                onClick = { onSelectType(MediaType.IMAGE) }
            ),
            MenuItem(
                title = "Record a Video",
                icon = R.drawable.ic_video,
                content = "Capture a moving moment",
                onClick = { onSelectType(MediaType.VIDEO) }
            )
        )
    )
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun MemoryScreenPreview() {
    MemoriesTheme {
        MemoryScreen(
            state = MemoryState(isLoading = true),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}