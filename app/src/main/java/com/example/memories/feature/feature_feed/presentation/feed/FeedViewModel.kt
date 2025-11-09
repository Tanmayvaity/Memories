package com.example.memories.feature.feature_feed.presentation.feed

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCases
import com.google.common.collect.Multimaps.index
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCases: FeedUseCases,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    private val _state = MutableStateFlow<FeedState>(FeedState())
    val state = _state
        .onStart { onEvent(FeedEvents.FetchFeed) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FeedState()
        )


    private val _isDataLoading = MutableStateFlow<Boolean>(false)
    val isDataLoading = _isDataLoading.asStateFlow()

    fun onEvent(event: FeedEvents) {
        when (event) {
            is FeedEvents.FetchFeed -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    feedUseCases.getFeedUseCase(_state.value.type)
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
                    val result = feedUseCases.deleteMemoryUseCase(event.memory)

                    if(result ==1){
                        Log.i(TAG, "FeedEvents Delete : Memory Deleted")
                    }
                }
            }
        }

    }



}