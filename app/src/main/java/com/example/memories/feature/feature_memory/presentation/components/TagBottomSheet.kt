package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.TagChip
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBottomSheet(
    modifier: Modifier = Modifier,
    textFieldValue: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    focusRequester: FocusRequester,
    textFieldInteraction: MutableInteractionSource,
    readOnly: Boolean,
    keyboardOptions: KeyboardOptions,
    listOfChips: List<TagModel>,
    onCrossClick: (Int) -> Unit,
    onChipAddClick: () -> Unit = {},
    onTagItemClick: (TagModel) -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    savedTags: List<TagModel> = emptyList(),
    isDarkMode: Boolean = false
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize(),
        containerColor = if (!isDarkMode) Color(0xffF2F2F2) else Color(0xff1A1A1A)

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.padding(start = 10.dp),
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Add Tags",
                        )
                    }

                    Text(
                        text = "Add Tags",
                        modifier = Modifier
                            .weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        modifier = Modifier.padding(end = 10.dp),
                        onClick = onChipAddClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Tags",
                        )
                    }

                }

            }
            item {
                TextFieldWithTags(
                    textFieldValue = textFieldValue,
                    onValueChanged = { newText ->
                        onValueChanged(newText)
                    },
                    focusRequester = focusRequester,
                    textFieldInteraction = textFieldInteraction,
                    readOnly = readOnly,
                    keyboardOptions = keyboardOptions,
                    listOfChips = listOfChips,
                    modifier = modifier,
                    onChipClick = { index -> onCrossClick(index) },
                    placeholder = placeholder,
                )
            }
            items(savedTags) { tag ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .padding(horizontal = 10.dp)
                        .clickable {
                            onTagItemClick(tag)
                        },
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
//                        containerColor = Color(0xff1A1A1A)

                    ),
//                    border = BorderStroke(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onSurface,
//                    )

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        TagChip(
                            tag = tag,
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {
                            onTagItemClick(tag)
                        }

//                        FilterChip(
//                            onClick = {
//                                onTagItemClick(tag)
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            modifier = Modifier
//                                .wrapContentHeight()
//                                .wrapContentWidth(align = Alignment.Start)
//                                .padding(10.dp)
//                                .weight(1f)
//                            ,
//                            border = null,
//                            colors = FilterChipDefaults.filterChipColors(
////                        backgroundColor = MaterialTheme.colors.secondary,
////                        contentColor = MaterialTheme.colors.onSecondary
//                                containerColor = MaterialTheme.colorScheme.primaryContainer,
////                            containerColor = ColorUtils.randomPastelColor()
//                            ),
//                            label = {
//                                Text(
//                                    modifier = Modifier
//                                        .padding(10.dp),
//                                    text = tag.label,
////                                color = MaterialTheme.colorScheme.onPrimary
//                                )
//                            },
//                            selected = false,
//                        )

                        IconButton(
                            modifier = Modifier
                                .padding(end = 10.dp),
                            onClick = onChipAddClick,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Add Tags",
                            )
                        }

                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewFontScale
@PreviewDynamicColors
@PreviewLightDark
@Composable
fun TagBottomSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        var hashTagTextValue by remember { mutableStateOf("") }
        val hashTagFocusRequester = remember { FocusRequester() }
        val hashTagInteraction = remember { MutableInteractionSource() }
        val hashTags = remember { mutableStateListOf("compose", "android", "jetpack") }
        TagBottomSheet(
            textFieldValue = hashTagTextValue,
            onValueChanged = { newText ->
                // Logic to add a tag when space or enter is pressed


                hashTagTextValue = newText


//                if (newText.contains(' ') || newText.contains('\n')) {
//                    val tag = newText.trim()
//                    if (tag.isNotEmpty()) {
//                        hashTags.add(tag)
//                    }
//                    hashTagTextValue = "" // Clear input
//                } else {
//                    hashTagTextValue = newText // Update input text
//                }
            },
            onChipAddClick = {
                val tag = hashTagTextValue.trim()
                if (hashTagTextValue.isNotEmpty()) {
                    hashTags.add(tag)
                    hashTagTextValue = ""
                }
            },

            focusRequester = hashTagFocusRequester,
            textFieldInteraction = hashTagInteraction,
            readOnly = false,
            keyboardOptions = KeyboardOptions.Default,
            listOfChips = hashTags.map { it -> TagModel(label = it.toString()) },
            modifier = Modifier,
            onCrossClick = { index -> hashTags.removeAt(index) },
            placeholder = "Add Tags",
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            ),
            onDismiss = {

            },
            savedTags = listOf(
                TagModel(label = "One"),
                TagModel(label = "Two"),
                TagModel(label = "Three"),
                TagModel(label = "Four"),
            )
        )
    }
}