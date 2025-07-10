package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult
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

//    private val _saveBitmapToInternalError = Channel<String>()
//    val saveBitmapToInternalError = _saveBitmapToInternalError.receiveAsFlow()

    private val _saveBitmapToInternalSuccess = Channel<Uri?>()
    val saveBitmapToInternalSuccessFlow = _saveBitmapToInternalSuccess.receiveAsFlow()




    fun onEvent(event : MediaEvents){
        when(event){
            is MediaEvents.UriToBitmap -> {
                viewModelScope.launch {
                    val result =  mediaUseCases.uriToBitmapUseCase(event.uri)
                    when(result){
                        is BitmapResult.Error -> {
                            Log.e(TAG, "UriToBitmap error : ${result.error.message.toString()}", )
                        }
                        is BitmapResult.Success -> {
                            Log.i(TAG, "UriToBitmap Successful ${result.bitmap}")
                            _bitmapState.update { _bitmapState.value.copy(
                                bitmap = result.bitmap
                            ) }
                        }
                    }
                }

            }

            is MediaEvents.DownloadBitmap ->{
                viewModelScope.launch {
                    val result = mediaUseCases.downloadWithBitmap(event.bitmap)
                    when(result){
                        is MediaResult.Error -> {
                            _downloadError.send(result.error.message.toString())
                        }

                        is MediaResult.Success -> {
                            _downloadError.send(result.successMessage)
                        }
                    }
                }
            }

            MediaEvents.BitmapToUri -> {
                viewModelScope.launch {
                    val result = mediaUseCases.saveBitmapToInternalStorageUseCase(_bitmapState.value.bitmap)
                    when(result){
                        is CaptureResult.Error -> {
                            Log.e(TAG, "onEvent: BitmapToUri error : ${result.error.message}", )
                        }
                        is CaptureResult.Success -> {
                            Log.d(TAG, "bitmap Uri : ${result.uri.toString()}")
                            _saveBitmapToInternalSuccess.send(result.uri)
                        }
                    }
                }
            }
        }
    }
}