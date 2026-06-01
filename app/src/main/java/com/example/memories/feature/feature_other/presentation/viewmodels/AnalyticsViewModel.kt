package com.example.memories.feature.feature_other.presentation.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_other.domain.AnalyticsCalculator
import com.example.memories.feature.feature_other.domain.model.AnalyticsData
import com.example.memories.feature.feature_other.domain.model.AnalyticsPeriod
import com.example.memories.feature.feature_other.domain.usecase.AnalyticsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val useCases: AnalyticsUseCases
) : ViewModel() {

    val period: StateFlow<AnalyticsPeriod>
        field = MutableStateFlow(AnalyticsPeriod.WEEK)

    val state: StateFlow<AnalyticsState> = combine(
        useCases.getDailyStats(),
        useCases.getMediaBreakdown(),
        useCases.getTopTags(),
        useCases.getTotalMemoryCount(),
        period
    ) { daily, media, tags, total, selectedPeriod ->
        if (total == 0) {
            AnalyticsState.Empty
        } else {
            AnalyticsState.Data(
                AnalyticsCalculator.compute(daily, media, tags, total, selectedPeriod)
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsState.Loading
        )

    fun setPeriod(value: AnalyticsPeriod) {
        period.update { value }
    }
}

sealed interface AnalyticsState {
    data object Loading : AnalyticsState
    data object Empty : AnalyticsState
    data class Data(val analytics: AnalyticsData) : AnalyticsState
}
