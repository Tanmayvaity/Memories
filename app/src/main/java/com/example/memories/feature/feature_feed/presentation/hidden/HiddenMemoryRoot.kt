package com.example.memories.feature.feature_feed.presentation.hidden

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HiddenMemoryRoot(modifier: Modifier = Modifier) {
    HiddenMemoryScreen()
}

@Composable
fun HiddenMemoryScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = "Hidden Memory"
        )
    }
}