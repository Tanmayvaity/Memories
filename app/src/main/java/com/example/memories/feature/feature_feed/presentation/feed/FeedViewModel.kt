package com.example.memories.feature.feature_feed.presentation.feed

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCases
import com.google.common.collect.Multimaps.index
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCases: FeedUseCases
) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    private val _state = MutableStateFlow<FeedState>(FeedState())
    val state = _state.asStateFlow()

    private val _isDataLoading =  MutableStateFlow<Boolean>(false)
    val isDataLoading = _isDataLoading.asStateFlow()

    fun onEvent(event: FeedEvents) {
        when (event) {
            FeedEvents.FetchFeed -> {
                viewModelScope.launch {
                   fetchData()
                }
            }

            FeedEvents.Refresh ->{
                viewModelScope.launch {
                    _isDataLoading.update{true}
                    fetchData()
                    _isDataLoading.update { false }
                }
            }

            is FeedEvents.ToggleFavourite -> {
                viewModelScope.launch {
                    updateFeedStateActionItems(updateFavourite = true, id = event.id)

                    val item = _state.value.memories.find { it.memory.memoryId == event.id }
                    Log.d(TAG, "_state.value.memories : ${_state.value.memories.size}")
                    if (item == null) {
                        Log.e(TAG, "onEvent: invalid id")
                        return@launch
                    }
                    feedUseCases.toggleFavouriteUseCase(
                        id = event.id,
                        isFavourite = item.memory.favourite
                    )
                }
            }

            is FeedEvents.ToggleHidden -> {
                viewModelScope.launch {
                    val currentHiddenStatus = _state.value.memories.find { it.memory.memoryId == event.id }?.memory?.hidden
                    updateFeedStateActionItems(updateFavourite = false, id = event.id)
                    Log.d(TAG, "_state.value.memories : ${_state.value.memories.size}")
                    if (currentHiddenStatus == null) {
                        Log.e(TAG, "onEvent: invalid id")
                        return@launch
                    }
                    feedUseCases.toggleHiddenUseCase(
                        id = event.id,
                        isHidden = !currentHiddenStatus
                    )
                }
            }
        }

    }

    private suspend  fun fetchData(){
        val memoryWithMediaList: List<MemoryWithMediaModel> =
            feedUseCases.getFeedUseCase()

        val filterList =
            memoryWithMediaList.filter { it !in _state.value.memories }

        if (filterList.isNotEmpty()) {
            _state.update {
                it.copy(
                    memories = _state.value.memories + memoryWithMediaList
                )
            }
        }
    }



    private fun updateFeedStateActionItems(
        updateFavourite: Boolean = false,
        id: String
    ) {
        _state.update { currentState ->
            val index = currentState.memories.indexOfFirst { it.memory.memoryId == id }
            if (index == -1) return@update currentState // Not found, return unchanged

            val updatedList = currentState.memories.toMutableList().apply {
                if (updateFavourite) {
                    this[index] = this[index].copy(
                        memory = this[index].memory.copy(
                            favourite = !this[index].memory.favourite
                        )
                    )
                } else {
                    this[index] = this[index].copy(
                        memory = this[index].memory.copy(
                            hidden = !this[index].memory.hidden
                        )
                    )
                }
            }

            val filterList = if(updateFavourite) updatedList else updatedList.filter { it.memory.memoryId !=id }
            Log.d(TAG, "updateFeedStateActionItems _state.value.memories : ${_state.value.memories.size}")

            currentState.copy(memories = filterList)
        }

    }

}