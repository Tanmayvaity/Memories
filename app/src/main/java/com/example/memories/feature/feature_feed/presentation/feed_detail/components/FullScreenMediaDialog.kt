package com.example.memories.feature.feature_feed.presentation.feed_detail.components

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.example.memories.R
import com.example.memories.LocalTheme
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.components.LoadingIconItem
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.to

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMediaDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    uriList: List<UriType> = emptyList(),
    page: Int = 0,
    memoryTitle: String = "Summer Beach Trip",
    memoryTime: Long = System.currentTimeMillis(),
    onFavourite: () -> Unit = {},
    onDownload: (Uri, Type) -> Unit = {_,_ ->},
    onShare: (Uri) -> Unit = {},
    isDownloading: Boolean = false,
    isSharing: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    onPlayIconClick : (Uri) -> Unit = {}
) {

    val pagerState = rememberPagerState { uriList.size }
    val previewMode = LocalInspectionMode.current
    val darkTheme = LocalTheme.current
    val scrollState = rememberScrollState()


    fun onActionsClickCallback(block: (Uri, Type) -> Unit) {
        val currentUriPair : Pair<String?, Type?> = uriList[pagerState.currentPage].uri to uriList[pagerState.currentPage].type

        currentUriPair.let { (uri,type) ->
            if(uri == null || type == null) return@let
            block(uri!!.toUri(),type!!)
        }

//        currentUriPair?.let { uri,type ->
//            block(uri.toUri(),type)
//        }
    }


    LaunchedEffect(Unit) {
        pagerState.scrollToPage(page)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState),

                ) {
//                HorizontalPager(
//                    state = pagerState,
//                    modifier = Modifier.fillMaxSize()
//                ) {pager ->
//
//                    AsyncImage(
//                        model = if(previewMode) R.drawable.ic_launcher_background else uriList[pager],
//                        contentDescription = "Media item $pager",
//                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }


                MediaPager(
                    modifier = Modifier.fillMaxWidth(),
                    uris = uriList,
                    imageContentScale = contentScale,
                    pagerState = pagerState,
                    onPlayIconClick = onPlayIconClick
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = memoryTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = memoryTime.formatTime(format = "dd MMM yyyy"),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                    LoadingIconItem(
                        drawableRes = R.drawable.ic_favourite,
                        onClick = onFavourite
                    )
                    LoadingIconItem(
                        drawableRes = R.drawable.ic_download,
                        onClick = {
                            onActionsClickCallback { uri,type ->
                                onDownload(uri,type)
                            }
                        },
                        isLoading = isDownloading
                    )
                    LoadingIconItem(
                        drawableRes = R.drawable.ic_share,
                        onClick = {
                            onActionsClickCallback { uri,_ ->
                                onShare(uri)
                            }

                        },
                        isLoading = isSharing
                    )
                }

            }
        }
    }
}


@PreviewLightDark
@Composable
private fun FullScreenMediaDialogPreview() {
    val data = List(5) { UriType("", Type.IMAGE_JPG) }

    MemoriesTheme {
        FullScreenMediaDialog(
            onDismiss = {},
            onConfirm = {},
            uriList = data,
        )
    }
}