package com.example.memories.view.screens


import android.Manifest
import com.example.memories.R
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.memories.view.utils.PermissionUtil
import com.example.memories.view.utils.isPermissionGranted
import com.example.memories.viewmodel.CameraScreenViewModel
import kotlinx.coroutines.delay
import java.util.UUID


@Composable
fun CameraScreen(
    popBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraViewModel = CameraScreenViewModel()
    var cameraPermissionStatus by remember {
        mutableStateOf(
            isPermissionGranted(
                context,
                Manifest.permission.CAMERA
            )
        )
    }
    var showRationale by remember { mutableStateOf(false) }
    val cameraRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // open camera
            cameraPermissionStatus = true
        } else {
            showRationale = true
            cameraPermissionStatus = false
        }
    }

    DisposableEffect(Unit) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    PermissionUtil.handlePermission(
                        permission = Manifest.permission.CAMERA,
                        context = context,
                        object : PermissionUtil.PermissionAskListener {
                            override fun onGranted() {
                                showRationale = false
                                cameraPermissionStatus = true
                            }

                            override fun onRequest() {
                                cameraRequestLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }

                            override fun onRationale() {
                                showRationale = true
                            }

                        }
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showRationale) {
        CameraRationaleDialog {
            showRationale = false
            popBack()
        }
    }

    if (cameraPermissionStatus) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.secondary,
//        ) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "permission has been granted"
//                )
//            }
//        }


        CameraPreviewContent(
            modifier = Modifier.fillMaxSize(),
            viewModel = cameraViewModel,
            lifecycleOwner = lifecycleOwner
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: CameraScreenViewModel,
    lifecycleOwner: LifecycleOwner
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val lensFacing by viewModel.lensFacing.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    val zoomScale by viewModel.zoomScale.collectAsStateWithLifecycle()
    val exposureValue by viewModel.exposureValue.collectAsStateWithLifecycle()
    val torchState by viewModel.torchState.collectAsStateWithLifecycle()

    var showExposureBottomSheet by remember { mutableStateOf(false) }
    var showTorchBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(lensFacing) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.bindToCamera(
                context.applicationContext,
                lifecycleOwner
            )
        }

    }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified) }

    val autofocusRequestId = autofocusRequest.first
    // Show the autofocus indicator if the offset is specified
    val showAutofocusIndicator = autofocusRequest.second.isSpecified
    // Cache the initial coords for each autofocus request
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    // Queue hiding the request for each unique autofocus tap
    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(1000L)
            // Clear the offset to finish the request and hide the indicator
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }

    surfaceRequest?.let { request ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { tapCoords ->
                                Log.d("CameraScreen", "Double Tap Detected")
                                viewModel.toggleCamera()
                            },
                            onTap = { tapCoords ->
                                with(coordinateTransformer) {
                                    viewModel.tapToFocus(tapCoords.transform())
                                }
                                autofocusRequest = UUID.randomUUID() to tapCoords
                            },
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            val scale = (zoomScale + (zoom - 1f)).coerceIn(0f, 1f)

                            viewModel.zoom(scale)
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .align(Alignment.TopCenter)
                    .pointerInput(Unit) {}
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconItem(
                        drawableRes = R.drawable.ic_exposure,
                        contentDescription = "Change Camera Exposure",
                        onClick = {
                            showExposureBottomSheet = true
                        }
                    )
                    IconItem(
                        drawableRes = if(torchState) R.drawable.ic_torch_off else R.drawable.ic_torch_on,
                        contentDescription = "toggle flash on and off",
                        onClick = {
                            showTorchBottomSheet = true
                            viewModel.toggleTorch()
                        }
                    )


                    IconItem(
                        drawableRes = R.drawable.ic_timer,
                        contentDescription = "Photo Capture Timer"
                    )
                    TextItem("Full")
                    TextItem("16M")
                    IconItem(
                        drawableRes = R.drawable.ic_filter,
                        contentDescription = "Choose filter"
                    )
                    IconItem(
                        drawableRes = R.drawable.ic_switch_camera,
                        contentDescription = "Toggle Camera",
                    ) {
                        viewModel.toggleCamera()
                    }
                }
            }


            Box(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .pointerInput(Unit) {}
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Slider(
                        value = zoomScale,
                        onValueChange = {
                            viewModel.zoom(it)
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.width((64 * 2).dp),

                        )
                    Surface(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .padding(10.dp)
                            .clickable(
                                onClick = {}
                            ),
                        color = Color.Transparent,
                        shape = CircleShape,
                    ) {}


                }

            }



            AnimatedVisibility(
                visible = showAutofocusIndicator,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                    .offset((-24).dp, (-24).dp)
            ) {
                Spacer(
                    Modifier
                        .border(1.dp, Color.White, CircleShape)
                        .size(48.dp)
                )

            }

            if (showExposureBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showExposureBottomSheet = false
                    },
                ) {
                    Slider(
                        value = exposureValue.toFloat(),
                        onValueChange = {
                            viewModel.changeExposure(it.toInt())
                        },
                        valueRange = viewModel.getExposureRange().lower.toFloat()..viewModel.getExposureRange().upper.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),

                        )
                }
            }


        }

    }
}



@Composable
fun TextItem(
    text: String,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = 24.sp,
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                onClick()
            },
        color = color
    )
}

@Composable
fun IconItem(
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(10.dp)
            .size(24.dp)
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            tint = color
        )
    }
}

@Composable
fun CameraPermissionRejectionDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Camera Permission Rejected")
        },
        text = {
            Text(text = "You won't be able to use camera features without this permission")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Ok")
            }
        },

        )
}

@Composable
fun CameraRationaleDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        title = {
            Text(text = "Camera Permission has not been granted")
        },
        text = {
            Text(text = "you cannot use any camera features without this permission. Go to settings and grant Camera permission")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    settingsIntent.data = uri
                    context.startActivity(settingsIntent)
                }
            ) {
                Text("Settings")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}








