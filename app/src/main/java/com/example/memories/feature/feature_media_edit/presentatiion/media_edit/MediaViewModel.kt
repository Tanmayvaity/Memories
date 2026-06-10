package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.domain.model.Result
import com.example.memories.core.presentation.UiState
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    val mediaUseCases: MediaUseCases
) : ViewModel() {
    companion object {
        private const val TAG = "MediaViewModel"
    }


    private val _state = MutableStateFlow(EditorState())
    val state = _state.asStateFlow()

//    private val _remoteImagesState = MutableStateFlow<UiState<List<Photos>>>(UiState.Loading)
//    val remoteImagesState = _remoteImagesState.asStateFlow()

    val remoteImages = mediaUseCases.fetchRemoteImagesUseCase()
        .catch { e ->
            Log.e(TAG, "error : ${e}")
        }
        .cachedIn(viewModelScope)
    val remoteVideos = mediaUseCases.fetchRemoteVideosUseCase()
        .catch { e ->
            Log.e(TAG, "error : ${e}")
        }
        .cachedIn(viewModelScope)


    private val _downloadEvents = Channel<MediaEditOneTimeEvents>()
    val downloadEvents = _downloadEvents.receiveAsFlow()


    fun onEvent(event: MediaEvents) {
        when (event) {
            is MediaEvents.EditToolStateChange -> {
                val previousTool = _state.value.initialActiveTool
                _state.update {
                    it.copy(
                        initialActiveTool = event.tool,
                        initialPreviousTool = previousTool
                    )
                }
            }

            is MediaEvents.FilterTypeStateChange -> {
                val currentPageState = _state.value.adjustStateMap[event.page] ?: AdjustState()
                val filterType = event.filterType
                _state.update {
                    it.copy(
                        adjustStateMap = it.adjustStateMap + (event.page to currentPageState.copy(
                            filterType = filterType
                        ))
                    )
                }
            }


            is MediaEvents.AdjustTypeStateChange -> {
                val currentPageState = _state.value.adjustStateMap[event.page] ?: AdjustState()

                currentPageState.let { adjustState ->
                    val adjustType = event.adjustType
                    _state.update {
                        it.copy(
                            adjustStateMap = it.adjustStateMap + (event.page to adjustState.copy(
                                currentAdjustType = adjustType
                            ))
                        )
                    }
                }
            }

            is MediaEvents.AdjustTypeValueChange -> {
                _state.update { st ->
                    val adjustState = st.adjustStateMap[event.page] ?: AdjustState()
                    val newValues = adjustState.adjustTypeValues +
                            (adjustState.currentAdjustType to event.value)
                    val step = mediaUseCases.composeShaderUseCase(
                        adjustState.filterType,
                        newValues
                    )
                    st.copy(
                        adjustStateMap = st.adjustStateMap + (event.page to adjustState.copy(
                            adjustTypeValues = newValues,
                            shaderStep = step
                        ))
                    )
                }
            }

            is MediaEvents.ChangeRotation -> {

                val currentPageState = _state.value.adjustStateMap[event.page] ?: AdjustState()

                currentPageState.let { adjustState ->
                    val currentRotation = adjustState.rotationDegrees
                    val targetRotation = when (event.direction) {
                        RotationDirection.LEFT -> currentRotation - event.value
                        RotationDirection.RIGHT -> currentRotation + event.value
                    }
                    _state.update {
                        it.copy(
                            adjustStateMap = it.adjustStateMap +
                                    (event.page to adjustState.copy(
                                        rotationDegrees = targetRotation
                                    ))
                        )
                    }
                }


            }

            is MediaEvents.DownloadMedia -> {
                _state.update { it.copy(isDownloading = true) }
                viewModelScope.launch {
                    Log.d(TAG, "onEvent: DownloadImage called")

                    if (event.uri == null) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("No Media Selected")
                        )
                        _state.update { it.copy(isDownloading = false) }
                        return@launch
                    }


                    if (state.value.uriMap[event.page] == null) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("Media Null,Please try again")
                        )
                        _state.update { it.copy(isDownloading = false) }
                        return@launch
                    }

                    val result = if (state.value.uriMap[event.page]?.type?.isImageFile() == true) {
                        mediaUseCases.downloadWithBitmap(
                            event.uri,
//                        state.value.shaderList[event.page],
                            state.value.adjustStateMap[event.page]?.shaderStep?.shaderCode ?: null,
                            event.degrees
                        )
                    } else {
                        mediaUseCases.downloadVideoUseCase(event.uri)
                    }
                    when (result) {
                        is Result.Error -> {
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowSnackBar(result.error.message.toString())
                            )
                            _state.update { it.copy(isDownloading = false) }
                        }

                        is Result.Success -> {
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowSnackBar(result.data.toString())
                            )
                            _state.update { it.copy(isDownloading = false) }
                        }
                    }
                }
            }

            is MediaEvents.ShareImage -> {
                _state.update { it.copy(isSharing = true) }
                viewModelScope.launch {

                    if (event.uri == null) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("No Media Selected")
                        )
                        _state.update { it.copy(isSharing = false) }
                        return@launch
                    }

                    val media = state.value.uriMap[event.page]
                    if (media == null) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("Media Null,Please try again")
                        )
                        _state.update { it.copy(isDownloading = false) }
                        return@launch
                    }

                    val shader = state.value.adjustStateMap[event.page]?.shaderStep?.shaderCode

                    val result = mediaUseCases.saveToCacheStorageWithBitmapUseCase(
                        listOf(media),
                        listOf(shader),
                        listOf(event.degrees)
                    )
                    if (result.getOrNull() == null) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("Returned Uri is null")
                        )
                        return@launch
                    }
                    when (result) {
                        is Result.Error -> {
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowSnackBar(result.error.message.toString())
                            )
                            _state.update { it.copy(isSharing = false) }
                        }

                        is Result.Success -> {
                            if (result.data == null || result.data.isEmpty() || result.data.first().uri == null) {
                                _downloadEvents.send(
                                    MediaEditOneTimeEvents.ShowSnackBar("Returned Uri is null")
                                )
                                return@launch
                            }
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowShareChooser(result.data!!.first().uri!!.toUri())
                            )
                            _state.update { it.copy(isSharing = false) }
                        }
                    }

                }
            }

            is MediaEvents.OnAdjustTypeValueClick -> {
                _state.update { st ->
                    val adjustState = st.adjustStateMap[event.page] ?: AdjustState()
                    val newValues = adjustState.adjustTypeValues +
                            (adjustState.currentAdjustType to 0f)
                    val step = mediaUseCases.composeShaderUseCase(
                        adjustState.filterType,
                        newValues
                    )
                    st.copy(
                        adjustStateMap = st.adjustStateMap + (event.page to adjustState.copy(
                            adjustTypeValues = newValues,
                            shaderStep = step
                        ))
                    )
                }
            }

            is MediaEvents.ApplyFilter -> {
                _state.update { st ->
                    val adjustState = st.adjustStateMap[event.page] ?: AdjustState()
                    // Compose the chosen filter with whatever adjustments are already applied so
                    // the two stack instead of overwriting one another.
                    val step = mediaUseCases.composeShaderUseCase(
                        event.filterType,
                        adjustState.adjustTypeValues
                    )
                    st.copy(
                        adjustStateMap = st.adjustStateMap + (event.page to adjustState.copy(
                            filterType = event.filterType,
                            shaderStep = step
                        ))
                    )
                }
            }

            is MediaEvents.AddMediaUri -> {
                _state.update {
                    it.copy(
                        uriMap = it.uriMap + (event.position to event.uriType)
                    )
                }
            }

            is MediaEvents.RemoveMediaUri -> {
                _state.update {
                    it.copy(
                        uriMap = it.uriMap - event.position,
                        adjustStateMap = it.adjustStateMap - event.position
                    )
                }
            }

            is MediaEvents.OpenDeviceCamera -> {
                viewModelScope.launch {
                    val isImage = when (event.mediaType) {
                        MediaType.IMAGE -> true
                        MediaType.VIDEO -> false
                        else -> null
                    }
                    isImage?.let { imageFlag ->
                        _state.update { it.copy(mediaType = event.mediaType) }
                        val uriResult = mediaUseCases.generateSharableUriUseCase(imageFlag)
                        if (uriResult is Result.Success && uriResult.data != null) {
                            _state.update {
                                it.copy(tempMediaUri = uriResult.data.toString())
                            }
                        }
                    }
                }
            }

            is MediaEvents.UpdateCurrentPosition -> {
                _state.update { it.copy(currentPosition = event.position) }
            }

            is MediaEvents.UpdateMediaActionType -> {
                _state.update { it.copy(mediaActionType = event.type) }
            }

            is MediaEvents.UpdateMediaType -> {
                _state.update { it.copy(mediaType = event.mediaType) }
            }

            is MediaEvents.SaveMultipleImages -> {
                _state.update { it.copy(isDownloadingForNavigation = true) }
                viewModelScope.launch {
                    val current = state.value
                    // Process media in page order; each carries its type (image vs video).
                    val ordered = current.uriMap.entries.sortedBy { it.key }
                    val mediaList = ordered.map { it.value }
                    val shaderList = ordered.map { (page, _) ->
                        current.adjustStateMap[page]?.shaderStep?.shaderCode
                    }
                    val degreesList = ordered.map { (page, _) ->
                        current.adjustStateMap[page]?.rotationDegrees ?: 0f
                    }

                    if (mediaList.isEmpty()) {
                        _downloadEvents.send(
                            MediaEditOneTimeEvents.ShowSnackBar("No Media Selected")
                        )
                        _state.update { it.copy(isDownloadingForNavigation = false) }
                        return@launch
                    }

                    val result = mediaUseCases.saveToCacheStorageWithBitmapUseCase(
                        mediaList,
                        shaderList,
                        degreesList
                    )

                    when (result) {
                        is Result.Error -> {
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowSnackBar(result.error.message.toString())
                            )
                            _state.update { it.copy(isDownloadingForNavigation = false) }
                        }

                        is Result.Success -> {
                            val data = result.data
                            if (data.isNullOrEmpty()) {
                                _downloadEvents.send(
                                    MediaEditOneTimeEvents.ShowSnackBar("Failed to save media")
                                )
                                _state.update { it.copy(isDownloadingForNavigation = false) }
                                return@launch
                            }
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.NavigateToMemory(value = data)
                            )
                            _state.update { it.copy(isDownloadingForNavigation = false) }
                        }
                    }
                }
            }
        }
    }
}