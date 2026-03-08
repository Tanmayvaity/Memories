package com.example.memories.feature.feature_memory.presentation

import android.R.id.message
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.example.memories.LocalTheme
import com.example.memories.R
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.MediaResult
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.ActionSelectorBottomSheet
import com.example.memories.feature.feature_memory.domain.model.MediaSlot
import com.example.memories.feature.feature_memory.presentation.CreationState.*
import com.example.memories.feature.feature_memory.presentation.components.CustomTextField
import com.example.memories.feature.feature_memory.presentation.components.ReminderDatePickerDialog
import com.example.memories.feature.feature_memory.presentation.components.SelectTagBottomSheet
import com.example.memories.feature.feature_memory.presentation.components.TagRow
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MemoryRoot(
    modifier: Modifier = Modifier,
    viewModel: MemoryViewModel = hiltViewModel<MemoryViewModel>(),
    onBackPress: () -> Unit,
    onGoToHomeScreen: (TopLevelScreen.Feed) -> Unit,
    onTagClick: (AppScreen.TagWithMemories) -> Unit,
    onNavigateToCamera : (AppScreen.Camera) -> Unit ,
    uriList: List<UriType>,
    takenUri : String?,
) {
    val state by viewModel.memoryState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        if (state.creationState == CreationState.CREATE && uriList.isNotEmpty()) {
            viewModel.onEvent(MemoryEvents.UpdateList(uriList))
        }

    }

    LaunchedEffect(takenUri) {
        if(takenUri != null && state.currentPosition != null && state.type == MediaActionType.CUSTOM_APP_CAMERA_FEATURE){
            viewModel.onEvent(MemoryEvents.AddMediaUri(takenUri,state.currentPosition!!))
            viewModel.onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }


    LaunchedEffect(Unit) {
        viewModel.mediaResultChannel.collect { result ->
            when(result){
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
                    when(result.data){
                        CreationState.CREATE -> {
                            onGoToHomeScreen(TopLevelScreen.Feed)
                        }

                        CreationState.UPDATE -> {
                            onBackPress()
                        }

                    }
                }
            }
        }
    }


//
//    LaunchedEffect(Unit) {
//        errorFLow!!.collect { message ->
//            Log.d("MemoryScreen", "MemoryRoot: error ${message}")
//            scope.launch {
//                snackbarHostState.showSnackbar(
//                    message = message,
//                    withDismissAction = true,
//                    duration = SnackbarDuration.Short
//                )
//            }
//        }
//    }

//    LaunchedEffect(Unit) {
//        successFlow!!.collect { message ->
//            when (state.creationState) {
//                CreationState.CREATE -> {
//
//                }
//
//                CreationState.UPDATE -> {
//                    onBackPress()
//                }
//
//            }
//
//        }
//    }

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
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var showTagBottomSheet by rememberSaveable { mutableStateOf(false) }
    var tagItem by remember { mutableStateOf<TagModel?>(null) }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val tagBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTagDeleteDialog by remember { mutableStateOf(false) }
    var showMediaPickerSelectorSheet by remember { mutableStateOf(false) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

        }
    }



    val openDeviceCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { successful ->
        if(successful && state.tempMediaUri != null && state.currentPosition != null && state.type == MediaActionType.DEVICE_CAMERA){
            onEvent(MemoryEvents.AddMediaUri(state.tempMediaUri,state.currentPosition))
            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }

    }

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null && state.currentPosition != null && state.type == MediaActionType.PHOTO_PICKER) {
            onEvent(
                MemoryEvents.AddMediaUri(
                    uri = uri.toString(),
                    position = state.currentPosition
                )
            )
            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.NONE))
        }
    }

    LaunchedEffect(state.tempMediaUri) {
        if(state.tempMediaUri != null && state.type == MediaActionType.DEVICE_CAMERA){
            openDeviceCameraLauncher.launch(state.tempMediaUri.toUri())
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Memory",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPress()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to previous screen"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            when (state.creationState) {
                                CREATE -> {
                                    onEvent(
                                        MemoryEvents.CreateMemory(
                                            uriList = state.uriMap.map { it.value },
                                            title = state.title,
                                            content = state.content
                                        )
                                    )
                                }

                                UPDATE -> {
                                    val slots = state.uriMap
                                        .map { it ->
                                            it.value
                                        }
                                        .map { uri ->
                                            val existingMedia = state.originalMediaList.find { it.uri == uri.uri }
                                            if(existingMedia == null){
                                                MediaSlot.New(uri)
                                            }else{
                                                MediaSlot.Existing(existingMedia)
                                            }
                                        }
                                    onEvent(
                                        MemoryEvents.UpdateMemory(
                                            orderedMediaSlots = slots
                                        )
                                    )
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(5) { itemIndex ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(25.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(25.dp)
                                )
                        ) {
                            val item = state.uriMap[itemIndex]

                            AnimatedContent(
                                item
                            ) { state ->
                                if (state != null) {
                                    AsyncImage(
                                        model = state.uri
                                            ?: R.drawable.ic_launcher_background,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                    )
                                    IconItem(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove media",
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp),
                                        alpha = 0.3f,
                                        onClick = {
                                            onEvent(MemoryEvents.RemoveMediaUri(itemIndex))
                                        },
                                        color = MaterialTheme.colorScheme.primary
                                    )
//                                }
                                }
                                else{
                                    IconItem(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add media",
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp),
                                        alpha = 0f,
                                        onClick = {
                                            onEvent(MemoryEvents.UpdateCurrentPosition(itemIndex))
                                            showMediaPickerSelectorSheet = true
                                        },
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                            }

                        }
                    }
                }



                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                OutlinedTextField(
                    value = state.memoryForTimeStamp?.formatTime(format = "dd/MM/YYYY") ?: "",
                    onValueChange = {},
                    label = {
                        Text(
                            text = "Date"
                        )
                    },
                    placeholder = {
                        Text(
                            text = "DD/MM/YYYY"
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color.Transparent,
                        errorCursorColor = Color.Transparent
                    ),
                    readOnly = true,
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker = !showDatePicker
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date For Memory"
                            )
                        }
                    }
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                TagRow(
                    totalTags = state.tagsSelectedForThisMemory,
                    showAdd = true,
                    onAddClick = {
                        showTagBottomSheet = true
                    },
                    onTagClick = { id, label ->
                        onTagClick(
                            AppScreen.TagWithMemories(
                                id = id,
                                tagLabel = label
                            )
                        )
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
                    onValueChange = { it ->
                        onEvent(MemoryEvents.TitleChanged(it))
                    },
                    onFocusChanged = {
                        onEvent(MemoryEvents.TitleFocusChanged(it))
                    },
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
                    onValueChange = { it ->
                        onEvent(MemoryEvents.ContentChanged(it))
                    },
                    onFocusChanged = {
                        onEvent(MemoryEvents.ContentFocusChanged(it))
                    },

                    )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }



        if(showMediaPickerSelectorSheet){
            ActionSelectorBottomSheet(
                onDismiss = {
                    showMediaPickerSelectorSheet = false
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                sheetTitle = "Media Picker",
                showLoading = false,
                loadingItemIndex = null,
                items = listOf(
                    MenuItem(
                        title = "Use Device Camera",
                        icon = R.drawable.ic_camera,
                        content = "Open System Default Camera App",
                        onClick = {
                            Toast.makeText(
                                context,
                                "Device  Camera",
                                Toast.LENGTH_LONG
                            ).show()
                            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.DEVICE_CAMERA))
                            onEvent(MemoryEvents.OpenDeviceCamera)
                            showMediaPickerSelectorSheet = false
                        }
                    ),
                    MenuItem(
                        title = "Use Memories' custom Camera feature",
                        icon = R.drawable.ic_aperture,
                        content = "Use Memories' custom camera feature for capturing media",
                        onClick = {
                            Toast.makeText(
                                context,
                                "Custom Camera",
                                Toast.LENGTH_LONG
                            ).show()
                            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.CUSTOM_APP_CAMERA_FEATURE))
                            onNavigateToCamera(AppScreen.Camera)
                            showMediaPickerSelectorSheet = false
                        }
                    ),
                    MenuItem(
                        title = "Choose From gallery",
                        icon = R.drawable.ic_feed,
                        content = "Select from your device photos",
                        onClick = {
                            onEvent(MemoryEvents.UpdateMediaActionType(MediaActionType.PHOTO_PICKER))
                            mediaLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            showMediaPickerSelectorSheet = false
                        }
                    ),
                )
            )
        }

        if (showDatePicker) {
            ReminderDatePickerDialog(
                onDismiss = {
                    showDatePicker = false
                    focusManager.clearFocus()
                },
                onConfirm = { dateInMillis ->
//                    onEvent(MemoryEvents.ReminderDateChanged(dateInMillis))
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
                    if (newText.contains("\n")) return@SelectTagBottomSheet
                    onEvent(MemoryEvents.TagsTextFieldContentChanged(newText))
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
                onResetCLick = {
                    onEvent(MemoryEvents.Reset)
                },
                onClearTextClick = {
                    onEvent(MemoryEvents.TagsTextFieldContentChanged(""))
                },
                savedTags = state.totalNumberOfTags,
                sheetState = tagBottomSheetState
            )
        }

        if (showTagDeleteDialog && tagItem != null) {
            GeneralAlertSheet(
                title = "Delete \"${tagItem!!.label}\" ?",
                content = "Are you sure you want to delete this tag? This will not delete the memories " +
                        "associated with it,only the tag itself",
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

@Preview
@Composable
fun MemoryScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryScreen(
            onBackPress = {},
            onEvent = {},
            state = MemoryState(isLoading = true),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}





