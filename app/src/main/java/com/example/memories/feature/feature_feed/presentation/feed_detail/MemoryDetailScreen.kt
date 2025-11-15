package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.ContentActionSheet
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.MediaPageIndicatorLine
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MemoryDetailRoot(
    modifier: Modifier = Modifier,
    viewmodel : MemoryDetailViewModel = hiltViewModel<MemoryDetailViewModel>(),
    onBack : () -> Unit ={}
) {
    val memory by viewmodel.memory.collectAsStateWithLifecycle()
    val context = LocalContext.current
//    LaunchedEffect(Unit) {
//        Log.d("MemoryDetailScreen", "MediaDetailRoot: ${memoryId}")
//        viewmodel.onEvent(MemoryDetailEvents.Fetch(id = memoryId))
//    }

    LaunchedEffect(Unit) {
        viewmodel.eventFlow.collect {event ->
            when(event){
                is UiEvent.ShowToast -> {
                    Toast.makeText(context,event.message.toString(),Toast.LENGTH_SHORT).show()
                    when(event.type){
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
        onBack =  onBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDetailScreen(
    modifier: Modifier = Modifier,
    memory : MemoryWithMediaModel? = null,
    onEvent : (MemoryDetailEvents) -> Unit = {},
    onBack : () -> Unit = {}
) {
    val previewMode = LocalInspectionMode.current
    val pagerState = rememberPagerState { if(previewMode) 1 else memory?.mediaList?.size?:0 }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    var showContentSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        ,
        topBar = {
            AppTopBar(
                scrollBehavior = scrollBehavior,
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = {
                    onBack()
                },
                showAction = true,
                actionContent = {
                    if(memory == null) return@AppTopBar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(onClick = {
                            onEvent(MemoryDetailEvents.FavoriteToggle(
                                id = memory.memory.memoryId,
                                isFavourite = memory.memory.favourite
                            ))
                        }) {
                            Icon(
                                imageVector = if(memory.memory.favourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "More options",
                                tint = if(memory.memory.favourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            showContentSheet = true
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }

//

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
            memory?.let {
                val item = memory.memory
                Box{
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()

                        ,

                    ) {page ->

                        AsyncImage(
                            model = if(LocalInspectionMode.current) R.drawable.ic_launcher_background else memory.mediaList[page].uri,
                            contentDescription = "Linked Image",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )

                    }
                    MediaPageIndicatorLine(
                        currentPage = pagerState.currentPage,
                        pageCount = pagerState.pageCount,
                        modifier = Modifier.align(Alignment.BottomCenter),
//                        backGroundColor = MaterialTheme.colorScheme.primaryContainer,
//                        pageTextColor = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
//                        .background(BlueishBlack)
                        .padding(10.dp)

                ){
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = item.title,
                            modifier = Modifier
                               ,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = item.timeStamp.formatTime(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
    }


    if(showContentSheet && memory!=null){
        val item = memory.memory
        ContentActionSheet(
            onDismiss = {
                showContentSheet = false
            },
            title = item.title,
            actionList = listOf(
                MenuItem(
                    title = "Edit",
                    icon = R.drawable.ic_edit,
                    iconContentDescription = "Edit",
                    onClick = {
                    }
                ),
                MenuItem(
                    title = if(item.hidden) "UnHide" else "Hide",
                    icon = if (item.hidden) R.drawable.ic_hidden else R.drawable.ic_not_hidden,
                    iconContentDescription = if(item.hidden) "UnHide" else "Hide",
                    onClick = {
                        onEvent(MemoryDetailEvents.HiddenToggle(item.memoryId,!item.hidden))
                    }
                ),MenuItem(
                    title = "Delete",
                    icon = R.drawable.ic_delete,
                    iconContentDescription = "Delete",
                    onClick = {
                        showDeleteDialog = true

                    }
                ),

            )

        )
    }

    if(showDeleteDialog){
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
