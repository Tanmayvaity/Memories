package com.example.memories.feature.feature_feed.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.presentation.RationaleDialog
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.core.util.isPermissionGranted


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE


@Preview
@Composable
fun SharedRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showRationale by remember { mutableStateOf(false) }
    var showSharedScreen by remember { mutableStateOf<Boolean?>(null) }
    var showCameraScreen by remember { mutableStateOf<Boolean?>(null) }
    val viewModel: SharedScreenViewModel = hiltViewModel()

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        showSharedScreen = true
    }


    DisposableEffect(Unit) {
        val observer = object: LifecycleEventObserver{
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if(event == Lifecycle.Event.ON_START){
                    runtimeRequestLogic(
                        context,
                        mediaPermissionLauncher,
                        onRationale = {
                            showRationale = true
                            showSharedScreen = false
                        },
                        onPermissionGranted = {
                            showSharedScreen = true
                            showRationale = false
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
        RationaleDialog(
            title = "Media permission has not been granted",
            body = "You won't be able to access images from shared storage " +
                    "Go to settings grant images/videos permission",
            icon = R.drawable.ic_camera,
            iconContentDescription = stringResource(R.string.camera_icon),
            onConfirm = {
                createSettingsIntent(context)
//                showRationale = false
//                showSharedScreen = false
            },
            onDismiss = {
                showRationale = false
            }
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        if(showSharedScreen!=null && showSharedScreen == true){
            Log.i("SharedScreen", "permission granted")
            SharedScreen(
                viewModel = viewModel
            )
        }
        if(showSharedScreen!=null && showSharedScreen != true){
            Text(
                text = "Media permission has not been granted"

            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SharedScreen(
    modifier: Modifier = Modifier,
    viewModel: SharedScreenViewModel = hiltViewModel<SharedScreenViewModel>()
) {
    val uiState by viewModel.mediaState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shared Screen",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
            .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            if(uiState.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(150.dp),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopCenter)

            ) {
                items(uiState.data){ media->
//                AsyncImage(
//                    model = media.uri,
//                    contentScale = ContentScale.Crop,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(5.dp))
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//
//                )

                    AsyncImage(
                        model = media.uri,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
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

fun isMediaPermissionGranted(context : Context) : Boolean{
    return isPermissionGranted(
        context,
        READ_MEDIA_VISUAL_USER_SELECTED
    ) || isPermissionGranted(context, READ_MEDIA_IMAGES)
            || isPermissionGranted(context, READ_EXTERNAL_STORAGE)
}