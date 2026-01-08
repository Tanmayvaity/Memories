package com.example.memories.core.presentation.components

import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.ItemSnapshotList
import coil3.compose.AsyncImage
import com.airbnb.lottie.L
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

enum class MediaCreationType {
    SHOW,
    EDIT
}


@Composable
fun MediaPager(
    mediaUris: SnapshotStateList<String?>?,
    modifier: Modifier = Modifier,
    pagerHeight: Dp = 300.dp,
    imageContentScale: ContentScale = ContentScale.FillWidth,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 }
    ),
    type: MediaCreationType = MediaCreationType.EDIT,
    readOnlyMediaUriList : List<String> = emptyList(),
    onAddMediaClick : () -> Unit = {},
    onRemoveMediaClick : () -> Unit = {}
) {

    Box(
        modifier = modifier
            .height(pagerHeight)
            .fillMaxWidth()
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val media = mediaUris?.getOrNull(page)
            AnimatedVisibility(type == MediaCreationType.EDIT && media == null && mediaUris!=null && mediaUris.size > page) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    IconItem(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        alpha = 0.3f,
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onAddMediaClick()
                        }
                    )
                }
                return@AnimatedVisibility
            }

            AnimatedVisibility((readOnlyMediaUriList.isNotEmpty() && type == MediaCreationType.SHOW) || (media != null && mediaUris!=null && mediaUris!!.size > page || LocalInspectionMode.current )){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    AsyncImage(
                        model = if (LocalInspectionMode.current)
                            R.drawable.ic_launcher_background
                        else if(type == MediaCreationType.EDIT) {
                            mediaUris!![page]
                        }else{
                            readOnlyMediaUriList[page]
                        },
//                model = R.drawable.ic_launcher_background,
                        contentDescription = "Media item $page",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = imageContentScale
                    )
                    if(type == MediaCreationType.EDIT){
                        IconItem(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                            alpha = 0.3f,
                            onClick = { onRemoveMediaClick()},
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
                return@AnimatedVisibility

            }


        }
        MediaPageIndicatorLine(
            currentPage = pagerState.currentPage,
            pageCount = pagerState.pageCount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }
}

@Preview
@Composable
fun MediaPagerPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaPager(
            mediaUris = SnapshotStateList<String?>().apply {
                repeat(5) {
                    add("")
                }
            },

        )
    }
}