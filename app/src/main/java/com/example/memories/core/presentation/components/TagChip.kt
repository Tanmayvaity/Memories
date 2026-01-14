package com.example.memories.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.TagModel
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun TagChip(
    tag: TagModel,
    modifier: Modifier = Modifier,
    selected : Boolean = true,
    showBorder : Boolean = false,
    onClick: (TagModel) -> Unit,
) {
    FilterChip(
        onClick = { onClick(tag) },
        selected = selected,
        shape = RoundedCornerShape(20.dp),
        border = if(showBorder) BorderStroke(width = 1.dp,color = MaterialTheme.colorScheme.primaryContainer) else null,
        modifier = modifier
            .wrapContentHeight()
            .wrapContentWidth(align = Alignment.Start),
//            .weight(1f),
//        colors = FilterChipDefaults.filterChipColors(
//            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
//        ),
        label = {
            Text(
                modifier = Modifier.padding(10.dp),
                text = tag.label,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    )
}



@PreviewDynamicColors
@PreviewLightDark
@PreviewFontScale
@Composable
fun TagChipPreview() {
    MemoriesTheme {
        TagChip(tag = TagModel(label = "Memory"), onClick = {}, showBorder = true)
    }
}