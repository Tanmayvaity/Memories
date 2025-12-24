package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.TagChip
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun TagRow(
    totalTags: List<TagModel>,
    showAdd: Boolean,
    onAddClick: () -> Unit= {},
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableStateOf(0) }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .fillMaxWidth()

    ) {

        // ----- TAG CHIPS -----
        repeat(totalTags.size) { index ->
//            FilterChip(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .wrapContentWidth()
//                    .height(48.dp)
//                ,
//                shape = RoundedCornerShape(20.dp),
//                selected = false,
//                border = null,
//                label = {
//                    Text(
//                        text = totalTags[index].label,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                },
//                onClick = {},
//                colors = FilterChipDefaults.filterChipColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                )
//            )
            TagChip(
                tag = totalTags[index],
                modifier = Modifier,
                selected = true
            ) {
                selected = index
            }
        }

        // ----- CONDITIONAL ADD TAG BUTTON -----
        if (showAdd) {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(25.dp)
                )

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Tags"
                    )
                    Text(
                        text = "Add Tags",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@PreviewDynamicColors
@Composable
fun TagRowPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        TagRow(
            totalTags = listOf("Memory", "Tag", "Example","Cyberpunk 2077","Cyberpunk 2077","Cyberpunk 2077").map { TagModel(label = it.toString()) },
            showAdd = true,
            onAddClick = {  }
        )

    }
}
