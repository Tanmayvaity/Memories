package com.example.memories.feature.feature_camera.presentation.camera

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_camera.domain.usecase.CameraUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraUseCase: CameraUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<CameraState>(CameraState())
    val state = _state.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri = _capturedImageUri.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow = _errorChannel.receiveAsFlow()

    init {
        onEvent(CameraEvent.SurfaceCallback)
    }

    private fun bindToCamera(
        app: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch : Boolean
    ) {
        viewModelScope.launch {
            cameraUseCase.bindToCameraUseCase(app, lifecycleOwner,lensFacing,torch)
        }
    }

    //  Camera Events
    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.Preview -> {
                val lensFacing = _state.value.lensFacing
                val torch = _state.value.torchState
                bindToCamera(event.app, event.lifecycleOwner,lensFacing,torch)
            }

            is CameraEvent.SurfaceCallback -> {
                cameraUseCase.setSurfaceCallbackUseCase { surfaceRequest ->
                    _state.update { _state.value.copy(surfaceRequest = surfaceRequest) }
                }
            }

            is CameraEvent.TorchToggle -> {
                _state.update { _state.value.copy(torchState = !_state.value.torchState) }
                cameraUseCase.torchToggleUseCase(_state.value.torchState)
            }

            CameraEvent.ChangeLensFacing -> {
                _state.update {
                    _state.value.copy(
                        lensFacing = if (_state.value.lensFacing == LensFacing.BACK) LensFacing.FRONT else LensFacing.BACK
                    )
                }
            }

            CameraEvent.PhotoMode -> {
                _state.update {
                    _state.value.copy(
                        mode = CameraMode.PHOTO
                    )
                }
            }
            CameraEvent.PortraitMode -> {
               _state.update {
                   _state.value.copy(
                       mode = CameraMode.PORTRAIT
                   )
               }
            }
            CameraEvent.VideoMode -> {
                _state.update {
                    _state.value.copy(
                        mode = CameraMode.VIDEO
                    )
                }
            }

            is CameraEvent.Zoom -> {
                _state.update { _state.value.copy(
                    zoomScale = event.scale
                ) }
                cameraUseCase.zoomRangeUseCase(event.scale)
//                Log.d("CameraViewModel", "zoom : ${event.scale}")

            }

            is CameraEvent.TakePicture ->{
                viewModelScope.launch {
                    val result = cameraUseCase.takePictureUseCase(event.file)
                    when(result){
                        is CaptureResult.Error -> {
                            _errorChannel.send(result.error.message.toString())
                        }

                        is CaptureResult.Success -> {
                            _capturedImageUri.update { result.uri }

                        }
                    }
                }
            }

            CameraEvent.Reset -> {
                reset()
            }
            is CameraEvent.ToggleAspectRatio -> {
                _state.update {
                    _state.value.copy(
                        aspectRatio =  if (_state.value.aspectRatio == AspectRatio.RATIO_16_9) AspectRatio.RATIO_4_3 else AspectRatio.RATIO_16_9
                    )
                }

                cameraUseCase.setAspectRatioUseCase(_state.value.aspectRatio)
            }

            is CameraEvent.TapToFocus ->{
                cameraUseCase.tapToFocusUseCase(event.offset)
            }

        }
    }

    private fun reset(){
        _capturedImageUri.update { null }
    }


}