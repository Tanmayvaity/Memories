package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun EditList(
    modifier: Modifier = Modifier,
    menuItems : List<MenuItem> = emptyList<MenuItem>()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(225.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.align(Alignment.Center)
        ) {
            itemsIndexed(menuItems) { index, it ->
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
                        drawableRes = it.icon,
                        contentDescription = it.iconContentDescription,
                        shape = CircleShape,
                        alpha = 0.1f,
                        color = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        onClick = it.onClick
                    )
                    Text(
                        text = it.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun EditListPreview(){
    MemoriesTheme {
        val actionList = getEditMenuItems()
        EditList(
            menuItems = actionList
        )
    }
}


fun getEditMenuItems():List<MenuItem> = listOf(
    MenuItem("Crop", R.drawable.ic_crop, "Crop Element", onClick = {}),
    MenuItem("Rotate", R.drawable.ic_rotate, "Rotate Element", onClick = {}),
    MenuItem("Brightness", R.drawable.ic_brightness_2, "Adjust Brightness", onClick = {}),
    MenuItem("Lux", R.drawable.ic_lux, "Adjust Lux", onClick = {}),
    MenuItem("Adjust", R.drawable.ic_adjust, "Adjust Image", onClick = {}),
    MenuItem("Filters", R.drawable.ic_torch_on, "Apply Filters", onClick = {}),
    MenuItem("Color", R.drawable.ic_color, "Adjust Color", onClick = {}),
    MenuItem("Blur", R.drawable.ic_blur, "Apply Blur", onClick = {}),
    MenuItem("Contrast", R.drawable.ic_contrast, "Adjust Contrast", onClick = {}),
    MenuItem("Structure", R.drawable.ic_structure, "Adjust Structure", onClick = {}),
    MenuItem("Warmth", R.drawable.ic_warmth, "Adjust Warmth", onClick = {}),
    MenuItem("Saturation", R.drawable.ic_saturation, "Adjust Saturation", onClick = {}),
    MenuItem("Fade", R.drawable.ic_fade, "Apply Fade", onClick = {}),
    MenuItem("Highlights", R.drawable.ic_blur, "Adjust Highlights", onClick = {}),
    MenuItem("Shadows", R.drawable.ic_blur, "Adjust Shadows", onClick = {}),
    MenuItem("Vignette", R.drawable.ic_vignette, "Apply Vignette", onClick = {}),
    MenuItem("Tilt Shift", R.drawable.ic_tilt_shift, "Apply Tilt Shift", onClick = {}),
    MenuItem("Sharpen", R.drawable.ic_blur, "Sharpen Image", onClick = {})
)