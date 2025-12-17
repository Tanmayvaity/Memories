package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    val feedUseCases: FeedUseCaseWrapper,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _memory = MutableStateFlow(MemoryWithMediaModel())
    val memory = _memory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _eventChannel  = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        Log.d(TAG, "inside init")
        savedStateHandle.get<String>("memoryId")?.let { itemId ->
            Log.d(TAG, "MemoryDetailViewModel-saveStateHandle : ${itemId}")
            onEvent(MemoryDetailEvents.Fetch(itemId))
        }
    }



    fun onEvent(event: MemoryDetailEvents){
        when(event){
            is MemoryDetailEvents.Fetch -> {
                _isLoading.update { true }
                viewModelScope.launch {
                    val memory = feedUseCases.getMemoryByIdUseCase(event.id)
                    if(memory!=null){
                        _memory.update {
                            memory
                        }
                    }
                    _isLoading.update { false }
                    Log.d(TAG, "MemoryDetailEvents.Fetch : ${memory.toString()}")
                }

            }

            is MemoryDetailEvents.FavoriteToggle -> {
                viewModelScope.launch {
                    _memory.update { it.copy(
                        memory = it.memory.copy(favourite = !event.isFavourite)
                    ) }
                    feedUseCases.toggleFavouriteUseCase(
                        event.id,
                        isFavourite = !event.isFavourite
                    )
                }

            }
            is MemoryDetailEvents.HiddenToggle -> {
                viewModelScope.launch {
                    _memory.update { it.copy(
                        memory = it.memory.copy(hidden = event.isHidden)
                    ) }
                    feedUseCases.toggleHiddenUseCase(
                        event.id,
                        isHidden = event.isHidden
                    )
                    _eventChannel.send(UiEvent.ShowToast(
                        message = if(_memory.value.memory.hidden) "Memory hidden" else "Memory Shown",
                        type = UiEvent.ToastType.HIDDEN
                    ))

                }
            }

            is MemoryDetailEvents.Delete -> {
                viewModelScope.launch {
                    val result = feedUseCases.deleteMemoryUseCase(
                        memory = _memory.value.memory,
                        uriList = _memory.value.mediaList.map { it.uri }
                        )
                    when(result){
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: error while deleting", )
                        }
                        is Result.Success<String> -> {
                            Log.i(TAG, "MemoryDetailEvents.Delete : Memory deleted")
                            _eventChannel.send(UiEvent.ShowToast(
                                message = "Memory deleted",
                                type = UiEvent.ToastType.DELETE
                            ))
                        }
                    }

                }
            }
        }


    }

    companion object {
        private const val TAG = "MemoryDetailViewModel"
    }

}