package com.example.memories.feature.feature_feed.presentation.history

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.setSelectedDate
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.graphics.shapes.star
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.UiState
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.util.formatTime
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.filterNotNull
import okhttp3.internal.format
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryRoot(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit = {}
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    HistoryScreen(
        state = state,
        fetchData = viewModel::fetchMemories,
        onBack = onBack,
        onNavigateToMemoryCreate = onNavigate,
        onNavigateToMemoryDetail = onNavigate
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    state: UiState<MemoryWithMediaModel> = UiState(),
    fetchData: (LocalDate) -> Unit = {},
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

    var showDatePicker by rememberSaveable() { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { datePickerState.getSelectedDate() }
            .filterNotNull()
            .collect { date ->
//                Log.d("HistoryScreen", "date ${date}")
                fetchData(date)
            }

    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = datePickerState.getSelectedDate()?.format(
                            DateTimeFormatter.ofPattern("dd MMMM yyyy")
                        ).toString()
                    )
                },
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = {
                    onBack()
                },
                showAction = true,
                actionContent = {
                    IconItem(
                        drawableRes = R.drawable.ic_calender,
                        contentDescription = "Calender icon",
                        onClick = {
                            showDatePicker = !showDatePicker
                        },
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
            if (showDatePicker) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = true
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.data.isEmpty() && !state.isLoading) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "No data for this date, why not create one today?",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Button(
                                onClick = {
                                    onNavigateToMemoryCreate(AppScreen.Memory())
                                },
                            ) {
                                Text(
                                    text = "Create Memory",
                                )
                            }
                        }

                    }
                }
                if (state.isLoading) {
                    item {
                        LoadingIndicator(
                            showText = true
                        )
                    }


                }
                items(state.data) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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
                                onClick = {
                                    onNavigateToMemoryDetail(AppScreen.MemoryDetail(item.memory.memoryId))
                                },
                                modifier = Modifier.align(Alignment.End),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "View Detail",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

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
        HistoryScreen(
            state = UiState(
                data = previewMemories,

                )
        )
    }
}