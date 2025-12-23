package com.example.memories.feature.feature_camera.presentation.camera

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import com.example.memories.core.presentation.components.RationaleDialog
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.PermissionHelper
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.core.util.mapContentUriToType
import com.example.memories.feature.feature_camera.presentation.camera.components.LowerBox
import com.example.memories.feature.feature_camera.presentation.camera.components.UpperBox
import com.example.memories.navigation.AppScreen
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.UUID

private const val TAG = "CameraScreen"

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CameraRoute(
    modifier: Modifier = Modifier,
    onNavigateToImageEdit: (AppScreen.MediaEdit) -> Unit,
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showRationale by remember { mutableStateOf(false) }
    var showAudioRationale by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf<Boolean?>(null) }
    val viewModel: CameraViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                onBack()
            }
        )
    }




    CameraScreen(
        modifier = modifier,
        permissionStatus = showCameraScreen,
        state = state,
        onEvent = viewModel::onEvent,
        viewModel = viewModel,
        onNavigateToImageEdit = onNavigateToImageEdit,
        onBack = onBack,
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
    viewModel: CameraViewModel = hiltViewModel<CameraViewModel>(),
    onNavigateToImageEdit: (AppScreen.MediaEdit) -> Unit,
    onBack: () -> Unit = {},
    cameraSettingsState: CameraSettingsState? = null
) {

    LaunchedEffect(cameraSettingsState) {
        if (cameraSettingsState != null) {
            Log.d(TAG, "CameraScreen: ${cameraSettingsState.toString()}")
        }
    }


    val context = LocalContext.current
    val app = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    var showImagePreview by remember { mutableStateOf(false) }
    val mediaUri by viewModel.capturedMediaUri.collectAsStateWithLifecycle()
    val timer by viewModel.timeElapsed.collectAsStateWithLifecycle()
    var showTimerPopUpMenu by remember { mutableStateOf(false) }
    var co by remember { mutableStateOf(Offset(0f, 0f)) }
    val pictureTimer by viewModel.takePictureTimerTimeElapsed.collectAsStateWithLifecycle()
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


//    val ratio = if(state.aspectRatio == AspectRatio.RATIO_16_9)

//    val mediaLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//
//        if (uri != null) {
//
//            val uriWrapper = UriType(
//                uri = uri.toString(),
//                type = uri.mapContentUriToType(context)
//            )
//            Log.d(TAG, "CameraScreen: ${uriWrapper.uri}")
//            Log.d(TAG, "CameraScreen: ${uriWrapper.type}")
//            onNavigateToImageEdit(
//                AppScreen.MediaEdit(
//                    uriWrapper
//                )
//            )
//        }
//
//    }

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { message ->
            Log.e(TAG, "CameraScreen: error while capturing $message")
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


//            if (!isUserInteractingWithSlider) {
//
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
                showTimerPopUpMenu = true
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
            isPictureTimerRunning = state.timerMode == TimerMode.Running,
//            onChooseFromGallery = {
//                mediaLauncher.launch(
//                    PickVisualMediaRequest(
//                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
//                    )
//                )
//            },
            onClick = {
//                val file = createTempFile(
//                    context
//                )
//
//                val videoFile = createVideoFile(
//                    context
//                )
//                Log.d(TAG, "CameraScreen: ${videoFile.path}")


                if (viewModel.state.value.timerMode == TimerMode.Started) {
                    viewModel.onEvent(CameraEvent.Timer)
                    return@LowerBox
                }

                if (state.timerMode == TimerMode.Running) {
                    onEvent(CameraEvent.CancelTimer)
                    return@LowerBox
                }

                if (viewModel.state.value.mode == CameraMode.VIDEO && viewModel.state.value.videoState == VideoState.Started) {
                    viewModel.onEvent(CameraEvent.Stop)
                    return@LowerBox
                }

                if (viewModel.state.value.mode == CameraMode.VIDEO &&
                    viewModel.state.value.videoState == VideoState.Idle
                ) {
                    viewModel.onEvent(CameraEvent.Take)
                    return@LowerBox
                } else {
                    viewModel.onEvent(CameraEvent.Take)
                    return@LowerBox
                }
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

        // tap indicator for debugging
//        Surface(
//            modifier = Modifier
//                .offset{co.round()}
//                .offset((-5.dp),(-5.dp))
//                .height(10.dp).width(10.dp)
//                .background(Color.White)
//
//        ) {
//
//        }

        if (state.videoState == VideoState.Started) {
            Text(
                text = String.format(Locale.ENGLISH, "%02d:%02d", timer / 60, timer % 60),
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
                text = pictureTimer.toString(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                fontSize = 128.sp
            )
        }


    }

    LaunchedEffect(mediaUri) {
        if (mediaUri.uri != null && mediaUri.type!!.isImageFile()) {
            onNavigateToImageEdit(AppScreen.MediaEdit(mediaUri))
            onEvent(CameraEvent.Reset)
        }
    }

    LaunchedEffect(mediaUri) {
        if (mediaUri.uri != null && mediaUri.type!!.isVideoFile()) {
            onNavigateToImageEdit(AppScreen.MediaEdit(mediaUri))
            onEvent(CameraEvent.Reset)
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
//            Toast.makeText(context,"Video Started",Toast.LENGTH_SHORT).show()
        }

        if (state.videoState == VideoState.Idle) {
            Log.d(TAG, "idle")
//            Toast.makeText(context,"Video idle",Toast.LENGTH_SHORT).show()
        }

        if (state.videoState == VideoState.Stop) {
            Log.d(TAG, "Stop")
//            Toast.makeText(context,"Video Stop",Toast.LENGTH_SHORT).show()
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


