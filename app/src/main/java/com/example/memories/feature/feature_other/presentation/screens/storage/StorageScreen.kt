package com.example.memories.feature.feature_other.presentation.screens.storage

import android.text.format.Formatter
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_other.presentation.screens.storage.components.ManageDataChoiceSheet
import com.example.memories.feature.feature_other.presentation.screens.storage.components.ManageUserDataSheet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.feature.feature_other.domain.model.StorageStats
import com.example.memories.feature.feature_other.presentation.viewmodels.StorageState
import com.example.memories.feature.feature_other.presentation.viewmodels.StorageViewModel
import com.example.memories.ui.theme.MemoriesTheme
import androidx.compose.ui.res.painterResource
import com.example.memories.feature.feature_other.presentation.viewmodels.StorageEvents
import kotlin.math.roundToInt

@Composable
fun StorageRoot(
    onBack: () -> Unit,
    onNavigateToMemoryDetail: (String) -> Unit = {},
    onNavigateToManageMedia: () -> Unit = {},
    viewModel: StorageViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isDeletingCache by viewModel.deletingCache.collectAsStateWithLifecycle()
    val memoryQuery by viewModel.memoryQuery.collectAsStateWithLifecycle()
    val tagQuery by viewModel.tagQuery.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val latestMemories = viewModel.latestMemories.collectAsLazyPagingItems()
    StorageScreen(
        state = state,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        isDeletingCache = isDeletingCache,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
        onNavigateToManageMedia = onNavigateToManageMedia,
        memoryQuery = memoryQuery,
        tagQuery = tagQuery,
        tags = tags,
        recentSearches = recentSearches,
        latestMemories = latestMemories
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    state: StorageState = StorageState(),
    onEvent : (StorageEvents) -> Unit = {},
    onBack: () -> Unit = {},
    isDeletingCache : Boolean = false,
    onNavigateToMemoryDetail: (String) -> Unit = {},
    onNavigateToManageMedia: () -> Unit = {},
    memoryQuery: String = "",
    tagQuery: String = "",
    tags: SectionState<List<TagWithMemoryCountModel>> = SectionState.Loading,
    recentSearches: SectionState<List<MemoryWithMediaModel>> = SectionState.Loading,
    latestMemories: LazyPagingItems<MemoryWithMediaModel> =
        flowOf(PagingData.empty<MemoryWithMediaModel>()).collectAsLazyPagingItems(),
) {
    var showChoiceSheet by remember { mutableStateOf(false) }
    var showUserDataSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                title = {
                    Text(
                        text = "Storage",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    text = "Calculating storage"
                )
            }

            else -> {
                StorageContent(
                    modifier = Modifier.padding(innerPadding),
                    stats = state.stats,
                    isDeletingCache = isDeletingCache,
                    onManageData = { showChoiceSheet = true },
                    onClearCache = {
                        onEvent(StorageEvents.DeleteCache)
                    }
                )
            }
        }
    }

    if (showChoiceSheet) {
        ManageDataChoiceSheet(
            onDismiss = { showChoiceSheet = false },
            onManageMedia = {
                onNavigateToManageMedia()
                showChoiceSheet = false
            },
            onManageUserData = {
                showUserDataSheet = true
                showChoiceSheet = false
            }
        )
    }

    if (showUserDataSheet) {
        ManageUserDataSheet(
            onDismiss = { showUserDataSheet = false },
            onNavigateToMemoryDetail = onNavigateToMemoryDetail,
            memoryQuery = memoryQuery,
            tagQuery = tagQuery,
            tags = tags,
            recentSearches = recentSearches,
            latestMemories = latestMemories,
            onDeleteMemories = {
                onEvent(StorageEvents.DeleteMemories(it))
            },
            onDeleteRecentSearch = {
                onEvent(StorageEvents.DeleteRecentSearch(it))
            },
            onClearAllRecentSearches = {
                onEvent(StorageEvents.ClearAllRecentSearches)
            },
            onTagQueryChange = {
                onEvent(StorageEvents.TagQueryChange(it))
            },
            onDeleteTags = {
                onEvent(StorageEvents.DeleteTags(it))
            },
            onMediaQueryChange = {
                onEvent(StorageEvents.MemoryQueryChange(it))
            }
        )
    }
}

@Composable
private fun StorageContent(
    modifier: Modifier = Modifier,
    stats: StorageStats?,
    isDeletingCache : Boolean = false,
    onManageData: () -> Unit,
    onClearCache: () -> Unit
) {
    val context = LocalContext.current

    val cacheBytes = stats?.cacheBytes ?: 0L
    val userDataBytes = ((stats?.dataBytes ?: 0L) - cacheBytes).coerceAtLeast(0L)
    val totalBytes = stats?.total ?: 0L
    val allocated = userDataBytes + cacheBytes
    val usedFraction = if (allocated > 0L) userDataBytes.toFloat() / allocated else 0f
    val usedPercent = (usedFraction * 100).roundToInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TotalSizeCard(
            totalSize = Formatter.formatShortFileSize(context, totalBytes),
            progress = usedFraction,
            usedPercent = usedPercent
        )

        Text(
            text = "Storage Breakdown",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        BreakdownCard(
            iconRes = R.drawable.ic_database,
            iconTint = MaterialTheme.colorScheme.primary,
            iconBackground = MaterialTheme.colorScheme.primaryContainer,
            title = "User Memories",
            subtitle = "Photos, Videos & Entries",
            size = Formatter.formatShortFileSize(context, userDataBytes),
            action = {
                TextButton(
                    onClick = onManageData,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Manage Data")
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_right),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )

        BreakdownCard(
            iconRes = R.drawable.ic_cache,
            iconTint = MaterialTheme.colorScheme.secondary,
            iconBackground = MaterialTheme.colorScheme.secondaryContainer,
            title = "System Cache",
            subtitle = "Temporary Files & Preloads",
            size = Formatter.formatShortFileSize(context, cacheBytes),
            action = {
                Button(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    if(!isDeletingCache){
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Clear Cache")
                    }else{
                        LoadingIndicator(
                            showText = false,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                }
            }
        )

        Text(
            text = "Clearing cache will free up space on your device but will not delete your " +
                    "saved memories, journal entries, or account information.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TotalSizeCard(
    totalSize: String,
    progress: Float,
    usedPercent: Int
) {
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Total App Size",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = totalSize,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearWavyProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$usedPercent% of allocated storage used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "User Data"
                )
                LegendItem(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    label = "Cache"
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BreakdownCard(
    iconRes: Int,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    subtitle: String,
    size: String,
    action: @Composable () -> Unit
) {
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = size,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}

@PreviewLightDark
@Composable
private fun StorageScreenPreview() {
    MemoriesTheme {
        StorageScreen(
            state = StorageState(
                isLoading = false,
                stats = StorageStats(
                    appBytes = 60L * 1024 * 1024,
                    dataBytes = 1_450L * 1024 * 1024,
                    cacheBytes = 250L * 1024 * 1024,
                    total = 1_510L * 1024 * 1024
                )
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun StorageScreenLoadingPreview() {
    MemoriesTheme {
        StorageScreen(state = StorageState(isLoading = true,), isDeletingCache = true)
    }
}
