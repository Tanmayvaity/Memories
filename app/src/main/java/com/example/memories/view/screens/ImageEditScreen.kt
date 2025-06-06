package com.example.memories.view.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
fun ImageEditScreen(
    uri : String,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AsyncImage(
            model = uri,
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize(),
        )
    }



}