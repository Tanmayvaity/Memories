package com.example.memories.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Range
import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.model.CameraRepository
import com.example.memories.model.models.CaptureResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class CameraScreenViewModel() : ViewModel() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
    val lensFacing = _lensFacing.asStateFlow()

    private val _torchState = MutableStateFlow<Boolean>(false)
    val torchState = _torchState.asStateFlow()

    private val _zoomScale = MutableStateFlow(0f)
    val zoomScale = _zoomScale.asStateFlow()

    private val _exposureValue: MutableStateFlow<Int> = MutableStateFlow(0)
    val exposureValue = _exposureValue.asStateFlow()

    private val _tempImageBitmap = MutableStateFlow<Bitmap?>(null)
    val tempImageBitmap = _tempImageBitmap.asStateFlow()

    private val _errorMessage = MutableStateFlow<Throwable?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _successfullImageCapture = MutableStateFlow<Uri?>(null)
    val successfullImageCapture = _successfullImageCapture.asStateFlow()


    private val cameraRepository = CameraRepository()


    init {
        cameraRepository.setSurfaceRequestCallback { surfaceRequest ->
            _surfaceRequest.update { surfaceRequest }
        }
    }


    suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        cameraRepository.bindToCameraUseCase(
            appContext,
            lifecycleOwner,
            _lensFacing.value,
            _zoomScale.value,
            _torchState.value,
            _exposureValue.value
        )

    }


    fun toggleCamera() {
        if (_lensFacing.value == CameraSelector.LENS_FACING_BACK) {
            _lensFacing.value = CameraSelector.LENS_FACING_FRONT
        } else {
            _lensFacing.value = CameraSelector.LENS_FACING_BACK
        }


    }

    fun toggleTorch() {
        _torchState.update { !_torchState.value }
        cameraRepository.toggleTorchState(_torchState.value)

    }

    fun tapToFocus(tapCoords: Offset) {
        cameraRepository.tapToFocus(tapCoords)

        _exposureValue.update { 0 }
        cameraRepository.changeExposure(_exposureValue.value)

    }

    fun zoom(scale: Float) {
        _zoomScale.update { scale }
        cameraRepository.zoom(scale)
    }


    fun changeExposure(value: Int) {
        _exposureValue.update { value }
        cameraRepository.changeExposure(value)
    }

    fun getExposureRange(): Range<Int> {
        return cameraRepository.getExposureRange()
    }

    fun isExposureSupported(): Boolean {
        return cameraRepository.isExposureSupported()
    }


    fun takePicture(tempImageFile : File ) {
        viewModelScope.launch {
            val captureResult = cameraRepository.takePicture(tempImageFile)
            when(captureResult){
                is CaptureResult.Success -> {
                    _successfullImageCapture.update { captureResult.uri }
                    resetErrorState()
                }
                is CaptureResult.Error -> {
                    _errorMessage.update { captureResult.error }
                    resetUriState()
                }
            }
        }
    }

    fun resetUriState(){
        _successfullImageCapture.update { null }
    }

    fun resetErrorState(){
        _errorMessage.update { null }
    }



}