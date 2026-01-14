package com.example.memories.feature.feature_feed.presentation.feed.components

import android.R.attr.contentDescription
import android.R.attr.fontWeight
import android.R.attr.onClick
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.ContextualMenuItem
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.MediaCreationType
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme
import com.google.common.collect.Multimaps.index


@Composable
fun MemoryItemCard(
    modifier: Modifier = Modifier,
    memoryItem: MemoryWithMediaModel = MemoryWithMediaModel(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    onFavouriteButtonClick: () -> Unit = {},
    onHideButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    elevation: Int = 25,
    shape: Shape = RoundedCornerShape(8.dp),
    state: LazyListState = rememberLazyListState(),
) {

    val isPreviewModeOn = LocalInspectionMode.current
    val pager =
        rememberPagerState(pageCount = { if (isPreviewModeOn) 5 else memoryItem.mediaList.size })

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        ),
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if(memoryItem.mediaList.isNotEmpty()){
                MediaPager(
                    mediaUris = null,
                    pagerState = pager,
                    pagerHeight = 250.dp,
                    imageContentScale = ContentScale.Crop,
                    type = MediaCreationType.SHOW,
                    readOnlyMediaUriList = memoryItem.mediaList.map { it.uri }
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = memoryItem.memory.memoryForTimeStamp!!.formatTime(),
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
                    )
                    Text(
                        text = memoryItem.memory.title,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = memoryItem.memory.content,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)

                    )
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                    ) {

                        IconItem(
                            imageVector = if (memoryItem.memory.favourite)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "",
                            onClick = {
                                onFavouriteButtonClick()
                            },
                            alpha = 0f,
                            color = if (memoryItem.memory.favourite) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        IconItem(
                            drawableRes = if (memoryItem.memory.hidden)
                                R.drawable.ic_hidden else R.drawable.ic_not_hidden,
                            contentDescription = "",
                            onClick = {
                                onHideButtonClick()
                            },
                            alpha = 0f,
                            color = Color.Gray
                        )
                        IconItem(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            onClick = {
                                onDeleteButtonClick()
                            },
                            alpha = 0f,
                            color = Color.Red,
                        )
                    }


                }
            }


        }
    }
}


@Preview
@Composable
fun MemoryItemCardPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryItemCard(
            memoryItem = MemoryWithMediaModel(
                memory = MemoryModel(
                    title = "Trip to the Hills",
                    content = "A peaceful day in the mountains 🌄",
                ),
                mediaList = listOf(
                    MediaModel(
                        uri = "android.resource://com.example.memories/drawable/ic_launcher_background",
                        memoryId = ""
                    )
                )
            )
        )
    }
}