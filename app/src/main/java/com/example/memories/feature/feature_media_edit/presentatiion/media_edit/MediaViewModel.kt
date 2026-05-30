package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _downloadEvents = Channel<MediaEditOneTimeEvents>()
    val downloadEvents = _downloadEvents.receiveAsFlow()

    init {
//        _state.update {
//            it.copy(
//                shaderList = updateShaderListToOriginal(it.shaderList),
//                adjustShaderList = updateAdjustShaderListToFirst(it.adjustShaderList)
//            )
//        }

    }

//    fun updateShaderListToOriginal(shaderList: List<String?>): List<String?> {
//        return shaderList.map { it -> mediaUseCases.applyFilterUseCase(FilterType.ORIGINAL) }
//    }

//    fun updateAdjustShaderListToFirst(adjustShaderList: List<String?>): List<String?> {
//        return adjustShaderList.map { it ->
//            mediaUseCases.applyAdjustFilterUseCase(
//                AdjustType.BRIGHTNESS,
//                0f
//            )
//        }
//    }


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
//            is MediaEvents.UriToBitmap -> {
//
//
//                viewModelScope.launch {
//                    val result = mediaUseCases.uriToBitmapUseCase(event.uri)
//                    when (result) {
//                        is Result.Error -> {
//                            Log.e(TAG, "UriToBitmap error : ${result.error.message.toString()}")
//                            _state.update { it.copy(isDownloading = false) }
//                        }
//
//                        is Result.Success -> {
//                            Log.i(TAG, "UriToBitmap Successful ${result.data}")
//                            _state.update {
//                                it.copy(
//                                    originalBitmapList = it.originalBitmapList.mapIndexed { index, bitmap ->
//                                        if (index == event.page) result.data else bitmap
//                                    },
//                                    isDownloading = false
//                                )
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            is MediaEvents.OnRemoveBitmap -> {
//                _state.update {
//                    it.copy(
//                        originalBitmapList = it.originalBitmapList.mapIndexed { index, bitmap ->
//                            if (index == event.page) null else bitmap
//                        }
//                    )
//                }
//            }
//
//            is MediaEvents.DownloadImage -> {
//                _state.update { it.copy(isDownloading = true) }
//                viewModelScope.launch {
//                    Log.d(TAG, "onEvent: DownloadImage called")
//
//                    if(event.uri == null){
//                        _downloadEvents.send(
//                            MediaEditOneTimeEvents.ShowSnackBar("No Media Selected")
//                        )
//                        _state.update { it.copy(isDownloading = false) }
//                        return@launch
//                    }
//                    val result = mediaUseCases.downloadWithBitmap(
//                        event.uri,
//                        state.value.shaderList[event.page],
//                        event.degrees
//                    )
//                    when (result) {
//                        is Result.Error -> {
//                            _downloadEvents.send(
//                                MediaEditOneTimeEvents.ShowSnackBar(result.error.message.toString())
//                            )
//                            _state.update { it.copy(isDownloading = false) }
//                        }
//
//                        is Result.Success -> {
//                            _downloadEvents.send(
//                                MediaEditOneTimeEvents.ShowSnackBar(result.data.toString())
//                            )
//                            _state.update { it.copy(isDownloading = false) }
//                        }
//                    }
//                }
//            }
//

//

//
//            is MediaEvents.ChangeRotation -> {
//                val targetRotation = when (event.direction) {
//                    RotationDirection.LEFT -> state.value.imageDegreeList[event.page] - event.value
//                    RotationDirection.RIGHT -> state.value.imageDegreeList[event.page] + event.value
//                }
//                _state.update {
//                    it.copy(
//                        imageDegreeList = it.imageDegreeList.mapIndexed { index, degree ->
//                            if (index == event.page) targetRotation else degree
//                        }
//                    )
//                }
//            }
//
//
//            MediaEvents.BitmapToUri -> {
////                viewModelScope.launch {
////                    val result =
////                        mediaUseCases.saveBitmapToInternalStorageUseCase(_bitmapState.value.bitmap)
////                    when (result) {
////                        is Result.Error -> {
////                            Log.e(TAG, "onEvent: BitmapToUri error : ${result.error.message}")
////                        }
////
////                        is Result.Success -> {
////                            Log.d(TAG, "bitmap Uri : ${result.data.toString()}")
////                            _saveBitmapToInternalSuccess.send(result.data)
////                        }
////                    }
////                }
//            }
//
//            is MediaEvents.DownloadVideo -> {
//                viewModelScope.launch {
//                    val result = mediaUseCases.downloadVideoUseCase(event.uri)
//                    when (result) {
//                        is Result.Error -> {
////                            _downloadError.send(result.error.message.toString())
//                        }
//
//                        is Result.Success -> {
////                            _downloadError.send(result.data!!)
//                        }
//                    }
//                }
//            }
//
//            is MediaEvents.EditToolStateChange -> {
//                val previousTool = _state.value.initialActiveTool
//                _state.update {
//                    it.copy(
//                        initialActiveTool = event.tool,
//                        initialPreviousTool = previousTool
//                    )
//                }
//            }
//
//            is MediaEvents.AdjustTypeStateChange -> {
//                _state.update {
//                    it.copy(
//                        activeAdjustType = event.adjustType,
//                        currentAdjustTypeList = it.currentAdjustTypeList.mapIndexed { index, type ->
//                            if (index == event.page) event.adjustType else type
//                        },
//                        adjustShaderList = it.adjustShaderList.mapIndexed { index, shader ->
//                            if (index == event.page) mediaUseCases.applyAdjustFilterUseCase(
//                                event.adjustType,
//                                it.adjustValuesList[event.page][event.adjustType] ?: 0f
//                            ) else shader
//                        }
//
//                    )
//                }
//            }
//
//            is MediaEvents.AdjustTypeValueChange -> {
//                _state.update {
//                    it.copy(
//                        adjustValues = it.adjustValues + (it.activeAdjustType to event.value),
//                        adjustValuesList = it.adjustValuesList.mapIndexed { index, map ->
//                            if (index == event.page) {
//                                it.adjustValuesList[index] + (it.activeAdjustType to event.value)
//                            } else {
//                                map
//                            }
//                        },
//                        adjustShaderList = it.adjustShaderList.mapIndexed { index, shader ->
//                            val adjustType = it.currentAdjustTypeList[event.page]
//                            if (index == event.page) mediaUseCases.applyAdjustFilterUseCase(
//                                it.currentAdjustTypeList[event.page],
//                                it.adjustValuesList[event.page][adjustType] ?: 0f
//                            ) else shader
//                        }
//                    )
//                }
////                mediaUseCases.applyAdjustFilterUseCase(state.value.currentAdjustTypeList[event.page],event.value)
//
//            }
//
//            is MediaEvents.FilterTypeStateChange -> {
//                _state.update {
//                    it.copy(
//                        filterType = event.filterType,
//                        filterTypeList = it.filterTypeList.mapIndexed { index, filterType ->
//                            if (index == event.page) event.filterType else filterType
//                        }
//
//                    )
//                }
//            }
//
//
//
//            is MediaEvents.ApplyFilter -> {
//                val shader = mediaUseCases.applyFilterUseCase(state.value.filterType)
//                _state.update {
//                    it.copy(
//                        shaderList = it.shaderList.mapIndexed { index, s ->
//                            if (index == event.page) shader else s
//                        }
//                    )
//                }
//            }
//
//        }
    }
}