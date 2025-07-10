package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.navigation.AppScreen

@Preview
@Composable
fun ImagePreview(
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit = {},
    onNextClick: (AppScreen.Memory) -> Unit = {},
    onEditItemClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    bitmap: Bitmap? = null,
) {
    AnimatedVisibility(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
//        visible = if(imageUri!=null) true else false,
        visible = if (bitmap != null) true else false,
        enter = fadeIn(
            animationSpec = tween(500)
        ),
        exit = fadeOut(
            animationSpec = tween(500)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {


            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .weight(5f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            ,
                        contentScale = ContentScale.Fit
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color.Black)
//                        .padding(10.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        enabled = bitmap != null,
                        onClick = {
                            onDownloadClick()
                        },
                        modifier = Modifier
                            .height(70.dp)
                            .weight(1f)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.LightGray
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.download)
                        )
                    }
                    IconItem(
                        drawableRes = R.drawable.ic_edit,
                        contentDescription = "",
                        backgroundColor = Color.Gray,
                        color = Color.White,
                        onClick = {
                            onEditItemClick()
                        }
                    )
                    Button(
                        onClick = {
                            onNextClick(AppScreen.Memory(""))
                        },
                        modifier = Modifier
                            .height(70.dp)
                            .weight(1f)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.navigate_to_next)
                        )
                    }
                }

            }

//            IconItem(
//                modifier = Modifier
//                    .rotate(45f)
//                    .padding(top = 100.dp)
//                    .align(Alignment.TopStart),
//                drawableRes = R.drawable.ic_create,
//                contentDescription = "",
//                backgroundColor = Color.Transparent,
//                color = Color.White,
//                onClick = {
//                    onBackPress()
//                },
//            )


        }
    }


}