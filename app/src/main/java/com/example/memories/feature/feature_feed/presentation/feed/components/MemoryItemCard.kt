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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
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
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.ContextualMenuItem
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.ui.theme.VeryLightGray
import com.google.common.collect.Multimaps.index


@Composable
fun MemoryItemCard(
    modifier: Modifier = Modifier,
    memoryItem: MemoryWithMediaModel = MemoryWithMediaModel(),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit = {},
    onFavouriteButtonClick: () -> Unit = {},
    onHideButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    elevation: Int = 0,
    shape: Shape = RoundedCornerShape(16.dp),
) {

    val isPreviewModeOn = LocalInspectionMode.current

    val pager = if (memoryItem.mediaList.isNotEmpty()) {
        rememberPagerState(pageCount = { if (isPreviewModeOn) 5 else memoryItem.mediaList.size })
    } else null

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
            if (pager != null) {
                MediaPager(
                    uris = memoryItem.mediaList.map { UriType(it.uri, it.type) },
                    pagerState = pager,
                    pagerHeight = 250.dp,
                    imageContentScale = ContentScale.Crop,
                    playVideoCapability = false
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = memoryItem.memory.memoryForTimeStamp!!.formatTime(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = memoryItem.memory.title,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = memoryItem.memory.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        lineHeight = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp)

                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    IconItem(
                        imageVector = if (memoryItem.memory.favourite)
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "",
                        onClick = {
                            onFavouriteButtonClick()
                        },
                        iconSize = 22.dp,
                        alpha = 0f,
                        color = if (memoryItem.memory.favourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconItem(
                        drawableRes = if (memoryItem.memory.hidden)
                            R.drawable.ic_hidden else R.drawable.ic_not_hidden,
                        contentDescription = "",
                        onClick = {
                            onHideButtonClick()
                        },
                        iconSize = 22.dp,
                        alpha = 0f,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconItem(
                        iconSize = 22.dp,
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "",
                        onClick = {
                            onDeleteButtonClick()
                        },
                        alpha = 0f,
                        color = MaterialTheme.colorScheme.error.copy(0.6f),
                    )
                }


            }
//            }


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
                    memoryForTimeStamp = 0L
                ),
                mediaList = listOf(
                    MediaModel(
                        uri = "android.resource://com.example.memories/drawable/ic_launcher_background",
                        memoryId = "",
                    )
                )
            )
        )
    }
}