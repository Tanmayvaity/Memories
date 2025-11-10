package com.example.memories.feature.feature_feed.presentation.feed.components

import android.R
import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.toUri
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_memory.presentation.components.ImageContainer
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun MemoryItem(
    modifier: Modifier = Modifier,
    memoryItem: MemoryWithMediaModel = MemoryWithMediaModel(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
) {
    val imageUri = memoryItem.mediaList.firstOrNull()?.uri
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    ImageContainer(
                        modifier = Modifier.padding(5.dp),
                        uri = imageUri?.toUri(),
                        size = 75
                    )
//                IconButton(
//                    modifier = Modifier.align(Alignment.BottomEnd),
//                    onClick = {
//                        onFavouriteButtonClick()
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(
//                            if(memoryItem.memory.favourite){
//                                com.example.memories.R.drawable.ic_favourite_filled
//                            }else{
//                                com.example.memories.R.drawable.ic_favourite
//                            }
//
//                        ),
//                        contentDescription = "Favourite icon",
//                        tint = MaterialTheme.colorScheme.inverseOnSurface
//                    )
//                }
                }


                Column(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),

                    ) {

                    Text(
                        text = memoryItem.memory.title,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = memoryItem.memory.content,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = memoryItem.memory.timeStamp.formatTime(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
//            IconButton(
//                modifier = Modifier.align(Alignment.Top),
//                onClick = {
//                    onOverflowButtonClick()
//                }
//
//            ) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = "More options"
//                )
//            }


            }

        }
    }

}

@Preview
@Composable
fun MemoryItemPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemoryItem()
    }
}