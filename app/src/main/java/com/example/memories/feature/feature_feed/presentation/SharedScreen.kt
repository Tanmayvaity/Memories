package com.example.memories.feature.feature_feed.presentation

import android.Manifest
import android.R.attr.bitmap
import android.R.attr.bottom
import android.R.attr.checked
import android.R.attr.contentDescription
import android.R.attr.onClick
import android.R.attr.strokeWidth
import android.R.attr.text
import android.R.string.no
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.State.Empty.painter
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.crossfade
import coil3.size.Size
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.core.presentation.RationaleDialog
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.core.util.isPermissionGranted
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

const val TAG = "SharedScreen"

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun SharedRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
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
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_START) {
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
    ) {
        if (showSharedScreen != null && showSharedScreen == true) {
            Log.i("SharedScreen", "permission granted")
            SharedScreen(
                viewModel = viewModel,
                onBack = onBack
            )
        }
        if (showSharedScreen != null && showSharedScreen != true) {
            Text(
                text = "Media permission has not been granted"

            )
        }
    }


}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SharedScreen(
    modifier: Modifier = Modifier,
    viewModel: SharedScreenViewModel = hiltViewModel<SharedScreenViewModel>(),
    onBack: () -> Unit = {}
) {

    var checked by rememberSaveable { mutableStateOf(false) }
    var showCheckBox by rememberSaveable { mutableStateOf(false) }
    var noOfItemsSelected by rememberSaveable { mutableStateOf(0) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val mediaItems by viewModel.selectedMediaUri.collectAsStateWithLifecycle()
    val internalCacheFileList by viewModel.internalFileList.collectAsStateWithLifecycle()
    var selectedImage by rememberSaveable { mutableStateOf<android.net.Uri?>(null) }
    var showImage by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val pagingResponse = viewModel.pagingState.collectAsLazyPagingItems()
    val bitmapThumbnail by viewModel.bitmapThumbnail.collectAsStateWithLifecycle()

    val deleteRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "SharedScreen: images deleted successfully")
            viewModel.onEvent(FeedEvent.DeleteMultiple)
            viewModel.onEvent(FeedEvent.MediaSelectedEmpty)
            showCheckBox = false
        } else {
            Log.d(TAG, "SharedScreen: not ok")
        }

    }
    val animationSpeed = 500
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                    AnimatedVisibility(
                        visible = showCheckBox && !showImage,
                    ) {
                        Text(
                            text = "$noOfItemsSelected Selected",
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                    AnimatedVisibility(
                        visible = !showCheckBox,
                    ) {
                        Text(
                            text = "Shared Screen",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = showCheckBox && !showImage,
                    ) {
                        IconButton(
                            onClick = {
                                if (noOfItemsSelected > 0) {
                                    val pendingIntent = MediaStore.createDeleteRequest(
                                        context.contentResolver,
                                        mediaItems
                                    )

                                    deleteRequestLauncher.launch(
                                        IntentSenderRequest.Builder(pendingIntent.intentSender)
                                            .build(),
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "No items selected",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = "Delete icon"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = !showCheckBox,
                    ) {
                        IconButton(
                            onClick = {
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.previous)
                            )
                        }
                    }


                },
                actions = {
                    pagingResponse.apply {
                        when{
                            loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading ->{
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(10.dp).size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            loadState.refresh is LoadState.Error ->{

                            }
                        }

                    }



                    AnimatedVisibility(
                        visible = showCheckBox && !showImage,
                    ) {

                        IconButton(
                            onClick = {
                                if (noOfItemsSelected <= 0) {
                                    Toast.makeText(context, "No items selected", Toast.LENGTH_SHORT)
                                        .show()
                                    return@IconButton
                                }
                                viewModel.onEvent(FeedEvent.ShareMultiple)

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                }


            )
        }
    ) { innerPadding ->



        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {

            val gridState = rememberLazyGridState()

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(200.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopCenter)
            ) {

                items(
                    count = pagingResponse.itemCount,
                    key = { it -> pagingResponse[it]!!.uri }
                ) { it ->
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


                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        val mediaUri = pagingResponse.get(it)!!.uri

                        val mediaBitmap = pagingResponse.get(it)!!.bitmap.asImageBitmap()


                        Image(
                            bitmap = mediaBitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
//                                .size(width = 200.dp, height = 250.dp)
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))

                                .combinedClickable(
                                    onClick = {
//                                        viewModel.onEvent(FeedEvent.Delete(media.uri))
                                        Log.d(TAG, "item uri : ${mediaUri}")

                                        if (showCheckBox && !showImage) {
                                            if (mediaItems.contains(mediaUri)) {
                                                viewModel.onEvent(FeedEvent.MediaUnSelect(mediaUri))
                                                noOfItemsSelected--

                                            } else {
                                                viewModel.onEvent(FeedEvent.MediaSelect(mediaUri))
                                                noOfItemsSelected++
                                            }


                                        }
                                    },
                                    onLongClick = {
                                        if (showCheckBox) {
                                            return@combinedClickable
                                        }

                                        showCheckBox = true
                                        viewModel.onEvent(FeedEvent.MediaSelect(mediaUri))
                                        noOfItemsSelected++
                                    }
                                )
                        )


//                        AsyncImage(
//                            model = ImageRequest.Builder(context)
//                                .data(
//                                    bitmap
//                                )
//                                .crossfade(true)
//                                .build()
//                            ,
//                            contentScale = ContentScale.Crop,
//                            contentDescription = null,
//                            placeholder = painterResource(R.drawable.ic_launcher_background),
//                            error = painterResource(R.drawable.ic_launcher_background),
//
//                            modifier = Modifier
//                                .padding(bottom = 10.dp)
//                                .clip(RoundedCornerShape(10.dp))
//                                .fillMaxWidth()
//                                .wrapContentHeight()
//                                .combinedClickable(
//                                    onClick = {
////                                        viewModel.onEvent(FeedEvent.Delete(media.uri))
//                                        Log.d(TAG, "item uri : ${mediaUri}")
//
//                                        if (showCheckBox && !showImage) {
//                                            if (mediaItems.contains(mediaUri)) {
//                                                viewModel.onEvent(FeedEvent.MediaUnSelect(mediaUri))
//                                                noOfItemsSelected--
//
//                                            } else {
//                                                viewModel.onEvent(FeedEvent.MediaSelect(mediaUri))
//                                                noOfItemsSelected++
//                                            }
//
//
//                                        }
//                                    },
//                                    onLongClick = {
//                                        if (showCheckBox) {
//                                            return@combinedClickable
//                                        }
//
//                                        showCheckBox = true
//                                        viewModel.onEvent(FeedEvent.MediaSelect(mediaUri))
//                                        noOfItemsSelected++
//                                    }
//                                )
////
//                        )
                        if (showCheckBox) {
                            Checkbox(
                                checked = mediaItems.contains(mediaUri),
                                onCheckedChange = { it ->
                                    checked = it
                                    Log.d(TAG, "onCheckedChange : ${it}")
                                    if (it) {
                                        viewModel.onEvent(FeedEvent.MediaSelect(mediaUri))
                                        noOfItemsSelected++
                                    }
                                    if (!it) {
                                        viewModel.onEvent(FeedEvent.MediaUnSelect(mediaUri))
                                        noOfItemsSelected--
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                            )

                            IconItem(
                                drawableRes = R.drawable.ic_expand,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 10.dp,bottom = 20.dp),
                                contentDescription = "Expand selected Image",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = CircleShape,
                                alpha = 0.3f,
                                onClick = {
                                    showImage = true
                                    selectedImage = mediaUri
                                }

                            )
                        }
                    }


                }
            }


            AnimatedVisibility(
                visible = showImage && selectedImage != null
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = "Selected Image Preview",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(),
                    )
                }

            }


        }


    }



    if (showBottomSheet) {
        DeleteConfirmationBottomSheet(
            onDismiss = {
                showBottomSheet = false
            },
            onConfirm = {
                if (showImage) {
//                    viewModel.onEvent(FeedEvent.Delete(selectedImage!!))


                } else {
//                    viewModel.onEvent(FeedEvent.DeleteMultiple)

//                    deleteMedia(mediaItems)
                }

                showBottomSheet = false
                showCheckBox = false
                showImage = false
                selectedImage = null
            },
            sheetState = sheetState
        )
    }

    BackHandler(
        enabled = true
    ) {

        if (showImage) {
            showImage = false
            selectedImage = null
            return@BackHandler
        }

        if (!showCheckBox) {
            onBack()
            noOfItemsSelected = 0
            return@BackHandler
        }
        if (showCheckBox) {
            showCheckBox = false
            noOfItemsSelected = 0
            viewModel.onEvent(FeedEvent.MediaSelectedEmpty)
            return@BackHandler
        }

    }

    LaunchedEffect(internalCacheFileList) {
        val list = arrayListOf<android.net.Uri>()
        if (internalCacheFileList.isNotEmpty()) {
            internalCacheFileList.forEach { file ->

                val uri =
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                context.grantUriPermission(
                    context.packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Log.d(TAG, "shareable uri : ${uri}")
                list.add(uri)
            }


            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, list)
                type = "image/*"

            }

            context.startActivity(Intent.createChooser(shareIntent, null))


        }


    }


}




@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Delete Selected Items?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        onDismiss()

                    }

                ) {
                    Text(
                        text = "Cancel"
                    )
                }

                Button(
                    onClick = {
                        onConfirm()
                    }

                ) {
                    Text(
                        text = "Delete"
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

fun isMediaPermissionGranted(context: Context): Boolean {
    return isPermissionGranted(
        context,
        READ_MEDIA_VISUAL_USER_SELECTED
    ) || isPermissionGranted(context, READ_MEDIA_IMAGES)
            || isPermissionGranted(context, READ_EXTERNAL_STORAGE)
}