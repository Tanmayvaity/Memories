package com.example.memories.feature.feature_other.presentation.screens.analytics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.feature.feature_other.domain.model.AnalyticsData
import com.example.memories.feature.feature_other.domain.model.AnalyticsPeriod
import com.example.memories.feature.feature_other.domain.model.StreakInfo
import com.example.memories.feature.feature_other.presentation.screens.analytics.components.CalendarHeatmap
import com.example.memories.feature.feature_other.presentation.screens.analytics.components.MediaBreakdownBar
import com.example.memories.feature.feature_other.presentation.screens.analytics.components.MemoriesBarChart
import com.example.memories.feature.feature_other.presentation.screens.analytics.components.TopTagsBars
import com.example.memories.feature.feature_other.presentation.screens.analytics.components.WordTrendChart
import com.example.memories.feature.feature_other.presentation.viewmodels.AnalyticsState
import com.example.memories.feature.feature_other.presentation.viewmodels.AnalyticsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalyticsRoot(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    AnalyticsScreen(
        state = state,
        period = period,
        onPeriodChange = viewModel::setPeriod,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    state: AnalyticsState,
    period: AnalyticsPeriod,
    onPeriodChange: (AnalyticsPeriod) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                title = { Text("Insights", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { innerPadding ->
        when (state) {
            is AnalyticsState.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                text = "Crunching your memories"
            )

            is AnalyticsState.Empty -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add some memories to see your insights",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is AnalyticsState.Data -> AnalyticsContent(
                modifier = Modifier.padding(innerPadding),
                data = state.analytics,
                period = period,
                onPeriodChange = onPeriodChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsContent(
    data: AnalyticsData,
    period: AnalyticsPeriod,
    onPeriodChange: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        StreakCard(streak = data.streak, total = data.total)

        ChartCard(title = "Activity") {
            CalendarHeatmap(days = data.heatmap)
        }

        ChartCard(
            title = "Memories",
            action = {
                PeriodToggle(period = period, onChange = onPeriodChange)
            }
        ) {
            MemoriesBarChart(data = data.perPeriod)
        }

        ChartCard(title = "Words written") {
            WordTrendChart(data = data.wordTrend)
        }

        ChartCard(title = "Media breakdown") {
            MediaBreakdownBar(media = data.media)
        }

        ChartCard(title = "Most-used tags") {
            TopTagsBars(tags = data.topTags)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ChartCard(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                action?.invoke()
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodToggle(
    period: AnalyticsPeriod,
    onChange: (AnalyticsPeriod) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        AnalyticsPeriod.entries.forEachIndexed { index, p ->
            SegmentedButton(
                selected = period == p,
                onClick = { onChange(p) },
                shape = SegmentedButtonDefaults.itemShape(index, AnalyticsPeriod.entries.size),
                label = { Text(if (p == AnalyticsPeriod.WEEK) "Weekly" else "Monthly") }
            )
        }
    }
}

@Composable
private fun StreakCard(streak: StreakInfo, total: Int) {
    SettingCard(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StreakStat(value = "${streak.current}", label = "Current streak")
            StreakStat(value = "${streak.longest}", label = "Longest streak")
            StreakStat(value = "$total", label = "Total memories")
        }
    }
}

@Composable
private fun StreakStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}
