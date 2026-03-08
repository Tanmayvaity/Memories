package com.example.memories.feature.feature_camera.presentation.camera

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.memories.R
import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.core.presentation.MediaResult
import com.example.memories.core.presentation.components.RationaleDialog
import com.example.memories.core.util.PermissionHelper
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.presentation.camera.components.LowerBox
import com.example.memories.feature.feature_camera.presentation.camera.components.UpperBox
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.UUID

private const val TAG = "CameraScreen"

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CameraRoot(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel<CameraViewModel>(),
    onBack: (String?) -> Unit = {}
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showRationale by remember { mutableStateOf(false) }
    var showAudioRationale by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf<Boolean?>(null) }
    val viewModel: CameraViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()



    LaunchedEffect(Unit) {
        viewModel.mediaEventFlow.collect { event ->
            when(event){
                is MediaResult.Error -> {
                    Toast.makeText(
                        context,
                        "error : ${event.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MediaResult.Success -> {
                    onBack(event.data)
                    Log.i(TAG, "CameraScreen: success while capturing ${event.data.toString()}")
                }
            }

        }
    }

    // check permission

    val cameraRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        showRationale = false
        showCameraScreen = true
    }
    val audioRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        showAudioRationale = false
        showCameraScreen = true
    }

    PermissionHelper(
        lifecycleOwner = lifecycleOwner,
        onGranted = {
            showRationale = false
            showCameraScreen = true
        },
        onRationale = {
            showRationale = true
            showCameraScreen = false
        },
        onRequest = { permission ->
            cameraRequestLauncher.launch(
                Manifest.permission.CAMERA
            )
        },
        permission = Manifest.permission.CAMERA,
        context = context
    )
    PermissionHelper(
        lifecycleOwner = lifecycleOwner,
        onGranted = {
            showAudioRationale = false
            showCameraScreen = true
        },
        onRationale = {
            showAudioRationale = true
            showCameraScreen = false
        },
        onRequest = { permission ->
            audioRequestLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        },
        permission = Manifest.permission.RECORD_AUDIO,
        context = context
    )

    if (showRationale) {
        RationaleDialog(
            title = "Camera permission has not been granted",
            body = "You won't be able to access camera features without this permission." +
                    "Go to settings and grant Camera permission",
            icon = R.drawable.ic_camera,
            iconContentDescription = stringResource(R.string.camera_icon),
            onConfirm = {
                createSettingsIntent(context)
                showRationale = false
            },
            onDismiss = {
                showRationale = false
            }
        )
    }

    if (showAudioRationale) {
        RationaleDialog(
            title = "Audio permission has not been granted",
            body = "You won't be able to record audio features without this permission." +
                    "Go to settings and grant audio permission",
            icon = R.drawable.ic_camera,
            iconContentDescription = stringResource(R.string.camera_icon),
            onConfirm = {
                createSettingsIntent(context)
                showAudioRationale = false
            },
            onDismiss = {
                showAudioRationale = false
                onBack(null)
            }
        )
    }




    CameraScreen(
        modifier = modifier,
        permissionStatus = showCameraScreen,
        state = state,
        onEvent = viewModel::onEvent,
        onBack = {
            onBack(null)
        },

        cameraSettingsState = state.cameraSettingsState
    )


}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    permissionStatus: Boolean?,
    state: CameraState,
    onEvent: (CameraEvent) -> Unit = {},
    onBack: () -> Unit = {},
    cameraSettingsState: CameraSettingsState? = null
) {

    LaunchedEffect(cameraSettingsState) {
        if (cameraSettingsState != null) {
            Log.d(TAG, "CameraScreen: ${cameraSettingsState.toString()}")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var co by remember { mutableStateOf(Offset(0f, 0f)) }
    var showTimer by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_PAUSE) {
                    Log.d(TAG, "onStateChanged: on Pause called")
                    onEvent(CameraEvent.CancelTimer)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val coordinateTransformer = remember(
        state.aspectRatio, state.lensFacing
    ) { MutableCoordinateTransformer() }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified) }

    val autofocusRequestId = autofocusRequest.first
    // Show the autofocus indicator if the offset is specified
    var showAutofocusIndicator = autofocusRequest.second.isSpecified
    // Cache the initial coords for each autofocus request
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    // Queue hiding the request for each unique autofocus tap
    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(2000)
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Log.d(TAG, "CameraScreen: permissionStatus = ${permissionStatus} ")
        if (permissionStatus != null && !permissionStatus) {
            Text(
                text = "Camera permission has not been granted",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (permissionStatus != null && permissionStatus) {
            Text(
                text = "Camera",
                modifier = Modifier.align(Alignment.Center)
            )
        }



        state.surfaceRequest?.let { surfaceRequest ->
            CameraXViewfinder(
                surfaceRequest = surfaceRequest,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .aspectRatio(state.aspectRatio.ratio)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { tapCoords ->
                                onEvent(CameraEvent.ChangeLensFacing)
                            },
                            onTap = { offset ->
                                co = offset
                                with(coordinateTransformer) {
                                    onEvent(CameraEvent.TapToFocus(offset.transform()))
                                }


                                autofocusRequest = UUID.randomUUID() to offset
                            }
                        )
                    }
                    .pointerInput(Unit) {
//                        detectTransformGestures { _, _, zoom, _ ->
//                            val scale = (state.zoomScale + (zoom - 1f)).coerceIn(0f, 1f)
//                            Log.d(TAG, "zoom scale : $scale")
//                            onEvent(CameraEvent.Zoom(scale))
//                        }
                    }
            )

            AnimatedVisibility(
                visible = showAutofocusIndicator,
                enter = fadeIn(
                    animationSpec = tween(200)
                ),
                exit = fadeOut(
                    animationSpec = tween(200)
                ),
                // will change this later
                modifier = Modifier.offset(y = 155.dp)
            ) {
                Spacer(
                    Modifier
                        .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                        .offset((-24).dp, (-24).dp)
//                        .offset(y = 150.dp)
                        .border(1.dp, Color.White, CircleShape)
                        .size(48.dp)

                )
            }

        }

        UpperBox(
            modifier = Modifier.align(Alignment.TopEnd),
            torchState = state.torchState,
            onTorchToggle = {
                onEvent(CameraEvent.TorchToggle)
            },
            onAspectRatioChange = {
                onEvent(CameraEvent.ToggleAspectRatio)
            },
            onTimerSet = {
                onEvent(CameraEvent.ToggleTimerMode)
            },
            isVideoPlaying = state.videoState == VideoState.Started,
            isPictureTimerRunning = state.timerMode == TimerMode.Running,
            onToggleCamera = {
                onEvent(CameraEvent.ChangeLensFacing)
            }
        )

        LowerBox(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            isImageSaving = state.cameraMediaSaving,
            isPictureTimerRunning = state.timerMode == TimerMode.Running,
            onClick = {
                if (state.timerMode == TimerMode.Started) {
                    onEvent(CameraEvent.Timer)
                    return@LowerBox
                }

                if (state.timerMode == TimerMode.Running) {
                    onEvent(CameraEvent.CancelTimer)
                    return@LowerBox
                }

                if (state.mode == CameraMode.VIDEO && state.videoState == VideoState.Started) {
                    onEvent(CameraEvent.Stop)
                    return@LowerBox
                }


                onEvent(CameraEvent.Take)
            },
            onCameraModeClick = { mode: CameraMode ->
                when (mode) {
                    CameraMode.PHOTO -> {
                        onEvent(CameraEvent.PhotoMode)
                    }

                    CameraMode.PORTRAIT -> {
                        onEvent(CameraEvent.PortraitMode)
                    }

                    CameraMode.VIDEO -> {
                        onEvent(CameraEvent.VideoMode)
                    }
                }


            },
            cameraMode = state.mode,
            isVideoPlaying = state.videoState == VideoState.Started

        )

        if (state.videoState == VideoState.Started) {

            Text(
                text = String.format(Locale.ENGLISH, "%02d:%02d",  state.timeElapsed/ 60, state.timeElapsed % 60),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }


        LaunchedEffect(state.timerMode) {
            if (state.timerMode == TimerMode.Running) {
                showTimer = true
                return@LaunchedEffect
            }

            if (state.timerMode == TimerMode.Idle) {
                showTimer = false
                return@LaunchedEffect
            }
            Log.d(TAG, "CameraScreen: showTimer : ${showTimer}")

        }

        if (showTimer) {
            Text(
                text = state.pictureTimerTimeElapsed.toString(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                fontSize = 128.sp
            )
        }


    }


    LaunchedEffect(lifecycleOwner, state.lensFacing, state.aspectRatio) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            onEvent(CameraEvent.Preview(lifecycleOwner))
        }

    }

    LaunchedEffect(state.videoState) {
        if (state.videoState == VideoState.Started) {
            Log.d(TAG, "started")
        }

        if (state.videoState == VideoState.Idle) {
            Log.d(TAG, "idle")
        }

        if (state.videoState == VideoState.Stop) {
            Log.d(TAG, "Stop")
        }
    }

    BackHandler(
        enabled = true
    ){
        if(state.timerMode == TimerMode.Running){
            onEvent(CameraEvent.CancelTimer)
        }else{
            onBack()
        }
    }
}


