package com.example.memories.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun IconItem(
    modifier : Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(10.dp)
            .size(24.dp)
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            tint = color
        )
    }
}