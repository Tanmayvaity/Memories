package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
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
    val displayedText = if (isHintVisible) hintContent else content

    var selectionState by remember { mutableStateOf(TextRange.Zero) }
    val textFieldValue = TextFieldValue(
        text = displayedText,
        selection = TextRange(
            selectionState.start.coerceIn(0, displayedText.length),
            selectionState.end.coerceIn(0, displayedText.length)
        )
    )

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isFocused by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current

    BasicTextField(
        cursorBrush = SolidColor(cursorBrushColor),
        value = textFieldValue,
        onValueChange = { newValue ->
            selectionState = newValue.selection
            if (newValue.text != displayedText) {
                onValueChange(newValue.text)
            }
        },
        onTextLayout = { textLayoutResult = it },
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChanged(it)
            },
        textStyle = TextStyle(
            color = if (isHintVisible) textColorOnVisibleHint else textColorWithoutVisibleHint,
            textMotion = TextMotion.Animated,
            fontWeight = fontWeight,
            fontSize = fontSize,
        )
    )

    LaunchedEffect(isFocused) {
        if (!isFocused) return@LaunchedEffect
        snapshotFlow {
            Triple(selectionState, textLayoutResult, imeInsets.getBottom(density))
        }
            .distinctUntilChanged()
            .collectLatest { (selection, layout, _) ->
                if (layout == null) return@collectLatest
                val caret = selection.end.coerceIn(0, layout.layoutInput.text.length)
                bringIntoViewRequester.bringIntoView(layout.getCursorRect(caret))
            }
    }
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
