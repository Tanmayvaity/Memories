package com.example.memories.feature.feature_feed.presentation.tags_with_memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun MemoryCard(
    memory: MemoryWithMediaModel,
    modifier: Modifier = Modifier,
    onClick : () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable{
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 1f
            )
        )
    ) {
        Column {
            if(memory.mediaList.isNotEmpty() || LocalInspectionMode.current){
                AsyncImage(
                    model = if(LocalInspectionMode.current) R.drawable.ic_launcher_background else  memory.mediaList[0].uri, // Replace with your image loader
                    contentDescription = "Memory Image ",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = memory.memory.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = memory.memory.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = memory.memory.memoryForTimeStamp!!.formatTime(
                        format = "dd MMM YYYY"
                    ), // Replace with your date logic
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MemoryCardPreview() {
    MemoriesTheme {
        MemoryCard(
            memory = MemoryWithMediaModel(
                memory = MemoryModel(
                    title = "Party Time",
                    content = "Some Content"
                )
            ),
        )
    }
}
