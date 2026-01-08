package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.memories.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.MediaCreationType
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.mapContentUriToType
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_feed.presentation.feed.MemoryEntryMode
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.EditModalBottomSheet
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.MediaPreview
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.contracts.contract

const val TAG = "MediaEditScreen"
typealias MediaUri = String?

@Composable
fun MediaEditRoot(
    modifier: Modifier = Modifier,
    viewmodel : MediaViewModel = hiltViewModel(),
    onBackPress: () -> Unit= {},
    onNextClick: (AppScreen.Memory) -> Unit = {},
) {
//    val state by viewmodel.state.collectAsStateWithLifecycle()
    MediaEditScreen(
        onEvent = viewmodel::onEvent,
        onBackPress = onBackPress,
        onNextClick = onNextClick,
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEditScreen(
    onEvent : (MediaEvents) -> Unit = {},
    onBackPress: () -> Unit = {},
    onNextClick: (AppScreen.Memory) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mediaEditState =  rememberEditorState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 }
    )

    val mediaUriList =  rememberSaveable { mutableStateListOf<MediaUri>().apply {
        repeat(5){
            add(null)
        }
    } }

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->


        if(uri!=null){
            mediaUriList[pagerState.currentPage] = uri.toString()
        }


    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            AppTopBar(
                showNavigationIcon = true,
                onNavigationIconClick = {
                    onBackPress()
                },
                title = {
                    Text(
                        text = "Edit"
                    )
                },
                showDivider = false,
                showAction = true,
                actionContent = {
                    Text(
                        text = "Next",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                EditTool.entries.forEach { editTool ->
                    NavigationBarItem(
                        selected = mediaEditState.activeTool == editTool,
                        onClick = {
                            mediaEditState.activeTool = editTool
                        },
                        icon = {
                            Icon(
                                painter = painterResource(editTool.icon),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            MediaPager(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
//                    .dropShadow(
//                        shape = RoundedCornerShape(16.dp),
//                        shadow = Shadow(
//                            radius = 10.dp,
//                            spread = 3.dp,
//                            color = MaterialTheme.colorScheme.surface,
//                            offset = DpOffset(x = 4.dp,y = 4.dp)
//                        )
//                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ,
                mediaUris = mediaUriList,
                imageContentScale = ContentScale.Fit,
                type = MediaCreationType.EDIT,
                pagerState = pagerState,
                onAddMediaClick = {
                    mediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveMediaClick = {
                    mediaUriList[pagerState.currentPage] = null
                }
            )
        }
    }
    if (mediaEditState.activeTool == EditTool.MORE) {
        EditModalBottomSheet(
            onDismiss = {
               mediaEditState.revertToPreviousTool()
            },
            sheetState = sheetState,
            sheetTitle = "More Options",
            items = listOf(
                MenuItem(
                    title = "Save to Device",
                    icon = R.drawable.ic_download,
                    content = "Save a copy to your local gallery" ,
                    onClick = {
                        mediaEditState.revertToPreviousTool()
                    }
                ),
                MenuItem(
                    title = "Share to Social",
                    icon = R.drawable.ic_share,
                    content = "Share this with your friends without saving to your gallery" ,
                    onClick = {
                        mediaEditState.revertToPreviousTool()
                    }
                )
            )
        )
    }
}


@Preview
@Composable
fun MediaEditScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaEditScreen()
    }
}

