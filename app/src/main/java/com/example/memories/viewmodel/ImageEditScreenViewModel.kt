package com.example.memories.viewmodel


import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.model.media.MediaRepository
import com.example.memories.model.models.CaptureResult
import com.example.memories.model.models.MediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageEditScreenViewModel() : ViewModel() {

    val mediaRepository: MediaRepository by lazy { MediaRepository() }

    private val _downloadImageFlow = MutableStateFlow<DownloadImageUiState<String>>(
        DownloadImageUiState()
    )
    val downloadImageFlow = _downloadImageFlow.asStateFlow()

    private val _tempImageUri = MutableStateFlow<DownloadImageUiState<Uri?>>(DownloadImageUiState())
    val tempImageUri = _tempImageUri.asStateFlow()




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
        appContext: Context,
        uri: String
    ) {
        viewModelScope.launch() {
            _downloadImageFlow.update { _downloadImageFlow.value.copy(isLoading = true) }

            try{
                val bitmap = uriToBitmap(appContext,uri.toUri())
                if(bitmap!=null){
                    val result = mediaRepository.downloadImageWithBitmap(appContext, bitmap)
                    resultLogic(result)
                }
            }catch(e : Exception){
                e.printStackTrace()
                Log.e("ImageEditViewModel", "downloadPictureBitmap: ${e.message}" )
            }


        }
    }

    private fun resultLogic(result : MediaResult){
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

    fun copyFromSharedStorage(
        context : Context,
        sharedUri : Uri,
        file : File
    ){
        viewModelScope.launch {
            val result = mediaRepository.copyFromSharedStorage(context, sharedUri,file)
            when(result){
                is CaptureResult.Success ->{
                    _tempImageUri.update { _tempImageUri.value.copy(
                        data = result.uri,
                    ) }
                }
                is CaptureResult.Error -> {
                    Log.e("ImageEditScreenViewModel", "copyFromSharedStorage : ${result.error} " )
                    _tempImageUri.update {
                        _tempImageUri.value.copy(
                            error = result.error.message
                        )
                    }
                }
            }
        }

    }

    suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    data class DownloadImageUiState<T>(
        val isLoading: Boolean = false,
        val error: String? = null,
        val data: T? = null
    )

}