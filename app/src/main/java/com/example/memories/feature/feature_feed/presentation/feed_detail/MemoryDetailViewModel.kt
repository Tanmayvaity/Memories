package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.net.Uri
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
) : ViewModel() {

    private val _state = MutableStateFlow(MemoryDetailState())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()
    private val _eventChannel = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        Log.d(TAG, "inside init")
        savedStateHandle.get<String>("memoryId")?.let { itemId ->
            Log.d(TAG, "MemoryDetailViewModel-saveStateHandle : ${itemId}")
            onEvent(MemoryDetailEvents.Fetch(itemId))
        }
    }


    fun onEvent(event: MemoryDetailEvents) {
        when (event) {
            is MemoryDetailEvents.Fetch -> {
                _isLoading.update { true }
                viewModelScope.launch {
                    val memory = feedUseCases.getMemoryByIdUseCase(event.id)
                    if (memory != null) {
                        _state.update {
                            it.copy(memory = memory)
                        }
                    }
                    _isLoading.update { false }
                    Log.d(TAG, "MemoryDetailEvents.Fetch : ${memory.toString()}")
                }

            }

            is MemoryDetailEvents.FavoriteToggle -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            memory = it.memory?.copy(
                                memory = it.memory.memory.copy(favourite = !event.isFavourite)
                            )
                        )
                    }
                    feedUseCases.toggleFavouriteUseCase(
                        event.id,
                        isFavourite = !event.isFavourite
                    )
                }

            }

            is MemoryDetailEvents.HiddenToggle -> {
                viewModelScope.launch {


                    _state.update {
                        it.copy(
                            memory = it.memory?.copy(
                                memory = it.memory.memory.copy(hidden = event.isHidden)
                            )
                        )
                    }
                    feedUseCases.toggleHiddenUseCase(
                        event.id,
                        isHidden = event.isHidden
                    )
                    _eventChannel.send(
                        UiEvent.ShowToast(
                            message = if (_state.value.memory!!.memory.hidden) "Memory hidden" else "Memory Shown",
                            type = UiEvent.ToastType.HIDDEN
                        )
                    )

                }
            }

            is MemoryDetailEvents.Delete -> {
                if(state.value.memory?.memory == null) return

                _isDeleting.update { true }
                viewModelScope.launch {
                    val result = feedUseCases.deleteMemoryUseCase(
                        memory = _state.value.memory!!.memory,
                        uriList = _state.value.memory!!.mediaList.map { it.uri }
                    )
                    _isDeleting.update { false }
                    when (result) {
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: error while deleting")
                            _eventChannel.send(
                                UiEvent.Error(
                                    message = "Cannot Delete Memory",
                                )
                            )
                        }

                        is Result.Success<String> -> {
                            Log.i(TAG, "MemoryDetailEvents.Delete : Memory deleted")
                            _eventChannel.send(
                                UiEvent.ShowToast(
                                    message = "Memory deleted",
                                    type = UiEvent.ToastType.DELETE
                                )
                            )
                        }
                    }
                }
            }

            is MemoryDetailEvents.DownloadImage -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true) }
                    val result = feedUseCases.downloadWithBitmapUseCase(
                        uri = event.uri,
                        shaderCode = null
                    )

                    when(result){
                        is Result.Error -> {
                            _eventChannel.send(
                                UiEvent.Error(
                                    message = "Cannot Download Image",
                                )
                            )
                            Log.e(TAG, "onEvent: ${result.error.message}", )
                            _state.update { it.copy(isDownloading = false) }
                        }
                        is Result.Success -> {
                            _eventChannel.send(
                                UiEvent.ShowToast(
                                    message = "Download Complete",
                                    type = UiEvent.ToastType.DOWNLOAD
                                )
                            )
                            _state.update { it.copy(isDownloading = false) }
                        }
                    }

                }
            }

            is MemoryDetailEvents.ShareImage -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSharing = true) }
                    val result = feedUseCases.saveToCacheStorageWithUriUseCase(event.uri)
                    when(result){
                        is Result.Error -> {
                            _eventChannel.send(
                                UiEvent.Error(
                                    message = "Cannot Share Image",
                                )
                            )
                            _state.update { it.copy(isSharing = false) }
                            Log.e(TAG, "onEvent: ${result.error.message}", )
                        }

                        is Result.Success<Uri> -> {
                            _state.update { it.copy(isSharing = false) }
                            _eventChannel.send(UiEvent.ShowShareChooser(result.data))
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


data class MemoryDetailState(
    val memory: MemoryWithMediaModel? = null,
    val isDownloading: Boolean = false,
    val isSharing : Boolean = false
)