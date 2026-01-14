package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.net.Uri
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.util.CoilUtils.result
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import com.google.common.collect.Multimaps.index
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
        _state.update {
            it.copy(
                shaderList = updateShaderListToOriginal(it.shaderList),
                adjustShaderList = updateAdjustShaderListToFirst(it.adjustShaderList)
            )
        }

    }

    fun updateShaderListToOriginal(shaderList: List<String?>): List<String?> {
        return shaderList.map { it -> mediaUseCases.applyFilterUseCase(FilterType.ORIGINAL) }
    }

    fun updateAdjustShaderListToFirst(adjustShaderList: List<String?>): List<String?> {
        return adjustShaderList.map { it ->
            mediaUseCases.applyAdjustFilterUseCase(
                AdjustType.BRIGHTNESS,
                0f
            )
        }
    }


    fun onEvent(event: MediaEvents) {
        when (event) {
            is MediaEvents.UriToBitmap -> {


                viewModelScope.launch {
                    val result = mediaUseCases.uriToBitmapUseCase(event.uri)
                    when (result) {
                        is Result.Error -> {
                            Log.e(TAG, "UriToBitmap error : ${result.error.message.toString()}")
                            _state.update { it.copy(isDownloading = false) }
                        }

                        is Result.Success -> {
                            Log.i(TAG, "UriToBitmap Successful ${result.data}")
                            _state.update {
                                it.copy(
                                    originalBitmapList = it.originalBitmapList.mapIndexed { index, bitmap ->
                                        if (index == event.page) result.data else bitmap
                                    },
                                    isDownloading = false
                                )
                            }
                        }
                    }
                }

            }

            is MediaEvents.OnRemoveBitmap -> {
                _state.update {
                    it.copy(
                        originalBitmapList = it.originalBitmapList.mapIndexed { index, bitmap ->
                            if (index == event.page) null else bitmap
                        }
                    )
                }
            }

            is MediaEvents.DownloadImage -> {
                _state.update { it.copy(isDownloading = true) }
                viewModelScope.launch {
                    Log.d(TAG, "onEvent: DownloadImage called")
                    val result = mediaUseCases.downloadWithBitmap(
                        event.uri,
                        state.value.shaderList[event.page],
                        event.degrees
                    )
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
                    val result = mediaUseCases.saveToCacheStorageWithBitmapUseCase(
                        listOf(event.uri),
                        listOf(state.value.shaderList[event.page]) ,
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
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.ShowShareChooser(result.data!!.first())
                            )
                            _state.update { it.copy(isSharing = false) }
                        }
                    }

                }
            }

            is MediaEvents.SaveMultipleImages -> {
                _state.update { it.copy(isDownloadingForNavigation = true) }
                viewModelScope.launch {
                    val result = mediaUseCases.saveToCacheStorageWithBitmapUseCase(
                        event.uriList,
                        state.value.shaderList,
                        state.value.imageDegreeList
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
                            _state.update { it.copy(isDownloadingForNavigation = false) }
                        }

                        is Result.Success -> {
                            _downloadEvents.send(
                                MediaEditOneTimeEvents.NavigateToMemory(
                                    value = result.data!!.filter { it != null }.map { uri ->
                                        UriType(
                                            uri = uri.toString(),
                                            type = uri.mapToType()
                                        )
                                    }
                                )
                            )
                            _state.update { it.copy(isDownloadingForNavigation = false) }
                        }

                    }
                }
            }

            is MediaEvents.ChangeRotation -> {
                val targetRotation = when (event.direction) {
                    RotationDirection.LEFT -> state.value.imageDegreeList[event.page] - event.value
                    RotationDirection.RIGHT -> state.value.imageDegreeList[event.page] + event.value
                }
                _state.update {
                    it.copy(
                        imageDegreeList = it.imageDegreeList.mapIndexed { index, degree ->
                            if (index == event.page) targetRotation else degree
                        }
                    )
                }
            }


            MediaEvents.BitmapToUri -> {
//                viewModelScope.launch {
//                    val result =
//                        mediaUseCases.saveBitmapToInternalStorageUseCase(_bitmapState.value.bitmap)
//                    when (result) {
//                        is Result.Error -> {
//                            Log.e(TAG, "onEvent: BitmapToUri error : ${result.error.message}")
//                        }
//
//                        is Result.Success -> {
//                            Log.d(TAG, "bitmap Uri : ${result.data.toString()}")
//                            _saveBitmapToInternalSuccess.send(result.data)
//                        }
//                    }
//                }
            }

            is MediaEvents.DownloadVideo -> {
                viewModelScope.launch {
                    val result = mediaUseCases.downloadVideoUseCase(event.uri)
                    when (result) {
                        is Result.Error -> {
//                            _downloadError.send(result.error.message.toString())
                        }

                        is Result.Success -> {
//                            _downloadError.send(result.data!!)
                        }
                    }
                }
            }

            is MediaEvents.EditToolStateChange -> {
                val previousTool = _state.value.initialActiveTool
                _state.update {
                    it.copy(
                        initialActiveTool = event.tool,
                        initialPreviousTool = previousTool
                    )
                }
            }

            is MediaEvents.AdjustTypeStateChange -> {
                _state.update {
                    it.copy(
                        activeAdjustType = event.adjustType,
                        currentAdjustTypeList = it.currentAdjustTypeList.mapIndexed { index, type ->
                            if (index == event.page) event.adjustType else type
                        },
                        adjustShaderList = it.adjustShaderList.mapIndexed { index, shader ->
                            if (index == event.page) mediaUseCases.applyAdjustFilterUseCase(
                                event.adjustType,
                                it.adjustValuesList[event.page][event.adjustType] ?: 0f
                            ) else shader
                        }

                    )
                }
            }

            is MediaEvents.AdjustTypeValueChange -> {
                _state.update {
                    it.copy(
                        adjustValues = it.adjustValues + (it.activeAdjustType to event.value),
                        adjustValuesList = it.adjustValuesList.mapIndexed { index, map ->
                            if (index == event.page) {
                                it.adjustValuesList[index] + (it.activeAdjustType to event.value)
                            } else {
                                map
                            }
                        },
                        adjustShaderList = it.adjustShaderList.mapIndexed { index, shader ->
                            val adjustType = it.currentAdjustTypeList[event.page]
                            if (index == event.page) mediaUseCases.applyAdjustFilterUseCase(
                                it.currentAdjustTypeList[event.page],
                                it.adjustValuesList[event.page][adjustType] ?: 0f
                            ) else shader
                        }
                    )
                }
//                mediaUseCases.applyAdjustFilterUseCase(state.value.currentAdjustTypeList[event.page],event.value)

            }

            is MediaEvents.FilterTypeStateChange -> {
                _state.update {
                    it.copy(
                        filterType = event.filterType,
                        filterTypeList = it.filterTypeList.mapIndexed { index, filterType ->
                            if (index == event.page) event.filterType else filterType
                        }

                    )
                }
            }

            is MediaEvents.OnAdjustTypeValueClick -> {
                _state.update {
                    it.copy(
                        adjustValues = it.adjustValues + (it.activeAdjustType to it.activeAdjustType.defaultValue),
                        adjustValuesList = it.adjustValuesList.mapIndexed { index, map ->
                            if (index == event.page) {
                                it.adjustValuesList[index] + (it.activeAdjustType to it.activeAdjustType.defaultValue)
                            } else {
                                map
                            }
                        }
                    )
                }
            }

            is MediaEvents.ApplyFilter -> {
                val shader = mediaUseCases.applyFilterUseCase(state.value.filterType)
                _state.update {
                    it.copy(
                        shaderList = it.shaderList.mapIndexed { index, s ->
                            if (index == event.page) shader else s
                        }
                    )
                }
            }

        }
    }
}