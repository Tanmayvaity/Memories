package com.example.memories.feature.feature_feed.presentation.history

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldAdaptStrategies
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.adapter.computeWindowSizeClass
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LargeHeadingText
import com.example.memories.core.util.PlayButton
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_feed.presentation.common.MemoriesStateContent
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.components.PagedListContainer
import com.example.memories.feature.feature_feed.presentation.feed_detail.MemoryDetailRoot
import com.example.memories.feature.feature_feed.presentation.history.components.AnimatedSegmentedRow
import com.example.memories.feature.feature_feed.presentation.search.components.EmptyResultPlaceHolder
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryRoot(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit = {}
) {
    val date by viewModel.date.collectAsStateWithLifecycle()
    val memoriesByDate by viewModel.memoriesByDate.collectAsStateWithLifecycle()
    val memoriesByMonth = viewModel.memoriesByMonth.collectAsLazyPagingItems()
    val timeLineDisplayMode by viewModel.currentTimeLineDisplayMode.collectAsStateWithLifecycle()

    HistoryScreen(
        memoriesState = memoriesByDate,
        memoriesByMonth = memoriesByMonth,
        date = date,
        onBack = onBack,
        onDateChange = viewModel::onDateChange,
        timeLineDisplayMode = timeLineDisplayMode,
        onTabChange = viewModel::onTabChange,
        onNavigate = onNavigate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    memoriesState: SectionState<List<MemoryWithMediaModel>>,
    memoriesByMonth: LazyPagingItems<MemoryListItem>,
    date: LocalDate? = null,
    timeLineDisplayMode: TimeLineDisplayMode,
    onDateChange: (LocalDate) -> Unit = {},
    onTabChange: (TimeLineDisplayMode) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit = {},
) {
    val isPreviewMode = LocalInspectionMode.current
    val pagerState = rememberPagerState(
        pageCount = { TimeLineDisplayMode.entries.size },
        initialPage = timeLineDisplayMode.ordinal
    )
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val todayMillis = remember {
        LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }


    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis
    )

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val currentMonth by remember(memoriesByMonth) {
        derivedStateOf {
            if (timeLineDisplayMode != TimeLineDisplayMode.List) return@derivedStateOf null
            if (memoriesByMonth.itemCount == 0) return@derivedStateOf null

            val index = lazyListState.firstVisibleItemIndex
            if (index >= memoriesByMonth.itemCount) return@derivedStateOf null

            when (val item = memoriesByMonth.peek(index)) {
                is MemoryListItem.Entry -> if (isPreviewMode) YearMonth.now() else item.yearMonth()
                is MemoryListItem.MonthHeader -> if (isPreviewMode) YearMonth.now() else item.yearMonth
                null -> null
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { datePickerState.getSelectedDate() }
            .filterNotNull()
            .collect {
                selectedDate -> onDateChange(selectedDate)
            }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> onTabChange(TimeLineDisplayMode.entries[page]) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = {
                    HistoryTopBarTitle(
                        timeLineDisplayMode = timeLineDisplayMode,
                        currentMonth = currentMonth,
                        datePickerState = datePickerState,
                    )
                },
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showAction = true,
                actionContent = {
                    AnimatedVisibility(
                        visible = timeLineDisplayMode == TimeLineDisplayMode.Calendar,
                    ) {
                        IconItem(
                            drawableRes = R.drawable.ic_calender,
                            contentDescription = "Calendar icon",
                            onClick = {
                                showDatePicker = !showDatePicker
//                                showDatePickerDialog = !showDatePickerDialog
                            },
                            alpha = 0f,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HistoryTabRow(
                pagerState = pagerState,
                onTabClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
//                shouldShowSegmentedTabRow = windowSizeClass.isWidthAtLeastBreakpoint(
//                    WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
//                )
                shouldShowSegmentedTabRow = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (TimeLineDisplayMode.entries[page]) {
                    TimeLineDisplayMode.List -> {

                            PagedListContainer(
                                items = memoriesByMonth,
                                lazyListState = lazyListState,
                                itemContent = { memory ->
                                    when (memory) {
                                        is MemoryListItem.Entry -> {
                                            val memoryId = memory.memory.memory.memoryId
                                            HistoryMemoryCard(
                                                modifier = Modifier
                                                    .widthIn(max = 640.dp)
                                                    .fillMaxWidth()
                                                    .align(
                                                        Alignment.CenterHorizontally
                                                    ),
                                                item = memory.memory,
                                                isPreviewMode = isPreviewMode,
                                                showDateHeader = true,
                                                onViewDetail = {
                                                    onNavigate(
                                                        AppScreen.MemoryDetail(
                                                            memoryId = memoryId
                                                        )
                                                    )
                                                }
                                            )
                                        }

                                        is MemoryListItem.MonthHeader -> {
                                            HeadingText(
                                                title = memory.yearMonth.format(
                                                    DateTimeFormatter.ofPattern("MMMM yyyy")
                                                ),
                                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.ExtraBold
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                },
                                itemKey = { item ->
                                    when (item) {
                                        is MemoryListItem.Entry -> "entry_${item.memory.memory.memoryId}"
                                        is MemoryListItem.MonthHeader -> "header_${item.anchorId}"
                                    }
                                },
                                itemContentType = { item -> item::class.simpleName },
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                emptyContent = {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        EmptyResultPlaceHolder(
                                            emptyText = "No memories have been created",
                                            buttonText = "Create Memory",
                                            height = 200.dp,
                                            onButtonClick = { onNavigate(AppScreen.Memory()) },
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(all = 16.dp)
                                                .align(Alignment.TopCenter)
                                        )
                                    }
                                }
                            )

                    }

                    TimeLineDisplayMode.Calendar -> {

                        CompactCalendarLayout(
                            memoriesState = memoriesState,
                            datePickerState = datePickerState,
                            showDatePicker = showDatePicker &&
                                    timeLineDisplayMode == TimeLineDisplayMode.Calendar,
                            isPreviewMode = isPreviewMode,
                            onNavigateToMemoryDetail = onNavigate,
                            onNavigateToMemoryCreate = onNavigate,
                        )

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryTopBarTitle(
    timeLineDisplayMode: TimeLineDisplayMode,
    currentMonth: YearMonth?,
    datePickerState: DatePickerState,
) {
    AnimatedContent(targetState = timeLineDisplayMode) { mode ->
        when (mode) {
            TimeLineDisplayMode.List -> {
                AnimatedContent(targetState = currentMonth) { month ->
                    LargeHeadingText(
                        title = month?.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                            ?: "Past Memories",
                    )
                }
            }

            TimeLineDisplayMode.Calendar -> {
                LargeHeadingText(
                    title = datePickerState.getSelectedDate()
                        ?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                        .toString(),
                )
            }
        }
    }
}

@Composable
private fun HistoryTabRow(
    pagerState: PagerState,
    onTabClick: (Int) -> Unit,
    shouldShowSegmentedTabRow: Boolean = true,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (shouldShowSegmentedTabRow) {
            AnimatedSegmentedRow(
                selectedIndex = pagerState.currentPage,
                options = TimeLineDisplayMode.entries.toList().map { it -> it.displayName },
                onSelect = onTabClick,
                modifier = Modifier
                    .width(250.dp)
                    .align(Alignment.Center),
                pagerPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
            )

        } else {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                TimeLineDisplayMode.entries.forEachIndexed { index, mode ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { onTabClick(index) },
                        text = { Text(text = mode.displayName) }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ListModeContent(
    memoriesByMonth: LazyPagingItems<MemoryListItem>,
    lazyListState: LazyListState,
    onNavigateToMemoryDetail: (AppScreen) -> Unit,
    onNavigateToMemoryCreate: (AppScreen) -> Unit,
    onNavigateToTagsWithMemories: (AppScreen) -> Unit,
    isPreviewMode: Boolean,
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        Log.d("HistoryScreen", "ListModeContent: called")
        scope.launch {
            navigator.navigateBack()
        }
    }

//    LaunchedEffect(isAtLeastMedium) {
//        if(isAtLeastMedium && navigator.currentDestination?.pane == ListDetailPaneScaffoldRole.Detail){
//            val selectedItem = navigator.currentDestination?.contentKey
//            if(selectedItem != null){
//                onNavigateToMemoryDetail(AppScreen.MemoryDetail(selectedItem))
//            }
//            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
//        }
//    }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        paneExpansionDragHandle = { state ->
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state = state,
                    minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                    interactionSource = remember { MutableInteractionSource() }
                )
            )
        },
        listPane = {
            AnimatedPane {
                PagedListContainer(
                    items = memoriesByMonth,
                    lazyListState = lazyListState,
                    itemContent = { memory ->
                        when (memory) {
                            is MemoryListItem.Entry -> {
                                val memoryId = memory.memory.memory.memoryId
                                HistoryMemoryCard(
                                    item = memory.memory,
                                    isPreviewMode = isPreviewMode,
                                    showDateHeader = true,
                                    onViewDetail = {
                                        scope.launch {
                                            navigator.navigateTo(
                                                pane = ListDetailPaneScaffoldRole.Detail,
                                                contentKey = memoryId
                                            )
                                        }
                                    }
                                )
                            }

                            is MemoryListItem.MonthHeader -> {
                                HeadingText(
                                    title = memory.yearMonth.format(
                                        DateTimeFormatter.ofPattern("MMMM yyyy")
                                    ),
                                    textStyle = MaterialTheme.typography.titleLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    },
                    itemKey = { item ->
                        when (item) {
                            is MemoryListItem.Entry -> "entry_${item.memory.memory.memoryId}"
                            is MemoryListItem.MonthHeader -> "header_${item.anchorId}"
                        }
                    },
                    itemContentType = { item -> item::class.simpleName },
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    emptyContent = {
                        EmptyResultPlaceHolder(
                            emptyText = "No memories have been created",
                            buttonText = "Create Memory",
                            height = 200.dp,
                            onButtonClick = { onNavigateToMemoryCreate(AppScreen.Memory()) },
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp)
                        )
                    }
                )

            }

        },
        detailPane = {
            AnimatedPane {
                val id = navigator.currentDestination?.contentKey
                if (id != null) {
                    MemoryDetailRoot(
                        onBack = {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        },
                        onTagClick = onNavigateToTagsWithMemories,
                        onNavigateToMemory = onNavigateToMemoryCreate,
                        memoryId = id
                    )

                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select a memory to view",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            }
        }

    )


}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CompactCalendarLayout(
    memoriesState: SectionState<List<MemoryWithMediaModel>>,
    datePickerState: DatePickerState,
    showDatePicker: Boolean,
    isPreviewMode: Boolean,
    onNavigateToMemoryDetail: (AppScreen) -> Unit,
    onNavigateToMemoryCreate: (AppScreen) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showDatePicker,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 300),
                expandFrom = Alignment.Top
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300),
                shrinkTowards = Alignment.Top
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
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
            MemoriesStateContent(
                memoryState = state,
                emptyContent = {
                    EmptyResultPlaceHolder(
                        emptyText = "No memories for this date",
                        buttonText = "Create Memory",
                        height = 200.dp,
                        onButtonClick = { onNavigateToMemoryCreate(AppScreen.Memory()) },
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                successContent = { memories ->
                    MemoryList(
                        memories = memories,
                        isPreviewMode = isPreviewMode,
                        onNavigateToMemoryDetail = { id ->
                            onNavigateToMemoryDetail(AppScreen.MemoryDetail(id))
                        },
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCalendarLayout(
    modifier: Modifier = Modifier,
    memoriesState: SectionState<List<MemoryWithMediaModel>>,
    datePickerState: DatePickerState,
    isPreviewMode: Boolean,
    onNavigateToMemoryDetail: (AppScreen) -> Unit,
    onNavigateToMemoryCreate: (AppScreen) -> Unit,
    onNavigateToTagsWithMemories: (AppScreen) -> Unit,
    date: LocalDate,
    windowSizeClass: WindowSizeClass
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).copy(
            // Allow up to 3 panes if space permits
            maxHorizontalPartitions = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND)) 3 else 2,
            // Optional: Adjust horizontal spacing between panes
            horizontalPartitionSpacerSize = 16.dp,
        ),
        adaptStrategies = ThreePaneScaffoldAdaptStrategies(
            primaryPaneAdaptStrategy = AdaptStrategy.Reflow(SupportingPaneScaffoldRole.Main),
            secondaryPaneAdaptStrategy = AdaptStrategy.Reflow(SupportingPaneScaffoldRole.Supporting),
            tertiaryPaneAdaptStrategy = AdaptStrategy.Hide,
        )
    )
    val scope = rememberCoroutineScope()
    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }


    LaunchedEffect(date) {
        if (navigator.currentDestination?.pane == SupportingPaneScaffoldRole.Supporting) {
            navigator.navigateBack()
        }
    }

    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane {
                AnimatedContent(
                    targetState = memoriesState,
                    modifier = Modifier.padding(16.dp),
                    label = "memories_state"
                ) { state ->
                    MemoriesStateContent(
                        memoryState = state,
                        emptyContent = {
                            EmptyResultPlaceHolder(
                                emptyText = "No memories for this date",
                                buttonText = "Create Memory",
                                height = 200.dp,
                                onButtonClick = { onNavigateToMemoryCreate(AppScreen.Memory()) },
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        successContent = { memories ->
                            MemoryList(
                                memories = memories,
                                isPreviewMode = isPreviewMode,
                                onNavigateToMemoryDetail = { id ->
                                    val memoryId = navigator.currentDestination?.contentKey
                                    scope.launch {
                                        navigator.navigateTo(
                                            pane = SupportingPaneScaffoldRole.Supporting,
                                            contentKey = id
                                        )
                                    }
                                },
                            )
                        }
                    )
                }
            }
        },
        extraPane = {
            AnimatedPane {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                val memoryId = navigator.currentDestination?.contentKey
                if (memoryId != null) {
                    MemoryDetailRoot(
                        memoryId = memoryId,
                        onBack = { scope.launch { navigator.navigateBack() } },
                        onTagClick = onNavigateToTagsWithMemories,
                        onNavigateToMemory = onNavigateToMemoryCreate,
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select a memory to view",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MemoryList(
    memories: List<MemoryWithMediaModel>,
    isPreviewMode: Boolean,
    onNavigateToMemoryDetail: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memories) { item ->
            HistoryMemoryCard(
                item = item,
                isPreviewMode = isPreviewMode,
                onViewDetail = {
                    onNavigateToMemoryDetail(item.memory.memoryId)
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryMemoryCard(
    modifier: Modifier = Modifier,
    item: MemoryWithMediaModel,
    isPreviewMode: Boolean,
    showDateHeader: Boolean = false,
    onViewDetail: () -> Unit,
    isExpanded: Boolean = false
) {
    if (item.memory.memoryForTimeStamp == null && !isPreviewMode) return

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(8.dp)
    ) {
        if (showDateHeader) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.memory.memoryForTimeStamp!!.formatTime("dd"),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.memory.memoryForTimeStamp.formatTime("EEE"),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.4f),
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (isPreviewMode) R.drawable.ic_launcher_background else item.mediaList[0].uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                                if (item.mediaList.first().type.isVideoFile()) {
                                    PlayButton(modifier = Modifier.align(Alignment.Center))
                                }
                            }
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HistoryScreenPreview() {
    val now = System.currentTimeMillis()
    val previewMemories = List(10) { i ->
        MemoryWithMediaModel().let { m ->
            m.copy(
                memory = m.memory.copy(
                    memoryId = i.toString(),
                    title = "Memory $i",
                    content = "Preview content $i",
                    timeStamp = now,
                    memoryForTimeStamp = now,
                )
            )
        }
    }
    val previewPagingData = PagingData.from<MemoryListItem>(
        previewMemories.map { MemoryListItem.Entry(it) }
    )
    MemoriesTheme {
        HistoryScreen(
            memoriesState = SectionState.Success(previewMemories),
            timeLineDisplayMode = TimeLineDisplayMode.List,
            memoriesByMonth = flowOf(previewPagingData).collectAsLazyPagingItems(),
        )
    }
}