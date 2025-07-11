package com.example.memories.feature.feature_camera.presentation.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.feature.feature_camera.domain.model.CameraMode

@Preview
@Composable
fun LowerBox(
    modifier: Modifier = Modifier,
    onToggleCamera: () -> Unit = {},
    onChooseFromGallery: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val cameraActionItems: List<CameraMode> = CameraMode.entries.toList()
    var selectedIndex by remember { mutableStateOf(0) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
//            .background(Color.LightGray.copy(alpha = 0.2f))
            .pointerInput(Unit) {},
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Take Picture layer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconItem(
                    drawableRes = R.drawable.ic_feed,
                    contentDescription = "Choose from gallery",
                    color = Color.White,
                    alpha = 0.5f,
                    onClick = {
                        onChooseFromGallery()
                    },
                )

                //external circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 5.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable {
                            onClick()
                        },
                ) {
                    //internal circle with icon
                    Icon(
                        painter = painterResource(R.drawable.ic_take_photo),
                        contentDescription = "Capture Photo",
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color.White, CircleShape)
                            .padding(2.dp),
                        tint = Color.White
                    )
                }

                IconItem(
                    drawableRes = R.drawable.ic_camera_flip,
                    contentDescription = "Toggle camera lens",
                    color = Color.White,
                    alpha = 0.5f,
                    onClick = { onToggleCamera() },
                )


                // Camera Action Items


            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {


                itemsIndexed(cameraActionItems) { index, item ->
                    TextUnderLinedItem(
                        modifier = Modifier
                            .drawBehind {
                                val strokeWidthPx = 1.dp.toPx()
                                val verticalOffset = size.height + 1.sp.toPx()
                                drawLine(
                                    color = if (selectedIndex == index) Color.White else Color.Transparent,
                                    strokeWidth = strokeWidthPx,
                                    start = Offset(30f, verticalOffset),
                                    end = Offset(size.width - 30f, verticalOffset)
                                )
                            }
                            .padding(start = 10.dp, end = 10.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    selectedIndex = index
                                }
                            ),
                        fontSize = 16,
                        text = item.toString(),
                        textColor = if (selectedIndex == index) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )

                }


            }

        }
    }
}