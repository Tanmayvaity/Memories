package com.example.memories.feature.feature_camera.presentation.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.presentation.Type
import com.example.memories.core.presentation.UriType
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_camera.domain.usecase.CameraUseCases
import com.example.memories.core.presentation.UriType.Companion.mapToType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraUseCase: CameraUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<CameraState>(CameraState())
    val state = _state.asStateFlow()
    private val _capturedMediaUri = MutableStateFlow<UriType>(UriType())
    val capturedMediaUri = _capturedMediaUri.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow = _errorChannel.receiveAsFlow()

    private val _timeElapsed = MutableStateFlow<Long>(0)
    val timeElapsed = _timeElapsed.asStateFlow()

    private var timerJob : Job? = null

    init {
        onEvent(CameraEvent.SurfaceCallback)
    }

    companion object{
        const val TAG = "CameraViewModel"
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
                reset()
                Log.d(TAG, "onEvent: photo mode")
            }
            CameraEvent.PortraitMode -> {
               _state.update {
                   _state.value.copy(
                       mode = CameraMode.PORTRAIT
                   )
               }
                reset()
                Log.d(TAG, "onEvent: portrait mode")
            }

            CameraEvent.VideoMode -> {
                _state.update {
                    _state.value.copy(
                        mode = CameraMode.VIDEO
                    )
                }
                reset()
                Log.d(TAG, "onEvent: video mode")
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
                            val uriType = UriType(
                                uri = result.uri.toString(),
                                type = if (UriType.isImageFile(result.uri.toString())) Type.IMAGE else Type.VIDEO
                            )
                            _capturedMediaUri.update { uriType }

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

            is CameraEvent.Take ->{
                if(_state.value.mode == CameraMode.VIDEO){
                    _state.update {
                        it.copy(videoState = VideoState.Started)
                    }
                    viewModelScope.launch {
                        val result = cameraUseCase.takeVideoUseCase(event.context,event.file)
                        when(result){
                            is CaptureResult.Error -> {
                                Log.e(TAG, "onEvent: error : ${result.error.message} ", )
                            }
                            is CaptureResult.Success -> {
                                val uriType = UriType(
                                    uri = result.uri.toString(),
                                    type = result.uri.mapToType()
                                )
                                _capturedMediaUri.update { uriType }
                                Log.d(TAG, "onEvent: success ${_capturedMediaUri.value.uri.toString()}")
                            }

                        }
                        Log.i(TAG, "onEvent: videoState = ${_state.value.videoState}")
                    }

                    timerJob = viewModelScope.launch {
                        while(isActive){
                            delay(1000)
                            _timeElapsed.update { it +1 }
                        }
                    }



                }
                if(_state.value.mode == CameraMode.PHOTO || _state.value.mode == CameraMode.PORTRAIT){
                    viewModelScope.launch {
                        val result = cameraUseCase.takePictureUseCase(event.file)
                        when(result){
                            is CaptureResult.Error -> {
                                _errorChannel.send(result.error.message.toString())
                            }

                            is CaptureResult.Success -> {
                                val uriType = UriType(
                                    uri = result.uri.toString(),
                                    type = result.uri.mapToType()
                                )
                                _capturedMediaUri.update { uriType }

                            }
                        }
                    }
                }


            }

            is CameraEvent.Pause ->{
                _state.update {
                    it.copy(videoState = VideoState.Pause)
                }
                cameraUseCase.pauseRecordingUseCase()
                Log.i(TAG, "onEvent: videoState = ${_state.value.videoState}")
            }
            is CameraEvent.Resume ->{
                _state.update {
                    it.copy(videoState = VideoState.Resume)
                }
                cameraUseCase.resumeRecordingUseCase()
                Log.i(TAG, "onEvent: videoState = ${_state.value.videoState}")
            }
            is CameraEvent.Stop ->{
                timerJob?.cancel()
                _state.update {
                    it.copy(videoState = VideoState.Stop)
                }
                cameraUseCase.stopRecordingUseCase()
                _state.update {
                    it.copy(
                        videoState = VideoState.Idle
                    )
                }
                Log.i(TAG, "onEvent: videoState = ${_state.value.videoState}")
            }

            is CameraEvent.Cancel ->{
                cameraUseCase.cancelRecordingUseCase()
                timerJob?.cancel()
                _state.update { it.copy(videoState = VideoState.Idle) }
                reset()
            }




        }
    }

    private fun reset(){
        _capturedMediaUri.update { it.copy(uri = null,type = null) }
        _timeElapsed.update { 0 }
    }





}