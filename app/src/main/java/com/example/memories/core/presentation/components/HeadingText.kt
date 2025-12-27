package com.example.memories.core.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HeadingText(
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    title: String,
) {
    Text(
        text = title,
        style = textStyle,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Preview
@Composable
fun HeadingTextPreview(modifier: Modifier = Modifier) {
    MaterialTheme{
        HeadingText(
            title = "Delete this tag",
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            )
        )
    }
}