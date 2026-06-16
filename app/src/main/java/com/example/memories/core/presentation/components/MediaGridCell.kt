package com.example.memories.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.R
import com.example.memories.core.util.PlayButton

@Composable
fun MediaGridCell(
    url: String,
    isVideo : Boolean = false,
    onClick : () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(if (LocalInspectionMode.current) R.drawable.ic_launcher_background else url)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_launcher_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().aspectRatio(1f)
        )
        if(isVideo){
            PlayButton(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}