package com.example.memories.feature.feature_other.presentation.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import com.example.memories.feature.feature_other.domain.usecase.MediaManagementUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.memories.core.util.toUriOrNull

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ManageMediaViewModel @Inject constructor(
    private val mediaUseCase: MediaManagementUseCaseWrapper
) : ViewModel() {


    val showHidden: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val media: Flow<PagingData<MediaModel>> =
        showHidden
            .flatMapLatest { hidden -> mediaUseCase.getAllMediaPagedUseCase(hidden) }
            .cachedIn(viewModelScope)

    private val _state = MutableStateFlow(ManageMediaState())
    val state: StateFlow<ManageMediaState> = _state.asStateFlow()

    private val _eventChannel = Channel<ManageMediaUiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private var associatedMemoryJob: Job? = null

    fun onEvent(event: ManageMediaEvents) {
        when (event) {
            is ManageMediaEvents.ToggleShowHidden -> showHidden.update { !it }

            is ManageMediaEvents.LoadAssociatedMemory -> {
                associatedMemoryJob?.cancel()
                _state.update { it.copy(associatedMemory = null) }
                associatedMemoryJob = viewModelScope.launch {
                    mediaUseCase.getMemoryByIdUseCase(event.memoryId).collectLatest { memory ->
                        _state.update { it.copy(associatedMemory = memory) }
                    }
                }
            }

            is ManageMediaEvents.ClearAssociatedMemory -> {
                associatedMemoryJob?.cancel()
                _state.update { it.copy(associatedMemory = null) }
            }

            is ManageMediaEvents.ToggleFavourite -> {
                viewModelScope.launch {
                    mediaUseCase.toggleMediaFavouriteUseCase(
                        event.mediaId,
                        event.currentFavouriteState
                    )
                }
            }

            is ManageMediaEvents.DownloadMedia -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true) }
                    val result = downloadSingle(event.uri, event.type)
                    _state.update { it.copy(isDownloading = false) }
                    when (result) {
                        is Result.Success -> _eventChannel.send(
                            ManageMediaUiEvent.ShowToast("Download complete")
                        )

                        is Result.Error -> _eventChannel.send(
                            ManageMediaUiEvent.Error("Cannot download media")
                        )
                    }
                }
            }

            is ManageMediaEvents.ShareMedia -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSharing = true) }
                    val uri = shareableUriFor(event.uri, event.type)
                    _state.update { it.copy(isSharing = false) }
                    if (uri != null) {
                        _eventChannel.send(ManageMediaUiEvent.ShowShareChooser(uri))
                    } else {
                        _eventChannel.send(ManageMediaUiEvent.Error("Cannot share media"))
                    }
                }
            }

            is ManageMediaEvents.DownloadMultiple -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true) }
                    var success = 0
                    event.media.forEach { item ->
                        val result = downloadSingle(item.uri.toUriOrNull(), item.type)
                        if (result is Result.Success) success++
                    }
                    _state.update { it.copy(isDownloading = false) }
                    _eventChannel.send(
                        ManageMediaUiEvent.ShowToast("Downloaded $success of ${event.media.size}")
                    )
                }
            }

            is ManageMediaEvents.ShareMultiple -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSharing = true) }
                    val uris = event.media.mapNotNull { item ->
                        shareableUriFor(item.uri.toUriOrNull(), item.type)
                    }
                    _state.update { it.copy(isSharing = false) }
                    if (uris.isNotEmpty()) {
                        _eventChannel.send(ManageMediaUiEvent.ShowMultiShareChooser(uris))
                    } else {
                        _eventChannel.send(ManageMediaUiEvent.Error("Cannot share media"))
                    }
                }
            }

            is ManageMediaEvents.PlayVideo -> {
                viewModelScope.launch {
                    val uri = shareableUriFor(event.uri, Type.VIDEO_MP4)
                    if (uri != null) {
                        _eventChannel.send(ManageMediaUiEvent.ShowMediaChooser(uri))
                    } else {
                        _eventChannel.send(ManageMediaUiEvent.Error("Cannot play video"))
                    }
                }
            }
        }
    }

    private suspend fun downloadSingle(uri: Uri?, type: Type): Result<String> {
        if (uri == null || type.isUnknownType()) {
            return Result.Error(Throwable("Unsupported media"))
        }
        return if (type.isImageFile()) {
            mediaUseCase.downloadWithBitmapUseCase(uri = uri, shaderCode = null)
        } else {
            mediaUseCase.downloadVideoUseCase(uri)
        }
    }

    private suspend fun shareableUriFor(uri: Uri?, type: Type): Uri? {
        if (uri == null) return null
        val result = mediaUseCase.generateShareableUriUseCase(type.isImageFile(), uri)
        return (result as? Result.Success)?.data
    }



    companion object {
        private const val TAG = "ManageMediaViewModel"
    }
}

data class ManageMediaState(
    val isDownloading: Boolean = false,
    val isSharing: Boolean = false,
    val associatedMemory: MemoryWithMediaModel? = null,
)

sealed interface ManageMediaEvents {
    object ToggleShowHidden : ManageMediaEvents
    data class LoadAssociatedMemory(val memoryId: String) : ManageMediaEvents
    object ClearAssociatedMemory : ManageMediaEvents
    data class ToggleFavourite(
        val mediaId: String,
        val currentFavouriteState: Boolean
    ) : ManageMediaEvents

    data class DownloadMedia(val uri: Uri, val type: Type) : ManageMediaEvents
    data class ShareMedia(val uri: Uri, val type: Type) : ManageMediaEvents
    data class DownloadMultiple(val media: List<MediaModel>) : ManageMediaEvents
    data class ShareMultiple(val media: List<MediaModel>) : ManageMediaEvents
    data class PlayVideo(val uri: Uri) : ManageMediaEvents
}

sealed interface ManageMediaUiEvent {
    data class ShowToast(val message: String) : ManageMediaUiEvent
    data class Error(val message: String) : ManageMediaUiEvent
    data class ShowShareChooser(val uri: Uri) : ManageMediaUiEvent
    data class ShowMultiShareChooser(val uris: List<Uri>) : ManageMediaUiEvent
    data class ShowMediaChooser(val uri: Uri) : ManageMediaUiEvent
}
