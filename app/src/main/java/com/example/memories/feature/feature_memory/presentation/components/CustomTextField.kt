package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    cursorBrushColor: Color = MaterialTheme.colorScheme.primary,
    isHintVisible: Boolean,
    hintContent: String,
    content: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    interactionSource: MutableInteractionSource? = null,
    textColorOnVisibleHint: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
        alpha = 0.6f
    ),
    textColorWithoutVisibleHint: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight : FontWeight = FontWeight.Normal,
    fontSize : TextUnit = 18.sp

) {
    BasicTextField(
        cursorBrush = SolidColor(cursorBrushColor),
        value = if (isHintVisible) hintContent else content,
        onValueChange = { it: String ->
            onValueChange(it)
        },
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 15.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
            .onFocusChanged {
                onFocusChanged(it)
            },
        textStyle = TextStyle(
            color = if (isHintVisible) textColorOnVisibleHint else textColorWithoutVisibleHint,
            textMotion = TextMotion.Animated,
            fontWeight = fontWeight,
            fontSize = fontSize,
        )
    )
}

@Preview
@Composable
fun TitleTextFieldPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 15.dp, start = 10.dp, end = 10.dp, bottom = 5.dp),
            isHintVisible = false,
            hintContent = "Hint",
            content = "Title Content",
            onValueChange = { it -> },
            onFocusChanged = {
            }
        )
    }
}