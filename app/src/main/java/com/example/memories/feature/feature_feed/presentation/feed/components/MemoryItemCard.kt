package com.example.memories.feature.feature_feed.presentation.feed.components

import android.R.attr.contentDescription
import android.R.attr.fontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MemoryItemCard(
    modifier: Modifier = Modifier,
    memoryItem: MemoryWithMediaModel = MemoryWithMediaModel(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    onOverflowButtonClick: () -> Unit = {},
    onFavouriteButtonClick: () -> Unit = {},
    elevation : Int = 25,
    shape : Shape = RoundedCornerShape(8.dp)
) {

    val isPreviewModeOn = LocalInspectionMode.current
    val pager = rememberPagerState(pageCount = { if(isPreviewModeOn) 1 else memoryItem.mediaList.size })

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        ),
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onOverflowButtonClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,

            ) {
            HorizontalPager(state = pager) { page ->
                AsyncImage(
                    model = if (LocalInspectionMode.current) R.drawable.ic_launcher_background else memoryItem.mediaList[page].uri,
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                    ,
                    contentScale = ContentScale.FillWidth
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = memoryItem.memory.timeStamp.formatTime(),
                        modifier = Modifier
                            .padding(start = 10.dp , top = 5.dp,end = 5.dp,bottom = 5.dp)
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
                    IconButton(
                        onClick = {
                            onFavouriteButtonClick()
                        }
                    ) {
                        Icon(
                            imageVector =
                                if (memoryItem.memory.favourite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                            contentDescription = "Favourite icon",
                            tint = if(memoryItem.memory.favourite) Color.Red else Color.Gray
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
                    MediaModel(uri = "android.resource://com.example.memories/drawable/ic_launcher_background", memoryId = "")
                )
            )
        )
    }
}