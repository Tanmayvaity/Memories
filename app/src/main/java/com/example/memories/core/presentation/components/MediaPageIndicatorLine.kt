package com.example.memories.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.example.memories.feature.feature_memory.presentation.components.MediaPageIndicator


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MediaPageIndicatorLine(
    modifier: Modifier = Modifier,
    currentPage : Int,
    pageCount : Int,
    activePageColor : Color = Color.White,
    inactivePageColor : Color = Color.White.copy(alpha = 0.5f)
) {
    Row(
        modifier
            .height(30.dp)
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) activePageColor else inactivePageColor
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)

            )
        }
    }
}

@Preview
@Composable
fun MediaPageIndicatorPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaPageIndicatorLine(
            currentPage = 2,
            pageCount = 3
        )
    }
}