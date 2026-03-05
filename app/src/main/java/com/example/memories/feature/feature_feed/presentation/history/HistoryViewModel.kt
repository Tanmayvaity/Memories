package com.example.memories.feature.feature_feed.presentation.history

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_feed.domain.usecase.history_usecase.FetchTodayMemoriesUseCase
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.toSectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val fetchTodayMemoriesUseCase: FetchTodayMemoriesUseCase
) : ViewModel() {

    val date: StateFlow<LocalDate?>
        field = MutableStateFlow(null)


    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("NewApi")
    val memories = date
        .flatMapLatest { date ->
            if (date == null) flowOf(emptyList())
            else fetchTodayMemoriesUseCase(date)
        }
        .toSectionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SectionState.Loading
        )

    fun onDateChange(date: LocalDate) {
        this.date.update { date }
    }


}