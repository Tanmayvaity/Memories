package com.example.memories.feature.feature_camera.presentation.camera.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TextUnderLinedItem(
    modifier : Modifier = Modifier,
    fontSize : Int,
    text : String,
    textColor : Color = Color.White,
    strokeColor:Color = Color.White,
    fontWeight : FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        color = textColor,
        fontSize = fontSize.sp,
        modifier = modifier,
        fontWeight = fontWeight,
    )
}