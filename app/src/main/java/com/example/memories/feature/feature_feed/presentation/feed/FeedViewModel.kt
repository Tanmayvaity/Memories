package com.example.memories.feature.feature_feed.presentation.feed

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.common.MemoryActionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCases: FeedUseCaseWrapper,
    val savedStateHandle: SavedStateHandle,
    val memoryActionHandler: MemoryActionHandler
) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    data class FilterState(
        val fetchType: FetchType,
        val sortType: SortType,
        val orderByType: SortOrder
    )

    private val _state = MutableStateFlow<FeedState>(FeedState())
    val state = _state.asStateFlow()

    private val _appliedFilters = MutableStateFlow(
        FilterState(
            fetchType = FetchType.ALL,
            sortType = SortType.DateAdded,
            orderByType = SortOrder.Descending
        )
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    val memories: Flow<PagingData<MemoryWithMediaModel>> = _appliedFilters
        .flatMapLatest { filters ->
            feedUseCases.getFeedUseCase(filters.fetchType, filters.sortType, filters.orderByType)
        }
        .cachedIn(viewModelScope)

    init {
        Log.d(TAG, "FeedViewModel created: ${hashCode()}")

    }

    fun onEvent(event: FeedEvents) {
        when (event) {
            is FeedEvents.ApplyFilter -> {
                _appliedFilters.update {
                    it.copy(
                        fetchType = state.value.type,
                        sortType = state.value.sortType,
                        orderByType = state.value.orderByType
                    )
                }
            }

            is FeedEvents.ChangeFetchType -> {
                _state.update {
                    it.copy(type = event.type)
                }
            }

            is FeedEvents.ChangeSortType -> {
                _state.update {
                    it.copy(sortType = event.type)
                }
                Log.d(TAG, "ChangeSortType :${state.value.sortType}")
            }

            is FeedEvents.ChangeSortOrderBy -> {
                _state.update {
                    it.copy(orderByType = event.type)
                }
            }

            is FeedEvents.ResetFilterState -> {
                _state.update {
                    it.copy(
                        type = FetchType.ALL,
                        sortType = SortType.DateAdded,
                        orderByType = SortOrder.Descending
                    )
                }
            }


            FeedEvents.Refresh -> {

            }

            is FeedEvents.Action -> {
                viewModelScope.launch {
                    memoryActionHandler.handle(event.action)
                }
            }
        }

    }


}