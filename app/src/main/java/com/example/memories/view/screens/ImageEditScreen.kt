package com.example.memories.view.screens


import android.R.attr.onClick
import android.R.attr.rotation
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.view.components.IconItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditScreen(
    uri: String,
    onArrowBackButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                title = {
                    Text("")
                },
                actions = {
                    Text(
                        text = "Next",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )

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

        var selectedIndex by remember { mutableStateOf<Int?>(null) }
        var scaleType by remember { mutableStateOf(ContentScale.Fit) }

        var showProgressBar by remember { mutableStateOf(false)}

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier.fillMaxSize().padding(10.dp)
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
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = scaleType,
                        )
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
            ){

                Row(

                ){

                    OutlinedButton(
                        modifier = Modifier
                            .height(70.dp)
                            .padding(10.dp)
                            .weight(1f)
                        ,
                        onClick = {
                        },

                    ) {
                        Text(
                            text = "Back to Home"
                        )
                    }
                    Button(
                        modifier = Modifier
                            .height(70.dp)
                            .padding(10.dp)
                            .weight(1f),
                        enabled = !showProgressBar,
                        onClick = {
                            showProgressBar = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.LightGray
                        )
                    ){
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(!showProgressBar){
                                Icon(
                                    painter = painterResource(R.drawable.ic_download),
                                    contentDescription = "Download icon",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Text(
                                    text = "Download",
                                    fontSize = 16.sp
                                )
                            }

                            if(showProgressBar){
                                CircularProgressIndicator(
                                    modifier = Modifier.size(25.dp),
                                    color = Color.Gray,
                                    strokeWidth = 2.dp
                                )
                            }

                            LaunchedEffect(showProgressBar) {
                                delay(3000)
                                showProgressBar = false
                            }


                        }
                    }
                }

            }




//
        }
    }


}