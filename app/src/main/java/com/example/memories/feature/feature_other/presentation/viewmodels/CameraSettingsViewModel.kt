package com.example.memories.feature.feature_other.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.feature.feature_other.domain.usecase.CameraSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraSettingsViewModel @Inject constructor(
    val cameraSettingsUseCase: CameraSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CameraSettingsState>(CameraSettingsState())
    val state = _state.asStateFlow()


    init {
        onEvent(CameraSettingsEvents.Fetch)
    }


    fun onEvent(event: CameraSettingsEvents) {
        when (event) {
            is CameraSettingsEvents.Fetch ->{
                viewModelScope.launch {
                    val state = cameraSettingsUseCase.getState()
                    _state.update { state }
                }
            }

            is CameraSettingsEvents.ShutterSoundToggle -> {
                Log.d("CameraSettingsViewModel", "shutter sound toggle called")
                viewModelScope.launch {
                    cameraSettingsUseCase.setShutterSound()
                    _state.update { it ->
                        it.copy(shutterSound = event.value)
                    }
                }
            }


            is CameraSettingsEvents.SaveLocationToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setSaveLocation()
                    _state.update { it ->
                        it.copy(saveLocation = event.value)
                    }
                }
            }


            is CameraSettingsEvents.MirrorImageToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setMirrorImage()
                    _state.update { it ->
                        it.copy(mirrorImage = event.value)
                    }
                }
            }




            is CameraSettingsEvents.GridImageToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setGridLines()
                    _state.update { it ->
                        it.copy(gridLines = event.value)
                    }
                }
            }
            is CameraSettingsEvents.FlipCameraToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setFlipCamera()
                    _state.update { it ->
                        it.copy(flipCameraUsingSwipe = event.value)
                    }
                }

            }


            is CameraSettingsEvents.HighEfficiencyPicturesToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setHeifPictures()
                    _state.update { it ->
                        it.copy(heifPictures = event.value)
                    }
                }
            }

            is CameraSettingsEvents.HighEfficiencyVideosToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setHevcVideos()
                    _state.update { it ->
                        it.copy(hevcVideos = event.value)
                    }
                }
            }



            is CameraSettingsEvents.WatermarkToggle -> {
                viewModelScope.launch {
                    cameraSettingsUseCase.setWatermark()
                    _state.update { it ->
                        it.copy(watermark = event.value)
                    }
                }

            }


        }
    }

}