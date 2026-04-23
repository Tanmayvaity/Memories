package com.example.memories.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
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
        // HeadlineSmall or HeadlineMedium provides a bolder, "bigger" feel than TitleLarge
//        style = MaterialTheme.typography.headlineSmall
//
        style = textStyle,

        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        modifier = modifier
    )
}

@Composable
fun LargeHeadingText(title : String,modifier: Modifier = Modifier) {
    HeadingText(
        title = title,
        textStyle = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        ),
        modifier = modifier.fillMaxWidth()
    )
}


@Preview
@Composable
fun HeadingTextPreview(modifier: Modifier = Modifier) {
    MaterialTheme{
        HeadingText(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            title = "Delete this tag",
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            )
        )
    }
}