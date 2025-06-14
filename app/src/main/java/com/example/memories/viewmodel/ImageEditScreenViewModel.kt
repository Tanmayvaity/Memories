package com.example.memories.viewmodel


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.model.media.MediaRepository
import com.example.memories.model.models.BitmapResult
import com.example.memories.model.models.CaptureResult
import com.example.memories.model.models.MediaResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class ImageEditScreenViewModel() : ViewModel() {

    val mediaRepository: MediaRepository by lazy { MediaRepository() }

    private val _downloadImageFlow = MutableStateFlow<DownloadImageUiState<String>>(
        DownloadImageUiState()
    )
    val downloadImageFlow = _downloadImageFlow.asStateFlow()
    private val _imageBitmap =
        MutableStateFlow<DownloadImageUiState<Bitmap>>(DownloadImageUiState())
    val imageBitmap = _imageBitmap.asStateFlow()

    private val _internalBitmapUri = MutableStateFlow<DownloadImageUiState<Uri>>(
        DownloadImageUiState()
    )
    val internalBitmapUri = _internalBitmapUri.asStateFlow()


    // with uri
    fun downloadPicture(
        appContext: Context,
        uri: String
    ) {
        viewModelScope.launch {
            _downloadImageFlow.update { _downloadImageFlow.value.copy(isLoading = true) }

            val result = mediaRepository.downloadImage(appContext, uri)
            resultLogic(result)
        }
    }

    // with bitmap
    fun downloadPictureBitmap(
        context: Context,
        bitmap: Bitmap
    ) {
        viewModelScope.launch() {
            _downloadImageFlow.update { _downloadImageFlow.value.copy(isLoading = true) }
            val result = mediaRepository.downloadImageWithBitmap(context, bitmap)
            resultLogic(result)

        }
    }

    private fun resultLogic(result: MediaResult) {
        when (result) {
            is MediaResult.Error -> {
                _downloadImageFlow.update {
                    _downloadImageFlow.value.copy(
                        isLoading = false,
                        error = result.error.message,
                        data = null
                    )
                }
            }

            is MediaResult.Success -> {
                _downloadImageFlow.update {
                    _downloadImageFlow.value.copy(
                        isLoading = false,
                        error = null,
                        data = result.successMessage
                    )
                }
            }
        }
    }


    fun reset() {
        _downloadImageFlow.update {
            _downloadImageFlow.value.copy(
                data = null,
                error = null
            )
        }
    }




    fun uriToBitmap(uri: Uri, context: Context) {
        viewModelScope.launch {
            _imageBitmap.update { _imageBitmap.value.copy(isLoading = true) }
            val result = mediaRepository.uriToBitmap(uri, context)
            when (result) {
                is BitmapResult.Error -> {
                    _imageBitmap.update {
                        _imageBitmap.value.copy(
                            isLoading = false,
                            error = result.error.message.toString(),
                            data = null
                        )
                    }
                }

                is BitmapResult.Success -> {
                    _imageBitmap.update {
                        _imageBitmap.value.copy(
                            isLoading = false,
                            error = null,
                            data = result.bitmap
                        )
                    }
                }
            }

        }
    }

    fun saveBitmapToInternalStorage(
        file:File,
        bitmap:Bitmap
    ){
        viewModelScope.launch {
            _internalBitmapUri.update { _internalBitmapUri.value.copy(isLoading = true) }
            val result = mediaRepository.saveToInternalStorage(file,bitmap)
            when(result){
                is CaptureResult.Error -> {
                    _internalBitmapUri.update { _internalBitmapUri.value.copy(
                        isLoading = false,
                        error = result.error.message,
                        data = null
                    ) }
                }

                is CaptureResult.Success -> {
                    _internalBitmapUri.update { _internalBitmapUri.value.copy(
                        isLoading = false,
                        error = null,
                        data = result.uri
                    ) }
                }
            }
        }
    }


    data class DownloadImageUiState<T>(
        val isLoading: Boolean = false,
        val error: String? = null,
        val data: T? = null
    )

}