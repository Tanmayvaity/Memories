package com.example.memories.feature.feature_memory.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.R
import com.example.memories.core.presentation.Type
import com.example.memories.core.presentation.UriType
import com.example.memories.core.util.isImageFile
import com.example.memories.core.util.isVideoFile
import com.example.memories.navigation.TopLevelScreen

@androidx.annotation.OptIn(UnstableApi::class)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_9_pro_xl"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onBackPress: () -> Unit = {},
    onCreateClick: (TopLevelScreen.Feed) -> Unit = {},
    uriType: UriType? = null
) {
    val viewModel: MemoryViewModel = viewModel()
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
                            onCreateClick(TopLevelScreen.Feed)
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

        val state by viewModel.memoryState.collectAsStateWithLifecycle()


        val scrollState = rememberScrollState()
        var titleHint by remember { mutableStateOf("Write Title") }
        val contentHint = "Write your story"

        val titleInteractionSource = remember { MutableInteractionSource() }
        var showDatePicker by remember { mutableStateOf(false) }

        val pagerState = rememberPagerState(pageCount = {
            6
        })


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

                Box {
                    HorizontalPager(
                        state = pagerState
                    ) {
//                        AsyncImage(
//                            model = uri,
//                            contentDescription = "",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                            ,
//                            contentScale = ContentScale.Crop
//                        )

                        if (uriType!!.type == Type.IMAGE) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uriType.uri)
                                    .crossfade(true)
                                    .build(),
                                error = painterResource(R.drawable.ic_launcher_background),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                        if (uriType!!.type == Type.VIDEO) {
                            val player = ExoPlayer.Builder(LocalContext.current).build().apply {
                                val mediaItem = MediaItem.fromUri(uriType.uri!!.toUri())
                                setMediaItem(mediaItem)
                                playWhenReady = false
                                prepare()
                            }

                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).also {
                                        it.player = player
                                        it.useController = true
                                        it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    }
                                },
                                update = {
                                    when (lifecycle) {
                                        Lifecycle.Event.ON_PAUSE -> {
                                            it.onPause()
                                            it.player?.pause()
                                        }

                                        Lifecycle.Event.ON_RESUME -> {
                                            it.onResume()
                                        }

                                        Lifecycle.Event.ON_DESTROY -> {
                                            it.player?.release()
                                        }

                                        else -> Unit
                                    }
                                },
//                                modifier = Modifier.fillMaxWidth()
//                                    .height(250.dp)


                            )
                        }


                    }
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "${(pagerState.currentPage + 1).toString()}/${pagerState.pageCount}",
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }


                }


                Button(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Set a reminder"
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = ""
                        )
                    }
                }


                BasicTextField(
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    value = if (state.isTitleHintVisible) state.titleHintContent else state.title,
                    onValueChange = { it: String ->
                        viewModel.onEvent(MemoryEvents.TitleChanged(it))
                    },
                    interactionSource = titleInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 15.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                        .onFocusChanged {
                            viewModel.onEvent(MemoryEvents.TitleFocusChanged(it))
                        },
                    textStyle = TextStyle(
                        color = if (state.isTitleHintVisible) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                        else MaterialTheme.colorScheme.onSurface,
                        textMotion = TextMotion.Animated,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,

                        )
                )
                BasicTextField(
                    value = if (state.isContentHintVisible) state.contentHintContent else state.content,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    onValueChange = { it: String ->
                        viewModel.onEvent(MemoryEvents.ContentChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                        .onFocusChanged {
                            viewModel.onEvent(MemoryEvents.ContentFocusChanged(it))
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    textStyle = TextStyle(
                        color = if (state.isContentHintVisible) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                        else MaterialTheme.colorScheme.onSurface,
                        textMotion = TextMotion.Animated,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                    ),
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

    }
}


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    state: DatePickerState = rememberDatePickerState()
) {
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(
                    text = "Confirm",
//                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = "Cancel",
                )
            }
        }


    ) {
        DatePicker(
//            colors = DatePickerDefaults.colors(
//                containerColor = Color.White,
//                selectedDayContainerColor = Color.Black,
//                selectedYearContainerColor = Color.Black,
//                dividerColor = Color.Black
//            ),
            state = state
        )
    }
}

@Preview
@Composable
fun ImageContainer(
    modifier: Modifier = Modifier,
    uri: Uri? = null
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
//        Image(
//            painter = painterResource(com.example.memories.R.drawable.ic_launcher_background),
//            contentDescription = "",
//            contentScale = ContentScale.Crop,
//        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(125.dp)
                .clip(RoundedCornerShape(8.dp))
        )


    }
}