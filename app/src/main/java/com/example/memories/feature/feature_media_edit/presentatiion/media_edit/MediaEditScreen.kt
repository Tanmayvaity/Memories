package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.EditModalBottomSheet
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.ImagePreview
import com.example.memories.navigation.AppScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEditScreen(
    uri: String,
    onBackPress: () -> Unit = {},
    onNextClick: (AppScreen.Memory) -> Unit = {}
) {
    val viewModel: MediaViewModel = hiltViewModel<MediaViewModel>()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val bitmapState by viewModel.bitmapState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        ImagePreview(
            onBackPress = {
                onBackPress()
            },
            onEditItemClick = {
                showEditBottomSheet = true
            },
            bitmap = bitmapState.bitmap,
            onDownloadClick = {
                viewModel.onEvent(MediaEvents.DownloadBitmap(bitmapState.bitmap!!))
            },
            onNextClick = {
                viewModel.onEvent(MediaEvents.BitmapToUri)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }




    if (showEditBottomSheet) {
        EditModalBottomSheet(
            onDismiss = {
                showEditBottomSheet = false
            },
            sheetState = sheetState
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(MediaEvents.UriToBitmap(uri.toUri()))
    }

    LaunchedEffect(Unit) {
        viewModel.downloadErrorFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
        viewModel.downloadSuccessFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }


    }

    LaunchedEffect(Unit) {
        viewModel.saveBitmapToInternalSuccessFlow.collect { uri ->
            Log.d("MediaEditScreen", "outside uri : ${uri.toString()}")
            uri?.let { it ->
                Log.d("MediaEditScreen", "inside uri : ${it.toString()}")
                onNextClick(AppScreen.Memory(it.toString()))
            }
        }
    }


}

