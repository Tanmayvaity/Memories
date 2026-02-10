package com.example.memories.feature.feature_memory.presentation.components

import android.R.attr.singleLine
import android.widget.CheckBox
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.memories.R
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.noRippleClickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTagBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    savedTags: List<TagModel> = emptyList(),
    tagQuery: String,
    selectedTags: List<TagModel> = emptyList(),
    onTagQueryChange: (String) -> Unit = {},
    onTagItemClick: (TagModel) -> Unit = {},
    onCreateTagClick: (String) -> Unit = {},
    onCrossClick: (TagModel) -> Unit = {},
    onClearTextClick : () -> Unit = {},
    onResetCLick: () -> Unit = {}
) {
    val stroke = remember {
        Stroke(
            width = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
    val dashColor = MaterialTheme.colorScheme.onSurfaceVariant
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Add Tags",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onResetCLick()
                    }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = tagQuery,
                onValueChange = onTagQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(text = "Search For Tags")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (tagQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onClearTextClick()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close), // use your mic icon
                                contentDescription = "Clear Text"
                            )
                        }
                    }
//
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { })
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = dashColor,
                            style = stroke,
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )
                    }
                    .clickable {
                        onCreateTagClick(tagQuery)
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconItem(
                        drawableRes = R.drawable.ic_create,
                        contentDescription = "Create Tag",
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            onCreateTagClick(tagQuery)
                        }
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    {
                        Text(
                            text = if (tagQuery.isEmpty()) "Create Tag" else "Create \"${tagQuery}\"",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = "Tap to add this tag to your collection",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                        )
                    }
                }
            }

            Text(
                text = "All Tags",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(savedTags) { index, tag ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                            .noRippleClickable {
                                onTagItemClick(tag)
                            }

                    ) {
                        Checkbox(
                            checked = tag in selectedTags,
                            onCheckedChange = {
                                onTagItemClick(tag)
                            }
                        )
                        Text(
                            text = "${tag.label}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
//
                        )
                        IconButton(onClick = {
                            onCrossClick(tag)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Close"
                            )
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SelectTagBottomSheetPreview() {
    MemoriesTheme {
        SelectTagBottomSheet(
            onDismiss = {},
            tagQuery = "",
            onTagQueryChange = {},
            savedTags = listOf(
                TagModel(tagId = "1", label = "Tag One")
            )
        )
    }
}