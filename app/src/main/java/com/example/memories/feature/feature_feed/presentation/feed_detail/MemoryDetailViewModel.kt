package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.util.CoilUtils.result
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction
import com.example.memories.feature.feature_feed.presentation.common.MemoryActionHandler
import com.example.memories.feature.feature_feed.presentation.feed_detail.UiEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    val feedUseCases: FeedUseCaseWrapper,
    savedStateHandle: SavedStateHandle,
    val memoryActionHandler: MemoryActionHandler
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
                    memory.collectLatest { memoryItem ->
                        if (memoryItem != null) {
                            _state.update {
                                it.copy(memory = memoryItem)
                            }
                        }
                        _isLoading.update { false }
                        Log.d(TAG, "MemoryDetailEvents.Fetch : ${memoryItem}")
                    }


                }

            }

            is MemoryDetailEvents.Action -> {
                viewModelScope.launch {
                    when (event.action) {
                        is MemoryAction.Delete -> {
                            if (state.value.memory?.memory == null) return@launch
                            _isDeleting.update { true }
                            val result = memoryActionHandler.delete(
                                memory = _state.value.memory!!.memory,
                                uriList = _state.value.memory!!.mediaList.map { it.uri }
                            )
                            _isDeleting.update { false }
                            when (result) {
                                is Result.Error -> {
                                    Log.e(TAG, "onEvent: error while deleting")
                                    _eventChannel.send(
                                        Error(
                                            message = "Cannot Delete Memory",
                                        )
                                    )
                                }

                                is Result.Success<String> -> {
                                    Log.i(TAG, "MemoryDetailEvents.Delete : Memory deleted")
                                    _eventChannel.send(
                                        ShowToast(
                                            message = "Memory deleted",
                                            type = UiEvent.ToastType.DELETE
                                        )
                                    )
                                }

                            }
                        }

                        is MemoryAction.ToggleFavourite -> {
                            _state.update {
                                it.copy(
                                    memory = it.memory?.copy(
                                        memory = it.memory.memory.copy(favourite = !event.action.currentFavouriteState)
                                    )
                                )
                            }
                            memoryActionHandler.handle(event.action)
                        }

                        is MemoryAction.ToggleHidden -> {
                            _state.update {
                                it.copy(
                                    memory = it.memory?.copy(
                                        memory = it.memory.memory.copy(hidden = !event.action.currentHiddenState)
                                    )
                                )
                            }
                            memoryActionHandler.handle(event.action)
                            _eventChannel.send(
                                ShowToast(
                                    message = if (_state.value.memory!!.memory.hidden) "Memory hidden" else "Memory Shown",
                                    type = UiEvent.ToastType.HIDDEN
                                )
                            )
                        }
                    }


                }

            }

            is MemoryDetailEvents.DownloadMedia -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true) }

                    if(event.type.isUnknownType()){
                        _eventChannel.send(
                            Error(
                                message = "Cannot Download this media type",
                            )
                        )
                        Log.e(TAG, "unknown type : ${event.type}")
                        _state.update { it.copy(isDownloading = false) }
                    }



                    val result = if (event.type.isImageFile()) {
                        feedUseCases.downloadWithBitmapUseCase(
                            uri = event.uri,
                            shaderCode = null
                        )
                    } else {
                        feedUseCases.downloadVideoUseCase(event.uri)
                    }


                    when (result) {
                        is Result.Error -> {
                            _eventChannel.send(
                                Error(
                                    message = "Cannot Download Media",
                                )
                            )
                            Log.e(TAG, "onEvent: ${result.error.message}")
                            _state.update { it.copy(isDownloading = false) }
                        }

                        is Result.Success -> {
                            _eventChannel.send(
                                ShowToast(
                                    message = "Download Complete",
                                    type = UiEvent.ToastType.DOWNLOAD
                                )
                            )
                            _state.update { it.copy(isDownloading = false) }
                        }
                    }

                }
            }

            is MemoryDetailEvents.ShareMedia -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSharing = true) }
                    val result = feedUseCases.getShareableUriUseCase(null, event.uri)
                    when (result) {
                        is Result.Error -> {
                            _eventChannel.send(
                                Error(
                                    message = "Cannot Share Image",
                                )
                            )
                            _state.update { it.copy(isSharing = false) }
                            Log.e(TAG, "onEvent: ${result.error.message}")
                        }

                        is Result.Success<Uri?> -> {
                            _state.update { it.copy(isSharing = false) }
                            if (result.data != null) {
                                _eventChannel.send(ShowShareChooser(result.data))
                            } else {
                                _eventChannel.send(
                                    Error(
                                        message = "Cannot Share Image please try again",
                                    )
                                )
                            }

                        }

                    }

                }
            }

            is MemoryDetailEvents.PlayVideo -> {
                viewModelScope.launch {
                    val result = feedUseCases.getShareableUriUseCase(null, event.uri)
                    if (result is Result.Success && result.data != null) {
                        _eventChannel.send(ShowMediaChooser(result.data))
                    } else {
                        _eventChannel.send(
                            Error(
                                message = "Cannot Play Video please try again",
                            )
                        )
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
    val isSharing: Boolean = false
)