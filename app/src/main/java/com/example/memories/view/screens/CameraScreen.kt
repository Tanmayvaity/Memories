package com.example.memories.view.screens


import android.Manifest
import com.example.memories.R
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.model.models.AspectRatio
import com.example.memories.view.components.CameraRationaleDialog
import com.example.memories.view.components.IconItem
import com.example.memories.view.components.TextItem
import com.example.memories.view.navigation.Screen
import com.example.memories.view.utils.PermissionUtil
import com.example.memories.view.utils.isPermissionGranted
import com.example.memories.viewmodel.CameraScreenViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.util.UUID

private const val TAG = "CameraScreen"

@Composable
fun CameraScreen(
    popBack: () -> Unit,
    onImageCaptureNavigate : (Screen.ImageEdit) -> Unit,
    viewModel: CameraScreenViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successfullImageCapture by viewModel.successfullImageCapture.collectAsStateWithLifecycle()
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
        CameraRationaleDialog(
            title = "Camera Permission has not been granted",
            text = "you cannot use any camera features without this permission. Go to settings and grant Camera permission",
            onDismissRequest = {
                showRationale = false
                popBack()
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

    if (cameraPermissionStatus) {
        CameraPreviewContent(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            lifecycleOwner = lifecycleOwner,
            onImageCaptureNavigate = onImageCaptureNavigate
        )
    }

    LaunchedEffect(errorMessage,successfullImageCapture) {
        if(errorMessage!=null){
            Toast.makeText(context,errorMessage!!.message,Toast.LENGTH_SHORT).show()
            viewModel.resetErrorState()
        }
        if(successfullImageCapture!=null){
            Toast.makeText(context,"Image Captured Successfully",Toast.LENGTH_SHORT).show()
            Log.d(TAG, "CameraScreen-content uri : ${successfullImageCapture.toString()}")
            onImageCaptureNavigate(Screen.ImageEdit)

        }
    }

    BackHandler(
        enabled = true
    ) {
        viewModel.resetUriState()
        popBack()

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: CameraScreenViewModel,
    lifecycleOwner: LifecycleOwner,
    onImageCaptureNavigate : (Screen.ImageEdit) -> Unit
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val lensFacing by viewModel.lensFacing.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    val zoomScale by viewModel.zoomScale.collectAsStateWithLifecycle()
    val exposureValue by viewModel.exposureValue.collectAsStateWithLifecycle()
    val torchState by viewModel.torchState.collectAsStateWithLifecycle()
    val tempImageState by viewModel.tempImageBitmap.collectAsStateWithLifecycle()

    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()

    var ratio = aspectRatio.ratio



    var showExposureBottomSheet by remember { mutableStateOf(false) }
    LaunchedEffect(lensFacing,aspectRatio) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.bindToCamera(
                context.applicationContext,
                lifecycleOwner
            )
        }

    }

    var isUserInteractingWithSlider by remember { mutableStateOf(false) }
//    var showExposureSlider = false

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified) }

    val autofocusRequestId = autofocusRequest.first
    // Show the autofocus indicator if the offset is specified
    var showAutofocusIndicator = autofocusRequest.second.isSpecified
    // Cache the initial coords for each autofocus request
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    // Queue hiding the request for each unique autofocus tap
    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId, isUserInteractingWithSlider) {

            if (!isUserInteractingWithSlider) {
                delay(2000)
                autofocusRequest = autofocusRequestId to Offset.Unspecified
            }
        }
    }

    if (surfaceRequest == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = Color.Black,
                strokeWidth = 5.dp
            )
        }

    }

    surfaceRequest?.let { request ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) {

            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .aspectRatio(ratio)
                    .align(Alignment.Center)
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

            UpperBox(
                modifier = Modifier.align(Alignment.TopCenter),
//                onExposureBtnClicked = {
//                    showExposureBottomSheet = true
//                },
                torchState = torchState,
                onTorchToggle = {
                    viewModel.toggleTorch()
                },
                onSwitchCamera = {
                    viewModel.toggleCamera()
                },
                onAspectRatioChange = {
                    viewModel.setAspectRatio()
                }

            )

            LowerBox(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                onExposureSliderChange = { zoomValue ->
                    viewModel.zoom(zoomValue)
                },
                zoomScale = zoomScale,
                cameraActionItems = listOf<String>(
                    "FUN",
                    "PORTRAIT",
                    "PHOTO",
                    "VIDEO",
                    "SLOW MOTION",
                    "PANAROMA"
                ),
                onClick = {
                    Log.d(TAG, "photo capture btn clicked")
                    val imageDirPath = File(context.cacheDir,"imagess").apply{
                        if(!exists()){
                            mkdir()
                        }
                    }
                    val tempImageFile = File.createTempFile("temp_",".jpg",imageDirPath)


                    viewModel.takePicture(tempImageFile)
                }
            )


            AnimatedVisibility(
                visible = showAutofocusIndicator,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
            ) {
                Spacer(
                    Modifier
                        .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                        .offset((-24).dp, (-24).dp)
                        .border(1.dp, Color.White, CircleShape)
                        .size(48.dp)

                )
                CustomSlider(
                    modifier = Modifier
                        .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                        .offset((-50).dp, 32.dp)
                        .width(100.dp),
                    colors = SliderColors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White,
                        inactiveTickColor = Color.White,
                        disabledThumbColor = Color.Gray,
                        disabledActiveTrackColor = Color.Gray,
                        disabledActiveTickColor = Color.Gray,
                        disabledInactiveTrackColor = Color.Gray,
                        disabledInactiveTickColor = Color.Gray,
                        activeTickColor = Color.White
                    ),
                    thumbIcon = R.drawable.ic_exposure,
                    onExposureChange = { exposure ->
                        isUserInteractingWithSlider = true
                        viewModel.changeExposure(exposure.toInt())
                    },
                    onExposureChangeFinished = {
                        isUserInteractingWithSlider = false
                    },
                    exposureValue = exposureValue,
                    min = viewModel.getExposureRange().lower.toFloat(),
                    max = viewModel.getExposureRange().upper.toFloat(),
                    thumbColor = Color.White
                )

            }

            if (showExposureBottomSheet) {
                SliderModalBottomSheet(
                    onDismissRequest = {
                        showExposureBottomSheet = false
                    },
                    onExposureChange = { exposure ->
                        viewModel.changeExposure(exposure.toInt())
                    },
                    exposureValue = exposureValue,
                    min = viewModel.getExposureRange().lower.toFloat(),
                    max = viewModel.getExposureRange().upper.toFloat(),
                    tempImageState!!
                )
            }


        }
    }

    LaunchedEffect(tempImageState) {
        if (tempImageState != null) {
            showExposureBottomSheet = true
        }
    }





//    LaunchedEffect(successfullImageCapture) {
//        if(successfullImageCapture!=null){
//
//        }
//    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderModalBottomSheet(
    onDismissRequest: () -> Unit,
    onExposureChange: (Float) -> Unit = {},
    exposureValue: Int,
    min: Float,
    max: Float,
    bitmap: Bitmap

) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
    ) {
//        CustomSlider(
//            modifier = Modifier.fillMaxWidth(),
//            colors = SliderColors(
//                thumbColor = Color.Black,
//                activeTrackColor = Color.Black,
//                inactiveTrackColor = Color.Black,
//                inactiveTickColor = Color.Black,
//                disabledThumbColor = Color.Gray,
//                disabledActiveTrackColor = Color.Gray,
//                disabledActiveTickColor = Color.Gray,
//                disabledInactiveTrackColor = Color.Gray,
//                disabledInactiveTickColor = Color.Gray,
//                activeTickColor = Color.Black
//            ),
//            thumbIcon = R.drawable.ic_exposure,
//            onExposureChange = onExposureChange,
//            exposureValue = exposureValue,
//            min = min,
//            max = max,
//            thumbColor = Color.Black
//        )

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = ""
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    modifier: Modifier = Modifier,
    colors: SliderColors,
    @DrawableRes thumbIcon: Int,
    thumbColor: Color,
    onExposureChange: (Float) -> Unit = {},
    onExposureChangeFinished: () -> Unit = {},
    exposureValue: Int,
    min: Float,
    max: Float,
) {
    Slider(
        value = exposureValue.toFloat(),
        onValueChange = {
            onExposureChange(it)
        },
        onValueChangeFinished = {
            onExposureChangeFinished()
        },
        valueRange = min..max,
        modifier = modifier,
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(2.dp),
                colors = colors
            )
        },
        thumb = { sliderState ->
            Icon(
                painter = painterResource(thumbIcon),
                contentDescription = "Exposure Change Icon",
                tint = thumbColor
            )
        }
    )
}

@Composable
fun UpperBox(
    modifier: Modifier = Modifier,
//    onExposureBtnClicked: () -> Unit = {},
    torchState: Boolean,
    onTorchToggle: () -> Unit = {},
    onTimerSet: () -> Unit = {},
    onAspectRatioChange: () -> Unit =  {},
    onSwitchCamera: () -> Unit = {},

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {}
//            .background(Color.LightGray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
//            IconItem(
//                drawableRes = R.drawable.ic_exposure,
//                contentDescription = "Change Camera Exposure",
//                onClick = {
//
//                    onExposureBtnClicked()
//                }
//            )
            IconItem(
                drawableRes = if (torchState) R.drawable.ic_torch_off else R.drawable.ic_torch_on,
                contentDescription = "toggle flash on and off",
                onClick = {
                    onTorchToggle()
                }
            )


            IconItem(
                drawableRes = R.drawable.ic_timer,
                contentDescription = "Photo Capture Timer",
                onClick = {
                    onTimerSet()
                }
            )
            TextItem(text = "Full"){
                onAspectRatioChange()
            }
            TextItem(text = "16M")
            IconItem(
                drawableRes = R.drawable.ic_filter,
                contentDescription = "Choose filter",
            )
            IconItem(
                drawableRes = R.drawable.ic_switch_camera,
                contentDescription = "Toggle Camera",
            ) {
                onSwitchCamera()
            }
        }
    }
}

@Composable
fun LowerBox(
    modifier: Modifier = Modifier,
    onExposureSliderChange: (Float) -> Unit = {},
    zoomScale: Float,
    cameraActionItems: List<String>,
    onClick: () -> Unit
) {

    var selectedIndex by remember { mutableStateOf(2) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
//            .background(Color.LightGray.copy(alpha = 0.2f))
            .pointerInput(Unit) {},
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                itemsIndexed(cameraActionItems) { index, item ->
                    OutlinedButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .padding(end = 10.dp),
                        colors = ButtonColors(
                            containerColor = if (selectedIndex == index) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.LightGray
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        border = BorderStroke(width = 1.dp, color = Color.White),
                        onClick = {
                            selectedIndex = index
                            Log.d("CameraScreen", "Mode : ${item.toString()}")
                        }
                    ) {
                        Text(
                            text = item.toString(),
                            fontSize = 16.sp,
                        )
                    }
                }
            }


            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable {
                        onClick()
//                        Log.d("Camera", "LowerBox: ")
                    },
                shape = CircleShape,
                color = Color.White,
            ) {}


        }

    }
}

















