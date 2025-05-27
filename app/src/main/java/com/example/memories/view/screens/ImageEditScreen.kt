package com.example.memories.view.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.memories.viewmodel.CameraScreenViewModel
import java.util.concurrent.CancellationException

@Composable
fun ImageEditScreen(
    viewModel: CameraScreenViewModel,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val tempImageUri by viewModel.successfullImageCapture.collectAsStateWithLifecycle()


        AsyncImage(
            model = tempImageUri,
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize(),
        )
    }

    BackHandler(
        enabled = true
    ) {
        viewModel.resetUriState()
        viewModel.resetErrorState()
        onBack()

    }


}