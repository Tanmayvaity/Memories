package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_feed.presentation.feed.components.OnThisDayCard
import com.example.memories.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnThisDaySection(
    onThisDayMemories: List<MemoryWithMediaModel>,
    carouselState: CarouselState,
    screenWidth: Dp,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            HeadingText(
                title = "On This Day",
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.headlineSmall
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "${carouselState.currentItem + 1}/${onThisDayMemories.size}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalMultiBrowseCarousel(
            state = carouselState,
            preferredItemWidth = screenWidth,
            itemSpacing = 5.dp,
            modifier = Modifier.height(250.dp)
        ) { index ->
            val memory = onThisDayMemories[index]
            OnThisDayCarouselItem(
                memory = memory,
                onClick = { onMemoryClick(memory.memory.memoryId) }
            )
        }
    }
}

@Composable
private fun OnThisDayCarouselItem(
    memory: MemoryWithMediaModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val media = memory.mediaList.firstOrNull()
    val uri = media?.uri
    val type = media?.type

    if (uri != null && type != null && type.isImageFile()) {
        Box(modifier = modifier.fillMaxSize()) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClick)
            )
            Text(
                text = memory.memory.memoryForTimeStamp!!.formatTime("dd MMM yyyy"),
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f,
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    } else {
        OnThisDayCard(
            time = memory.memory.memoryForTimeStamp!!,
            title = memory.memory.title,
            onClick = onClick
        )
    }
}