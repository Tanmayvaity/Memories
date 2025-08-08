package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.EditModalBottomSheet
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components.MediaPreview
import com.example.memories.navigation.AppScreen
import kotlinx.coroutines.launch


const val TAG = "MediaEditScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEditScreen(
    uriType: UriType? = null,
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
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        MediaPreview(
            onBackPress = {
                onBackPress()
            },
            onEditItemClick = {
                showEditBottomSheet = true
            },
            bitmap = bitmapState.bitmap,
            onDownloadClick = {
                if(uriType!!.type == Type.IMAGE){
                    viewModel.onEvent(MediaEvents.DownloadBitmap(bitmapState.bitmap!!))
                    return@MediaPreview
                }
                if(uriType!!.type == Type.VIDEO){
                    viewModel.onEvent(MediaEvents.DownloadVideo(uriType.uri!!.toUri()))
                    return@MediaPreview
                }
//
            },
            onNextClick = {
                if(uriType!!.type == Type.IMAGE){
                    viewModel.onEvent(MediaEvents.BitmapToUri)
                }

                if(uriType.type == Type.VIDEO){
                    onNextClick(AppScreen.Memory(uriType))
                }

            },
            modifier = Modifier.padding(innerPadding),
            uriType = uriType
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
        if(uriType!!.type == Type.IMAGE){
            viewModel.onEvent(MediaEvents.UriToBitmap(uriType!!.uri!!.toUri()))
        }
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
                onNextClick(AppScreen.Memory(UriType(
                    uri = it.toString(),
                    type = it.mapToType()
                )))
            }
        }
    }


}

