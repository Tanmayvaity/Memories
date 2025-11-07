package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MediaActionBar(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEditClick: () -> Unit,
    backgroundColor : Color = MaterialTheme.colorScheme.background,
    iconColor : Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Min)
            .padding(20.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
    ) {
        Column {
            IconItem(
                modifier = Modifier.padding(5.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                alpha = 0.1f,
                onClick = onNextClick,
                color = iconColor
            )
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = R.drawable.ic_download,
                contentDescription = "Download",
                alpha = 0.1f,
                onClick = onDownloadClick,
                color = iconColor
            )
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = R.drawable.ic_edit,
                contentDescription = "Edit",
                alpha = 0.1f,
                onClick = onEditClick,
                color = iconColor
            )
        }
    }
}

@Preview
@Composable
fun MediaActionBarPreview(){
    MemoriesTheme {
        MediaActionBar(
            onNextClick = {},
            onDownloadClick = {},
            onEditClick = {},
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            iconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}