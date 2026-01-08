package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    companion object{
        private const val TAG = "MediaViewModel"
    }





    private val _bitmapState= MutableStateFlow<BitmapState>(BitmapState())
    val bitmapState = _bitmapState.asStateFlow()

    private val _downloadError = Channel<String>()
    val downloadErrorFlow = _downloadError.receiveAsFlow()

    private val _downloadSuccess = Channel<String>()
    val downloadSuccessFlow = _downloadSuccess.receiveAsFlow()


    private val _saveBitmapToInternalSuccess = Channel<Uri?>()
    val saveBitmapToInternalSuccessFlow = _saveBitmapToInternalSuccess.receiveAsFlow()




    fun onEvent(event : MediaEvents){
        when(event){
            is MediaEvents.UriToBitmap -> {
                viewModelScope.launch {
                    val result =  mediaUseCases.uriToBitmapUseCase(event.uri)
                    when(result){
                        is Result.Error -> {
                            Log.e(TAG, "UriToBitmap error : ${result.error.message.toString()}", )
                        }
                        is Result.Success -> {
                            Log.i(TAG, "UriToBitmap Successful ${result.data}")
                            _bitmapState.update { _bitmapState.value.copy(
                                bitmap = result.data
                            ) }
                        }
                    }
                }

            }

            is MediaEvents.DownloadBitmap ->{
                viewModelScope.launch {
                    val result = mediaUseCases.downloadWithBitmap(event.bitmap)
                    when(result){
                        is Result.Error -> {
                            _downloadError.send(result.error.message.toString())
                        }

                        is Result.Success -> {
                            _downloadError.send(result.data!!)
                        }
                    }
                }
            }

            MediaEvents.BitmapToUri -> {
                viewModelScope.launch {
                    val result = mediaUseCases.saveBitmapToInternalStorageUseCase(_bitmapState.value.bitmap)
                    when(result){
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: BitmapToUri error : ${result.error.message}", )
                        }
                        is Result.Success -> {
                            Log.d(TAG, "bitmap Uri : ${result.data.toString()}")
                            _saveBitmapToInternalSuccess.send(result.data)
                        }
                    }
                }
            }

            is MediaEvents.DownloadVideo -> {
                viewModelScope.launch {
                    val result = mediaUseCases.downloadVideoUseCase(event.uri)
                    when(result){
                        is Result.Error -> {
                            _downloadError.send(result.error.message.toString())
                        }

                        is Result.Success -> {
                            _downloadError.send(result.data!!)
                        }
                    }
                }
            }

            is MediaEvents.EditToolStateChange -> {
            }

        }
    }
}