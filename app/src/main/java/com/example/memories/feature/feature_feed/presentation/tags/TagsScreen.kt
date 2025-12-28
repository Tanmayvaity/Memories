package com.example.memories.feature.feature_feed.presentation.tags

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_other.presentation.screens.components.ThemeBottomSheet
import com.example.memories.ui.theme.VeryLightGray

@Composable
fun TagsRoot(
    modifier: Modifier = Modifier,
    viewmodel: TagsViewModel = hiltViewModel(),
    onBack : () -> Unit = {}
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()

    TagsScreen(
        state = state,
        onBack = onBack,
        onEvent = viewmodel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    modifier: Modifier = Modifier,
    state: TagsState = TagsState(),
    onBack: () -> Unit = {},
    onEvent : (TagEvents) -> Unit = {}
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    var showSortBySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteTagSheet by remember { mutableStateOf(false) }
    var tagItem : TagWithMemoryCountModel? = null

    Scaffold(
        topBar = {
            AppTopBar(
                showNavigationIcon = true,
                title = { HeadingText(title = "Your Tags") },
                onNavigationIconClick = onBack,
                showDivider = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            /* ---------- FIXED HEADER ---------- */
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Tags") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(inputText.isNotEmpty()){
                            IconItem(
                                imageVector = Icons.Default.Close,
                                onClick = {
                                    inputText = ""
                                },
                                contentDescription = "remove text",
                                alpha = 0f,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create New Tag")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- SCROLLABLE GRID ---------- */
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // ⭐ critical
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "All Tags",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = {
                            showSortBySheet = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_filter_list),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Sort By Count")
                        }
                    }
                }

                items(state.tags) { tag ->
                    Card(
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                tagItem = tag
                                showDeleteTagSheet = true
                            }
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = tag.tagLabel,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "${tag.memoryCount} Memories",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }


        if(showSortBySheet){
            SortTagsBottomSheet(
                onDismiss = {
                    showSortBySheet = false
                },
                onApply = { sortBy, sortOrder ->
                    showSortBySheet = false
                }
            )
        }

        if(showDeleteTagSheet && tagItem!=null){
            GeneralAlertSheet(
                title = "Delete \"${tagItem!!.tagLabel}\" ?",
                content = "Are you sure you want to delete this tag? This will not delete the memories " +
                        "associated with it,only the tag itself",
                state = sheetState,
                onDismiss = {
                    showDeleteTagSheet = false
                    tagItem = null
                },
                onConfirm = {
                    onEvent(TagEvents.DeleteTag(tagItem!!.tagId))
                    showDeleteTagSheet = false
                }
            )
        }

    }
}


@PreviewLightDark
@Composable
fun TagsScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        TagsScreen(
            state = TagsState(
                tags = List(30){
                    TagWithMemoryCountModel(
                        tagId = "",
                        tagLabel = "Vacation",
                        memoryCount = 10
                    )
                }
            )
        )
    }
}