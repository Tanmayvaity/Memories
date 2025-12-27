package com.example.memories.feature.feature_feed.presentation.feed

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.OrderByType
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCases: FeedUseCaseWrapper,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    private val _state = MutableStateFlow<FeedState>(FeedState())
    val state = _state
        .onStart { onEvent(FeedEvents.FetchFeed) }
        .onCompletion {
            _state.update { it.copy(isLoading = false) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FeedState()
        )




    private val _isDataLoading = MutableStateFlow<Boolean>(false)
    val isDataLoading : StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun onEvent(event: FeedEvents) {
        when (event) {
            is FeedEvents.FetchFeed -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    feedUseCases.getFeedUseCase(
                        type = state.value.type,
                        sortType = state.value.sortType,
                        orderByType = state.value.orderByType
                    )
                        .collectLatest { itemList ->
                        _state.update { it ->
                            it.copy(
                                memories = itemList,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            }

            is FeedEvents.ChangeFetchType ->{
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

            is FeedEvents.ChangeOrderByType -> {
                _state.update {
                    it.copy(orderByType = event.type)
                }
            }

            is FeedEvents.ResetFilterState -> {
                _state.update {
                    it.copy(
                        type = FetchType.ALL,
                        sortType = SortType.DateAdded,
                        orderByType = OrderByType.Descending
                    )
                }
            }


            FeedEvents.Refresh -> {
//                viewModelScope.launch {
//                    _isDataLoading.update { true }
//                    fetchData()
//                    _isDataLoading.update { false }
//                }
            }

            is FeedEvents.ToggleFavourite -> {
                viewModelScope.launch {
                    feedUseCases.toggleFavouriteUseCase(
                        id = event.id,
                        isFavourite = event.isFav
                    )

                }
            }

            is FeedEvents.ToggleHidden -> {
                viewModelScope.launch {
                    feedUseCases.toggleHiddenUseCase(
                        id = event.id,
                        isHidden = event.isHidden
                    )
                }
            }

            is FeedEvents.Delete -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDeleting = true) }
                    val result = feedUseCases.deleteMemoryUseCase(event.memory,event.uriList)
                    when(result){
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: Error while deleting", )
                        }
                        is Result.Success<String> -> {
                            Log.i(TAG, "onEvent: Deleted Succesfully")
                        }
                    }
                    _state.update { it.copy(isDeleting = false) }
                }
            }
        }

    }



}