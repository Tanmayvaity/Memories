package com.example.memories.view.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.R
import com.example.memories.view.components.IconItem
import com.example.memories.view.navigation.Screen
import com.example.memories.view.utils.createTempFile
import com.example.memories.viewmodel.ImageEditScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditScreen(
    uri: String,
    onArrowBackButtonClick: () -> Unit,
    onNextButtonClick: (Screen.Memory) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ImageEditScreenViewModel = viewModel()


    val downloadImageState by viewModel.downloadImageFlow.collectAsStateWithLifecycle()
    val imageBitmapState by viewModel.imageBitmap.collectAsStateWithLifecycle()
    val internalBitmapUri by viewModel.internalBitmapUri.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }


    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var scaleType by remember { mutableStateOf(ContentScale.Fit) }

    var showProgressBar by remember { mutableStateOf(false) }

    var showImage by remember { mutableStateOf(false) }

    var showInternalBitmapCreationProgressBar by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier.border(1.dp, Color.Black),
                title = {
                    Text("")
                },
                actions = {
                    Button(
                        onClick = {
                            showInternalBitmapCreationProgressBar = true
                            Log.i("ImageEditScreen", "Next:${imageBitmapState.toString()}")
//                            Log.e("ImageEditScreen","Next:${inter}")
                            if (imageBitmapState.data != null) {
                                viewModel.saveBitmapToInternalStorage(
                                    file = createTempFile(context),
                                    bitmap = imageBitmapState.data!!
                                )
                            }

                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.LightGray
                        ),
                        enabled = imageBitmapState.data != null && !showInternalBitmapCreationProgressBar
                    ) {
                        if (!showInternalBitmapCreationProgressBar && imageBitmapState.data !=null) {
                            Text(
                                text = "Next",
                            )
                        }

                        if (showInternalBitmapCreationProgressBar || imageBitmapState.data==null) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Color.Gray,
                            )
                        }

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackButtonClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        val actionList = listOf<Pair<String, Int>>(
            Pair("Crop", R.drawable.ic_crop),
            Pair("Rotate", R.drawable.ic_rotate),
            Pair("Brightness", R.drawable.ic_brightness),
            Pair("Enhance", R.drawable.ic_filter),
            Pair("Adjust", R.drawable.ic_adjust),
            Pair("Filters", R.drawable.ic_torch_on),
            Pair("Color", R.drawable.ic_color),
            Pair("Blur", R.drawable.ic_blur),
        )



        LaunchedEffect(Unit) {
            viewModel.uriToBitmap(uri.toUri(), context)
        }

        LaunchedEffect(imageBitmapState) {
            Log.d("ImageEditScreen", "launcheffect : ${imageBitmapState.toString()}")
            if (imageBitmapState.data != null) {
                showImage = true
            }
            if (imageBitmapState.error != null) {
                Log.e("ImageEditScreen", "ImageEditScreen: ${imageBitmapState.error}")
                Toast.makeText(context, "${imageBitmapState.error}", Toast.LENGTH_SHORT).show()
            }

        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        if (showImage) {
                            Log.d(
                                "ImageEditScreen",
                                "image : ${imageBitmapState.data!!::class.qualifiedName}"
                            )
                            Log.d("ImageEditScreen", "image : ${imageBitmapState.toString()}")
                            Image(
                                bitmap = imageBitmapState.data!!.asImageBitmap(),
                                contentDescription = "Captured Image",
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = scaleType,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                        ) {
                            if (!showImage) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.Center),
                                    strokeWidth = 3.dp,
                                    color = Color.Black
                                )
                            }
                        }


                    }

                    IconItem(
                        drawableRes = R.drawable.ic_toggle_scale_type,
                        contentDescription = "Toggle Image Scale Type",
                        backgroundColor = Color.Transparent,
                        color = Color.White,
                        iconSize = 18.dp,
                        onClick = {
                            scaleType = if (scaleType == ContentScale.Fit) {
                                ContentScale.Crop
                            } else {
                                ContentScale.Fit
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    )


                }



                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    itemsIndexed(actionList) { index, it ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(5.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Black.copy(0.3f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    selectedIndex = index
                                }

                        ) {
                            IconItem(
                                modifier = Modifier.padding(top = 10.dp),
                                drawableRes = it.second,
                                contentDescription = it.first,
                                shape = CircleShape,
                                alpha = 0.7f,
                                color = Color.Black
                            )
                            Text(
                                text = it.first,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }

                    }
                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {

                Row(

                ) {

                    OutlinedButton(
                        modifier = Modifier
                            .height(70.dp)
                            .padding(10.dp)
                            .weight(1f),
                        onClick = {

                        },

                        ) {
                        Text(
                            text = "Back to Home",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        modifier = Modifier
                            .height(70.dp)
                            .padding(10.dp)
                            .weight(1f),
                        enabled = !showProgressBar && imageBitmapState.data != null,
                        onClick = {
                            Log.d(
                                "ImageEditScreen",
                                "download click : ${imageBitmapState.data!!::class.qualifiedName}"
                            )
                            showProgressBar = true
                            viewModel.downloadPictureBitmap(context, imageBitmapState.data!!)

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!showProgressBar) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_download),
                                    contentDescription = "Download icon",
                                    tint = if(imageBitmapState.data==null)Color.Gray else Color.White,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Text(
                                    text = "Download",
                                    fontSize = 16.sp
                                )
                            }

                            if (showProgressBar) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(25.dp),
                                    color = Color.Gray,
                                    strokeWidth = 2.dp
                                )
                            }


                            LaunchedEffect(downloadImageState) {
                                if (downloadImageState.isLoading) {
                                    showProgressBar = true
                                }

                                if (downloadImageState.error != null) {
                                    Toast.makeText(
                                        context,
                                        "${downloadImageState.error}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showProgressBar = false
                                    viewModel.reset()
                                }

                                if (downloadImageState.data != null) {
                                    showProgressBar = false
                                    //TODO : handle snack bar collapse due to config change
                                    snackbarHostState.showSnackbar(
                                        message = downloadImageState.data.toString(),
                                        withDismissAction = true
                                    )
                                    viewModel.reset()
                                }
                            }

                            LaunchedEffect(internalBitmapUri) {
                                if (!internalBitmapUri.isLoading) {
                                    showInternalBitmapCreationProgressBar = false
                                }

                                Log.i(
                                    "ImageEditScreen",
                                    "internalBitmapUri : ${internalBitmapUri.toString()}",
                                )
                                if (internalBitmapUri.data != null) {
                                    onNextButtonClick(Screen.Memory(uri = internalBitmapUri.data.toString()))
                                }

                                if (internalBitmapUri.error != null) {
                                    Toast.makeText(
                                        context,
                                        "${internalBitmapUri.error}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(
                                        "ImageEditScreen",
                                        "internalBitmapUri : ${internalBitmapUri.error}",
                                    )
                                }

                            }


                        }
                    }
                }

            }
        }
    }


}





