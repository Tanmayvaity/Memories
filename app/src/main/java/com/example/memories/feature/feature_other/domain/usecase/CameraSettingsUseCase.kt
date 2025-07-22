package com.example.memories.feature.feature_other.domain.usecase

import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.feature.feature_other.domain.repository.CameraSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CameraSettingsUseCase @Inject constructor(
    val cameraSettingsRepository: CameraSettingsRepository
) {

    suspend fun getState(): CameraSettingsState {
        return cameraSettingsRepository.getState()
    }

    fun getShutterSound(): Flow<Boolean> {
        return cameraSettingsRepository.enableShutterSound
    }

    suspend fun setShutterSound() {
        cameraSettingsRepository.setShutterSoundSetting()
    }

    fun getSaveLocation(): Flow<Boolean> {
        return cameraSettingsRepository.enableSaveLocation
    }

    suspend fun setSaveLocation() {
        cameraSettingsRepository.setSaveLocationSetting()
    }

    fun getMirrorImage(): Flow<Boolean> {
        return cameraSettingsRepository.enableMirrorImage
    }

    suspend fun setMirrorImage() {
        cameraSettingsRepository.setMirrorImageSetting()
    }

    fun getGridLines(): Flow<Boolean> {
        return cameraSettingsRepository.enableGridLines
    }

    suspend fun setGridLines() {
        cameraSettingsRepository.setGridLinesSeting()
    }

    fun getFlipCamera(): Flow<Boolean> {
        return cameraSettingsRepository.enableFlipCamera
    }

    suspend fun setFlipCamera() {
        cameraSettingsRepository.setEnableFlipCameraSetting()
    }

    fun getWatermark(): Flow<Boolean> {
        return cameraSettingsRepository.enableWatermark
    }

    suspend fun setWatermark() {
        cameraSettingsRepository.setWatermarkSetting()
    }

    fun getHeifPictures(): Flow<Boolean> {
        return cameraSettingsRepository.enableHeifPictures
    }

    suspend fun setHeifPictures() {
        cameraSettingsRepository.setHeifPicturesSetting()
    }

    fun getHevcVideos(): Flow<Boolean> {
        return cameraSettingsRepository.enableHevcVideos
    }

    suspend fun setHevcVideos() {
        cameraSettingsRepository.setHevcVideosSetting()
    }


}