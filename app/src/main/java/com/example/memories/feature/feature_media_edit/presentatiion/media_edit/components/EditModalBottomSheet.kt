package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.presentation.IconItem

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        },
    ) {
        val actionList = listOf<Pair<String, Int>>(
            Pair("Crop", R.drawable.ic_crop),
            Pair("Rotate", R.drawable.ic_rotate),
            Pair("Brightness", R.drawable.ic_brightness_2),
            Pair("Lux", R.drawable.ic_lux),
            Pair("Adjust", R.drawable.ic_adjust),
            Pair("Filters", R.drawable.ic_torch_on),
            Pair("Color", R.drawable.ic_color),
            Pair("Blur", R.drawable.ic_blur),
            Pair("Contrast", R.drawable.ic_contrast),
            Pair("Structure", R.drawable.ic_structure),
            Pair("Warmth", R.drawable.ic_warmth),
            Pair("Saturation", R.drawable.ic_saturation),
            Pair("Fade", R.drawable.ic_fade),
            Pair("Highlights", R.drawable.ic_blur),
            Pair("Shadows", R.drawable.ic_blur),
            Pair("Vignette", R.drawable.ic_vignette),
            Pair("Tilt Shift", R.drawable.ic_tilt_shift),
            Pair("Sharpen", R.drawable.ic_blur),

            )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
        ) {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.align(Alignment.Center)
            ) {
                itemsIndexed(actionList) { index, it ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(5.dp)

                    ) {
                        IconItem(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
//                                    color = Color.LightGray.copy(0.3f),
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(20.dp),
                            iconSize = 64.dp,
                            drawableRes = it.second,
                            contentDescription = it.first,
                            shape = CircleShape,
                            alpha = 0.1f,
                            color = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.background
                        )
                        Text(
                            text = it.first,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            }
        }

    }

}