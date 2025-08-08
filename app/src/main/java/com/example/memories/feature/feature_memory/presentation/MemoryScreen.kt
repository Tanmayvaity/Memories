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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_memory.presentation.components.CustomTextField
import com.example.memories.feature.feature_memory.presentation.components.MediaViewer
import com.example.memories.feature.feature_memory.presentation.components.ReminderDatePickerDialog
import com.example.memories.feature.feature_memory.presentation.components.ReminderPickerButton
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
    uriType: UriType
) {
    val state by viewModel.memoryState.collectAsStateWithLifecycle()


    MemoryScreen(
        onBackPress = onBackPress,
        uriType = uriType,
        onEvent = viewModel::onEvent,
        state = state,
        onCreateClick = onGoToHomeScreen,
        errorFLow = viewModel.errorFlow,
        successFlow = viewModel.successFlow
    )




}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onBackPress: () -> Unit = {},
    uriType: UriType,
    onEvent: (MemoryEvents) -> Unit = {},
    state: MemoryState,
    onCreateClick : (TopLevelScreen.Feed) -> Unit = {},
    errorFLow : Flow<String>? = null,
    successFlow : Flow<String>? = null
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val titleInteractionSource = remember { MutableInteractionSource() }
    var showDatePicker by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = {
        1
    })


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
                        style = MaterialTheme.typography.titleLarge
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
                            Log.i("MemoryScreen", "MemoryScreen: ${uriType == null}")
                            onEvent(
                                MemoryEvents.CreateMemory(
                                    uriList = listOf(uriType!!),
                                    title = state.title,
                                    content = state.content
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Create",
                            color = MaterialTheme.colorScheme.primary
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
                    uriType = uriType,
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
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 15.dp, start = 10.dp, end = 10.dp),
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
                    fontSize =  32.sp

                )
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 10.dp),
                    isHintVisible = state.isContentHintVisible,
                    hintContent = state.contentHintContent,
                    content = state.content,
                    onValueChange = {it ->
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

@PreviewLightDark
@Preview
@Composable
fun MemoryScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryScreen(
            onBackPress = {},
            uriType = UriType(),
            onEvent = {},
            state = MemoryState()
        )
    }
}





