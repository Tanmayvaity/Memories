package com.example.memories.feature.feature_feed.presentation.feed_detail.components

import android.annotation.SuppressLint
import androidx.camera.viewfinder.core.impl.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.memories.R
import coil3.compose.AsyncImage
import com.example.memories.LocalTheme
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIconItem
import com.example.memories.core.presentation.components.MediaPageIndicatorLine
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme
import okhttp3.internal.format
import java.time.LocalTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    uriList : List<String> = emptyList(),
    page : Int= 0,
    memoryTitle : String = "Summer Beach Trip",
    memoryTime : Long = System.currentTimeMillis(),
    onFavourite : () -> Unit = {},
    onDownload : () -> Unit = {},
    onShare : () -> Unit = {},
    isDownloading : Boolean = false,
    isSharing : Boolean = false
) {

    val pagerState = rememberPagerState { uriList.size }
    val previewMode = LocalInspectionMode.current
    val darkTheme = LocalTheme.current
    val scrollState = rememberScrollState()

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
        ) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                ,
            ){
                HorizontalPager(
                    state = pagerState,

                ) {pager ->
                    AsyncImage(
                        model = if(previewMode) R.drawable.ic_launcher_background else uriList[pager],
                        contentDescription = "Media item $pager",
                        contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(
//                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                ) {
                    MediaPageIndicatorLine(
                        currentPage = pagerState.currentPage,
                        pageCount = uriList.size,
                        activePageColor = MaterialTheme.colorScheme.onBackground,
                        inactivePageColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
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
                            onClick = onDownload,
                            isLoading = isDownloading
                        )
                        LoadingIconItem(
                            drawableRes = R.drawable.ic_share,
                            onClick = onShare,
                            isLoading = isSharing
                        )
                    }
                }



            }
        }
    }
}


@PreviewLightDark
@Composable
private fun FullScreenImageDialogPreview() {
    MemoriesTheme {
        FullScreenImageDialog(
            onDismiss = {},
            onConfirm = {},
            uriList = listOf("",""),
        )
    }
}