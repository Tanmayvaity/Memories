package com.example.memories.feature.feature_feed.presentation.history

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.memories.feature.feature_feed.domain.usecase.history_usecase.HistoryUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.toSectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCaseWrapper: HistoryUseCaseWrapper
) : ViewModel() {

    val date: StateFlow<LocalDate?>
        field = MutableStateFlow(null)

    val currentTimeLineDisplayMode: StateFlow<TimeLineDisplayMode>
        field = MutableStateFlow(TimeLineDisplayMode.List)


    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("NewApi")
    val memoriesByDate = date
        .flatMapLatest { date ->
            if (date == null) flowOf(emptyList())
            else historyUseCaseWrapper.fetchTodayMemoriesUseCase(date)
        }
        .toSectionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SectionState.Loading
        )


    @RequiresApi(Build.VERSION_CODES.O)
    val memoriesByMonth = historyUseCaseWrapper.fetchAllMemories()
        .map { pagingData ->
            pagingData.map { memory ->
                MemoryListItem.Entry(memory)
            }
                .insertSeparators { before, after ->
                    when {
                        after == null -> null
                        before == null && after is MemoryListItem.Entry -> {
                            MemoryListItem.MonthHeader(
                                after.yearMonth(),
                                anchorId = after.memory.memory.memoryId
                            )
                        }

                        before is MemoryListItem.Entry &&
                                after is MemoryListItem.Entry &&
                                before.yearMonth() != after.yearMonth() ->
                            MemoryListItem.MonthHeader(
                                after.yearMonth(),
                                anchorId = after.memory.memory.memoryId
                            )

                        else -> null
                    }
                }
        }
        .catch { e ->
            e.printStackTrace()
            Log.e("HistoryViewModel", "error while fetching memories: ${e.message}")
        }
        .cachedIn(viewModelScope)


    fun onDateChange(date: LocalDate) {
        this.date.update { date }
    }

    fun onTabChange(tab: TimeLineDisplayMode) {
        currentTimeLineDisplayMode.update { tab }
    }


}