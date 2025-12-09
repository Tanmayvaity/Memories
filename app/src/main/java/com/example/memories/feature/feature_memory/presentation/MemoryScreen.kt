package com.example.memories.feature.feature_memory.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.memories.LocalTheme
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.feature.feature_memory.presentation.components.CustomTextField
import com.example.memories.feature.feature_memory.presentation.components.MediaViewer
import com.example.memories.feature.feature_memory.presentation.components.ReminderDatePickerDialog
import com.example.memories.feature.feature_memory.presentation.components.ReminderPickerButton
import com.example.memories.feature.feature_memory.presentation.components.TagBottomSheet
import com.example.memories.feature.feature_memory.presentation.components.TagRow
import com.example.memories.navigation.TopLevelScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Composable
fun MemoryRoot(
    modifier: Modifier = Modifier,
    viewModel: MemoryViewModel = hiltViewModel<MemoryViewModel>(),
    onBackPress: () -> Unit,
    onGoToHomeScreen: (TopLevelScreen.Feed) -> Unit,
    uriList: List<UriType>,
) {
    val state by viewModel.memoryState.collectAsStateWithLifecycle()
    MemoryScreen(
        onBackPress = onBackPress,
        uriList = uriList,
        onEvent = viewModel::onEvent,
        state = state,
        onCreateClick = onGoToHomeScreen,
        errorFLow = viewModel.errorFlow,
        successFlow = viewModel.successFlow,
        isDarkModeEnabled = LocalTheme.current,
    )


}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onBackPress: () -> Unit = {},
    uriList: List<UriType>,
    onEvent: (MemoryEvents) -> Unit = {},
    state: MemoryState,
    onCreateClick: (TopLevelScreen.Feed) -> Unit = {},
    errorFLow: Flow<String>? = null,
    successFlow: Flow<String>? = null,
    isDarkModeEnabled : Boolean = false,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val titleInteractionSource = remember { MutableInteractionSource() }
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(pageCount = {
        uriList.size
    })
    var showTagBottomSheet by rememberSaveable{ mutableStateOf(false) }
    var newTagsTextValue by rememberSaveable{ mutableStateOf("") }
    val tagsFocusRequester = remember { FocusRequester() }
    val tagTextFieldInteractionSource = remember { MutableInteractionSource() }
    val totalTags = remember { mutableStateListOf<String>() }

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

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
                            Log.i("MemoryScreen", "MemoryScreen: ${uriList == null}")
                            onEvent(
                                MemoryEvents.CreateMemory(
                                    uriList = uriList,
                                    title = state.title,
                                    content = state.content
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Create",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->


        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier

                    .padding(10.dp)
                    .verticalScroll(scrollState),
            ) {
                MediaViewer(
                    pagerState = pagerState,
                    uriTypeList = uriList,
                    imageContentDescription = "Captured Image",
                    lifecycle = lifecycle
                )

                ReminderPickerButton(
                    onClick = {
                        showDatePicker = true
                    },
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    vectorContentDescription = "Arrow Right"
                )
                var tagsText by remember { mutableStateOf("") }

//               RoundedTagTextField(
//                   value = tagsText,
//                   onValueChange = {it ->
//                       tagsText = it
//                   },
//                   onFocus = {
//                       tagExpanded = true
//                   }
//               )

                TagRow(
                    totalTags = state.tagsSelectedForThisMemory,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    showAdd = true,
                    onAddClick = {
                        showTagBottomSheet = true
                    }
                )


//                FlowRow(
//                    horizontalArrangement = Arrangement.spacedBy(5.dp),
//                    verticalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth()
//                        .padding(start = 10.dp,end = 10.dp,top = 15.dp)
//                ) {
//                    repeat(totalTags.size){ index ->
//                        FilterChip(
//                            modifier = Modifier.height(48.dp),
//                            shape = RoundedCornerShape(20.dp),
//                            selected = false,
//                            border = null,
//                            label = {
//                                Text(
//                                    text = totalTags[index].toString(),
//                                    color = MaterialTheme.colorScheme.onPrimaryContainer
//                                )
//                            },
//                            onClick = {},
//                            colors = FilterChipDefaults.filterChipColors(
//                                containerColor = MaterialTheme.colorScheme.primaryContainer
//                            )
//                        )
//                    }
//                    repeat(times = 1){
//                        Button(
//                            onClick = {
//                                showTagBottomSheet = true
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.surface,
//                                contentColor = MaterialTheme.colorScheme.onSurface
//                            ),
//                            modifier = Modifier
//                                .border(
//                                1.dp,
//                                MaterialTheme.colorScheme.primary,
//                                RoundedCornerShape(25.dp)
//                            )
//
//                        ) {
//                            Row (
//                                verticalAlignment = Alignment.CenterVertically
//                            ){
//                                Icon(
//                                    imageVector = Icons.Default.Add,
//                                    contentDescription = "Add Tags"
//                                )
//                                Text(
//                                    text = "Add Tags",
//                                    modifier = Modifier.padding(start = 5.dp),
//                                )
//
//                            }
//                        }
//                    }
//                }
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 15.dp, end = 10.dp),
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

            }
        }

        if (showDatePicker) {
            ReminderDatePickerDialog(
                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        if (showTagBottomSheet) {
            TagBottomSheet(
                textFieldValue = state.tagTextFieldValue,
                onValueChanged = { newText ->
                    Log.d("MemoryScreen", "new Text : ${newText}")
                    // Logic to add a tag when space or enter is pressed
//                    newTagsTextValue = newText
                    if(newText.contains('\n'))return@TagBottomSheet

                    onEvent(MemoryEvents.TagsTextFieldContentChanged(newText))
                },
                focusRequester = tagsFocusRequester,
                textFieldInteraction = tagTextFieldInteractionSource,
                readOnly = false,
                keyboardOptions = KeyboardOptions.Default,
                listOfChips = state.tagsSelectedForThisMemory,
                modifier = Modifier,
                onCrossClick = { index ->
                    onEvent(MemoryEvents.RemoveTagsFromTextField(state.tagsSelectedForThisMemory[index]))
                },
                placeholder = "Add Tags",
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = false
                ),
                onDismiss = {
                    showTagBottomSheet = false
                },
                onChipAddClick = {
                    val tag = state.tagTextFieldValue.trim()
                    Log.d("MemoryScreen", "onChipAddClick: ${tag}")
                    if (tag.isNotEmpty()) {
                        onEvent(MemoryEvents.AddTag(tag))
//                        onEvent(MemoryEvents.TagsTextFieldContentChanged(""))
                    }
                },
                savedTags = state.totalNumberOfTags,
                onTagItemClick = { tag ->
                    onEvent(MemoryEvents.UpdateTagsInTextField(tag))
//                    onEvent(MemoryEvents.TagsTextFieldContentChanged(""))
                },
                isDarkMode = isDarkModeEnabled
            )
        }

        LaunchedEffect(Unit) {
            errorFLow!!.collect { message ->
                Log.d("MemoryScreen", "MemoryRoot: error ${message}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            successFlow!!.collect { message ->
                onCreateClick(TopLevelScreen.Feed)
            }
        }


    }
}

@PreviewFontScale
@PreviewDynamicColors
@PreviewLightDark
@Composable
fun MemoryScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryScreen(
            onBackPress = {},
            uriList = emptyList<UriType>(),
            onEvent = {},
            state = MemoryState()
        )
    }
}





