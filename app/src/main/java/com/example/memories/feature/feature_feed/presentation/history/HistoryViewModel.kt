package com.example.memories.feature.feature_feed.presentation.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.UiState
import com.example.memories.feature.feature_feed.domain.usecase.history_usecase.FetchTodayMemoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val fetchTodayMemoriesUseCase: FetchTodayMemoriesUseCase
) : ViewModel() {


    val state: StateFlow<UiState<MemoryWithMediaModel>>
        field = MutableStateFlow(UiState())

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchMemories(date: LocalDate) {
        if (date == null) return

        state.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            fetchTodayMemoriesUseCase(date)
                .catch { e ->
                    state.update {
                        it.copy(
                            isLoading = false,
                            error = e
                        )
                    }

                }
                .collect { memories ->
                state.update {
                    it.copy(
                        data = memories,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }


}