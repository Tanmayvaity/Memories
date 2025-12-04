package com.example.memories.feature.feature_memory.presentation.components

import android.R.attr.label
import android.R.attr.singleLine
import android.R.attr.textStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.material3.ChipColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.memories.ui.theme.MemoriesTheme


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp // Crucial for wrapping
import androidx.compose.ui.unit.sp
import com.example.memories.core.domain.model.TagModel


@Composable
fun TextFieldWithTags(
    textFieldValue: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    focusRequester: FocusRequester,
    textFieldInteraction: MutableInteractionSource,
    readOnly: Boolean,
    keyboardOptions: KeyboardOptions,
    listOfChips: List<TagModel>,
    modifier: Modifier,
    onChipClick: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isFocused = textFieldInteraction.collectIsFocusedAsState()

    // Used in the original answer for custom theming, simplified here
    val placeholderColor = Color.Gray

    LaunchedEffect(Unit) {
        focusManager.moveFocus(FocusDirection.Up)
    }

    Box(
        modifier = modifier
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(25.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(25.dp)
            )
            .background(MaterialTheme.colorScheme.surface),


//            .clip(RoundedCornerShape(25.dp))
//            .border(
//                width = 1.dp,
//                color = Color.LightGray,
//                shape = RoundedCornerShape(25.dp)
//            )

    ) {
        // 1. Placeholder Text: Only shows if the input and chip list are empty.
        AnimatedVisibility(
            textFieldValue.isEmpty() && listOfChips.isEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,

                )
                Text(
                    text = placeholder,
                    color = placeholderColor,
                    modifier = Modifier,
                )

            }

        }

        // 2. FlowRow: The container that allows items (chips + text field) to wrap.
        FlowRow(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(all = 10.dp)

            ,
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
            // Horizontal spacing between chips/input
        ) {

            // 3. Render the Chips
            repeat(times = listOfChips.size) { index ->
                FilterChip(
                    onClick = {
//                        onChipClick(index)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .wrapContentWidth()

                    ,
                    trailingIcon = {
                        // Custom styling for the "X" button on the chip
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(3.dp)
                                .clickable{
                                    onChipClick(index)
                                }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.Close),
                                contentDescription = "Remove tag",
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
//                        backgroundColor = MaterialTheme.colors.secondary,
//                        contentColor = MaterialTheme.colors.onSecondary
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    label = {
                        Text(
                            text = listOfChips[index].label,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    selected = false,
                )
            }

            // 4. The editable Text Field (BasicTextField for maximum control)
            BasicTextField(
                value = textFieldValue,
                onValueChange = onValueChanged,
                modifier = modifier
                    .focusRequester(focusRequester)
                    // CRUCIAL: Makes the text field shrink to content width, allowing chips to be inline
                    .width(IntrinsicSize.Min)
                ,
                singleLine = false,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .defaultMinSize(minHeight = 48.dp), // Set minimum height for touch target
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier.wrapContentWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 4.dp)
                                    .wrapContentWidth(),
                            ) {
                                // Render the actual input text
                                innerTextField()
                            }
                        }
                    }
                },
                interactionSource = textFieldInteraction,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
//                cursorBrush =  Brush.verticalGradient(
//                    0.1f to Color.Red,
//                    0.3f to Color.Green,
//                    0.5f to Color.Blue,
//                    startY = 0.0f,
//                    endY = 100.0f
//                ),
                readOnly = readOnly,
                keyboardOptions = keyboardOptions,
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
            )
        }
    }
}

@PreviewFontScale
@PreviewDynamicColors
@PreviewLightDark
@Composable
fun TextFieldWithTagsPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        var hashTagTextValue by remember { mutableStateOf("") }
        val hashTagFocusRequester = remember { FocusRequester() }
        val hashTagInteraction = remember { MutableInteractionSource() }
        val hashTags = remember { mutableStateListOf("compose", "android", "jetpack") }
        TextFieldWithTags(
            textFieldValue = hashTagTextValue,
            onValueChanged = { newText ->
                // Logic to add a tag when space or enter is pressed
                if (newText.contains(' ') || newText.contains('\n')) {
                    val tag = newText.trim()
                    if (tag.isNotEmpty() && !hashTags.contains(tag) && tag.isNotBlank()) {
                        hashTags.add(tag)
                    }
                    hashTagTextValue = "" // Clear input
                } else {
                    hashTagTextValue = newText // Update input text
                }
            },
            focusRequester = hashTagFocusRequester,
            textFieldInteraction = hashTagInteraction,
            readOnly = false,
            keyboardOptions = KeyboardOptions.Default,
//            listOfChips = hashTags.map { it -> TagModel(label = it.toString()) },
            listOfChips = emptyList(),
            modifier = Modifier,
            onChipClick = { index -> hashTags.removeAt(index)},
            placeholder = "Add Tags"
        )
    }
}