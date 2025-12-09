package com.example.memories.feature.feature_feed.presentation.search.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MemoryItemForCategory(
    item: MemoryWithMediaModel,       // your model
    onClick: (String) -> Unit,          // memoryId callback
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable {
                onClick(item.memory.memoryId)
            }
    ) {
        AsyncImage(
            model = if (LocalInspectionMode.current)
                R.drawable.ic_launcher_background
            else
                item.mediaList.first().uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(28.dp)),
            error = painterResource(R.drawable.ic_launcher_background),
            placeholder = painterResource(R.drawable.ic_launcher_background),
        )
        Text(
            text = item.memory.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@PreviewLightDark
@Preview
@Composable
fun MemoryItemForCategoryPreview(){
    MemoriesTheme {
        MemoryItemForCategory(
            item = MemoryWithMediaModel(),
            onClick = {}
        )
    }
}
