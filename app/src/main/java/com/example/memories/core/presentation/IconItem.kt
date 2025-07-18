package com.example.memories.core.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconItem(
    modifier : Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    color: Color = Color.White,
    shape : Shape = CircleShape,
    iconSize : Dp = 24.dp,
    backgroundColor : Color = Color.LightGray,
    alpha : Float = 1f,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .clip(shape)
            .background(backgroundColor.copy(alpha = alpha))
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier
                .size(iconSize),
        )
    }
}