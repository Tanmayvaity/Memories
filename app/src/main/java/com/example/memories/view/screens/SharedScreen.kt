package com.example.memories.view.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.memories.view.components.CameraRationaleDialog
import com.example.memories.view.utils.isPermissionGranted
import com.example.memories.viewmodel.SharedScreenViewModel


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SharedScreen(
    modifier: Modifier = Modifier,
    onArrowBackButtonClick : () -> Unit = {}
) {
    var loadMediaFromOtherApps by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: SharedScreenViewModel = viewModel()
    var showRationale by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { 2 }
    var showProgressBar by remember { mutableStateOf(false) }
    val fetchFromThisApp by viewModel.fetchFromThisApp.collectAsStateWithLifecycle()
    val requestMediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.any{it.value}
        if(granted){
            viewModel.fetchMediaFromShared(context)
        }else{
            showRationale = true
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shared"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackButtonClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to previous screen"
                        )
                    }
                },
                actions = {
                    Switch(
                        checked = !fetchFromThisApp,
                        onCheckedChange = { it ->
                            viewModel.clearData()
                            viewModel.toggleFromAppState()
                        },
                        modifier = Modifier.padding(10.dp),

                        )
                }
            )
        }
    ) { innerPadding ->

        val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
//        val appImages by viewModel.appMediaImages.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {


            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                items(mediaState.data) { media ->
                    AsyncImage(
                        model = media.uri,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .fillMaxWidth()
                            .wrapContentHeight()

                    )

                }
            }

            if (showProgressBar) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    color = Color.Black,
                    strokeWidth = 3.dp
                )
            }



//            LaunchedEffect(fetchFromThisApp) {
//                if (!fetchFromThisApp) {
//                    runtimeRequestLogic(
//                        context,
//                        requestMediaPermissionLauncher,
//                        onRationale = {
//                            showRationale = true
//                        },
//                        onPermissionGranted = {
//                            viewModel.fetchMediaFromShared(context = context)
//                        }
//                    )
//                }
//
//                if(fetchFromThisApp){
//                    viewModel.fetchMediaFromShared(context = context)
//                }
//
//
//            }

            LaunchedEffect(mediaState) {
                showProgressBar = mediaState.isLoading
            }

            DisposableEffect(fetchFromThisApp) {
                val observer = object : LifecycleEventObserver {
                    override fun onStateChanged(
                        source: LifecycleOwner,
                        event: Lifecycle.Event
                    ) {
                        if (event == Lifecycle.Event.ON_RESUME) {
                            if (!fetchFromThisApp) {
                                runtimeRequestLogic(
                                    context,
                                    requestMediaPermissionLauncher,
                                    onRationale = {
                                        showRationale = true
                                    },
                                    onPermissionGranted = {
                                        viewModel.fetchMediaFromShared(
                                            context = context,
                                        )
                                    }
                                )
                            } else {
                                viewModel.fetchMediaFromShared(context = context)
                            }
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }


            if (showRationale) {
                CameraRationaleDialog(
                    title = "Media permission has not been granted",
                    text = "you cannot view any shared media without this permission. " +
                            "Go to settings and grant Media permission permission",
                    onDismissRequest = {
//                        viewModel.fetchMediaFromShared(context)
                        showRationale = false
                    },
                    onConfirm = {
                        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        settingsIntent.data = uri
                        context.startActivity(settingsIntent)
                    }
                )
            }


        }


    }

}

fun runtimeRequestLogic(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    onPermissionGranted: () -> Unit = {},
    onRationale: () -> Unit = {}
) {

    val activity = context as Activity

    when {
        isPermissionGranted(
            context,
            READ_MEDIA_VISUAL_USER_SELECTED
        ) || isPermissionGranted(context, READ_MEDIA_IMAGES)
                || isPermissionGranted(context, READ_EXTERNAL_STORAGE) -> {
            // show data
            onPermissionGranted()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) -> {
            // show rationale
            onRationale()

        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            launcher.launch(
                arrayOf(
                    READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            launcher.launch(arrayOf(READ_MEDIA_IMAGES))
        }

        else -> {
            launcher.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

    }
}

