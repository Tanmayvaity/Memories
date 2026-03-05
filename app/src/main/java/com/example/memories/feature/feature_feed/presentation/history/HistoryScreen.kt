package com.example.memories.feature.feature_feed.presentation.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_feed.presentation.components.ErrorStateCard
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.SectionStateContainer
import com.example.memories.feature.feature_feed.presentation.search.components.EmptyResultPlaceHolder
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.filterNotNull
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryRoot(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit = {}
) {
    val date by viewModel.date.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()

    HistoryScreen(
        memoriesState = memories,
        date = date,
        onBack = onBack,
        onDateChange = viewModel::onDateChange,
        onNavigateToMemoryCreate = onNavigate,
        onNavigateToMemoryDetail = onNavigate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    memoriesState: SectionState<List<MemoryWithMediaModel>>,
    date: LocalDate? = null,
    onDateChange: (LocalDate) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToMemoryDetail: (AppScreen) -> Unit = {},
    onNavigateToMemoryCreate: (AppScreen) -> Unit = {}
) {
    val isPreviewMode = LocalInspectionMode.current

    val todayMillis = remember {
        LocalDate.now()
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis
    )

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { datePickerState.getSelectedDate() }
            .filterNotNull()
            .collect { selectedDate -> onDateChange(selectedDate) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = datePickerState.getSelectedDate()
                            ?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                            .toString()
                    )
                },
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showAction = true,
                actionContent = {
                    IconItem(
                        drawableRes = R.drawable.ic_calender,
                        contentDescription = "Calendar icon",
                        onClick = { showDatePicker = !showDatePicker },
                        alpha = 0f,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .animateContentSize()
        ) {
            AnimatedVisibility(showDatePicker) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = true
                )
            }
            AnimatedContent(
                targetState = memoriesState,
                modifier = Modifier.padding(16.dp),
                label = "memories_state"
            ) { state ->
                SectionStateContainer(
                    state = state,
                    loadingContent = {
                        LoadingIndicator(showText = true)
                    },
                    emptyContent = {
                        EmptyResultPlaceHolder(
                            emptyText = "No memories for this date",
                            buttonText = "Create Memory",
                            height = 200.dp,
                            onButtonClick = {
                                onNavigateToMemoryCreate(AppScreen.Memory())
                            }
                        )
                    },
                    errorContent = {
                        ErrorStateCard(onRetryClick = {})
                    },
                    successContent = { memories ->
                        MemoryList(
                            memories = memories,
                            isPreviewMode = isPreviewMode,
                            onNavigateToMemoryDetail = onNavigateToMemoryDetail
                        )
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MemoryList(
    memories: List<MemoryWithMediaModel>,
    isPreviewMode: Boolean,
    onNavigateToMemoryDetail: (AppScreen) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memories) { item ->
            MemoryCard(
                item = item,
                isPreviewMode = isPreviewMode,
                onViewDetail = {
                    onNavigateToMemoryDetail(AppScreen.MemoryDetail(item.memory.memoryId))
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MemoryCard(
    item: MemoryWithMediaModel,
    isPreviewMode: Boolean,
    onViewDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_timer),
                            contentDescription = "Time Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.memory.timeStamp.formatTime(format = "hh : mm a"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = item.memory.title,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.memory.content,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.mediaList.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        model = if (isPreviewMode) R.drawable.ic_launcher_background else item.mediaList[0].uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewDetail,
                modifier = Modifier.align(Alignment.End),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "View Detail",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@PreviewLightDark
@Composable
private fun HistoryScreenPreview() {
    val previewMemories = List(10) { MemoryWithMediaModel() }
    MemoriesTheme {
        HistoryScreen(memoriesState = SectionState.Success(previewMemories))
    }
}