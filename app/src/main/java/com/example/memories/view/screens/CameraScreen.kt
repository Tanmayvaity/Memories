package com.example.memories.view.screens


import android.Manifest
import android.R
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.ImageButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.view.utils.PermissionUtil
import com.example.memories.view.utils.isPermissionGranted
import com.example.memories.viewmodel.CameraScreenViewModel


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

@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: CameraScreenViewModel,
    lifecycleOwner: LifecycleOwner
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val lensFacing by viewModel.lensFacing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(lensFacing) {
        viewModel.bindToCamera(
            context.applicationContext,
            lifecycleOwner
        )
    }

    surfaceRequest?.let{ request ->
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            CameraXViewfinder(
                modifier = modifier,
                surfaceRequest = request
            )

            IconButton(
                onClick = {viewModel.toggleCamera()},
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(com.example.memories.R.drawable.ic_switch_camera),
                    contentDescription = "camera flip",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

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








