package com.example.memories.view.components

import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextItem(
    modifier : Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = 24.sp,
        modifier = modifier
            .padding(10.dp)
            .clickable {
                onClick()
            },
        color = color
    )
}