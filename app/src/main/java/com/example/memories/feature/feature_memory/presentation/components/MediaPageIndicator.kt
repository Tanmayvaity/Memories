package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MediaPageIndicator(
    modifier: Modifier = Modifier,
    currentPage : Int,
    pageCount : Int,
    pageTextColor : Color = MaterialTheme.colorScheme.inverseOnSurface,
    backGroundColor : Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
) {
    Surface(
        modifier = modifier
            .padding(10.dp),
        shape = CircleShape,
        color = backGroundColor
    ) {
        Text(
            text = "${(currentPage + 1).toString()}/${pageCount}",
            color = pageTextColor,
            fontSize = 18.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Preview
@Composable
fun MediaPageIndicatorPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaPageIndicator(
            currentPage = 0,
            pageCount = 3
        )
    }
}