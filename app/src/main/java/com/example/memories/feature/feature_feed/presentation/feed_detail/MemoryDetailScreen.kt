package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.R.attr.onClick
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.MediaPageIndicatorLine
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_memory.presentation.components.TagRow
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MemoryDetailRoot(
    modifier: Modifier = Modifier,
    viewmodel: MemoryDetailViewModel = hiltViewModel<MemoryDetailViewModel>(),
    onBack: () -> Unit = {},
    onNavigateToMemory: (AppScreen) -> Unit
) {
    val memory by viewmodel.memory.collectAsStateWithLifecycle()
    val isLoading by viewmodel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current
//    LaunchedEffect(Unit) {
//        Log.d("MemoryDetailScreen", "MediaDetailRoot: ${memoryId}")
//        viewmodel.onEvent(MemoryDetailEvents.Fetch(id = memoryId))
//    }

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

            }
        }
    }

    MemoryDetailScreen(
        memory = memory,
        onEvent = viewmodel::onEvent,
        onBack = onBack,
        onNavigateToMemory = onNavigateToMemory,
        isLoading = isLoading
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemoryDetailScreen(
    modifier: Modifier = Modifier,
    memory: MemoryWithMediaModel? = null,
    isLoading: Boolean = false,
    onEvent: (MemoryDetailEvents) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToMemory: (AppScreen) -> Unit = {}
) {
    val previewMode = LocalInspectionMode.current
    val pagerState = rememberPagerState { if (previewMode) 5 else memory?.mediaList?.size ?: 0 }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    var showContentSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Bottom,
    )
    var expandToolBar by remember { mutableStateOf(false) }

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
                MediaPager(
                    mediaUris = memory.mediaList.map { it -> it.uri },
                    pagerState = pagerState
                )

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
                            text = item.timeStamp.formatTime(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TagRow(
                            totalTags = memory.tagsList,
                            showAdd = false,
                            onAddClick = {},
                            modifier = Modifier.padding(top = 5.dp)
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
        if (!isLoading) {
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
    if (showDeleteDialog) {
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
                        showContentSheet = false
                        onEvent(MemoryDetailEvents.Delete)
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


}

@PreviewLightDark
@Preview
@Composable
fun MemoryDetailScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryDetailScreen(
            memory = MemoryWithMediaModel()
        )
    }
}
