package com.example.memories.feature.feature_other.data.repository

import com.example.memories.core.data.data_source.CameraSettingsDatastore
import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.feature.feature_other.domain.repository.CameraSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CameraSettingsRepositoryImpl @Inject constructor(
    val cameraSettingsDatastore: CameraSettingsDatastore,
) : CameraSettingsRepository {
    override val enableShutterSound: Flow<Boolean>
        get() = cameraSettingsDatastore.enableShutterSound
    override val enableSaveLocation: Flow<Boolean>
        get() = cameraSettingsDatastore.enableSaveLocation
    override val enableMirrorImage: Flow<Boolean>
        get() = cameraSettingsDatastore.enableMirrorImage
    override val enableGridLines: Flow<Boolean>
        get() = cameraSettingsDatastore.enableGridLines
    override val enableFlipCamera: Flow<Boolean>
        get() = cameraSettingsDatastore.enableFlipCamera
    override val enableWatermark: Flow<Boolean>
        get() = cameraSettingsDatastore.enableWatermark
    override val enableHeifPictures: Flow<Boolean>
        get() = cameraSettingsDatastore.enableHeifPictures
    override val enableHevcVideos: Flow<Boolean>
        get() = cameraSettingsDatastore.enableHevcVideos

    override suspend fun setShutterSoundSetting() {
        cameraSettingsDatastore.setShutterSoundSetting()
    }

    override suspend fun setSaveLocationSetting() {
        cameraSettingsDatastore.setSaveLocationSetting()
    }

    override suspend  fun setMirrorImageSetting() {
        cameraSettingsDatastore.setMirrorImageSetting()
    }

    override suspend fun setGridLinesSeting() {
        cameraSettingsDatastore.setGridLinesSeting()
    }

    override suspend fun setEnableFlipCameraSetting() {
       cameraSettingsDatastore.setEnableFlipCameraSetting()
    }

    override suspend fun setWatermarkSetting() {
       cameraSettingsDatastore.setWatermarkSetting()
    }

    override suspend  fun setHeifPicturesSetting() {
        cameraSettingsDatastore.setHeifPicturesSetting()
    }

    override suspend  fun setHevcVideosSetting() {
        cameraSettingsDatastore.setHevcVideosSetting()
    }

    override suspend fun getState(): CameraSettingsState {
        return cameraSettingsDatastore.getState()
    }

}