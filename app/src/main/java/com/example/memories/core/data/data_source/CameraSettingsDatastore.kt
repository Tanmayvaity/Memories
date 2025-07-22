package com.example.memories.core.data.data_source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.memories.core.domain.model.CameraSettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CameraSettingsDatastore(
    val context : Context
) {
    companion object{
        private val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = "camera_settings")
        private val SHUTTER_SOUND_KEY = booleanPreferencesKey("shutter_sound")
        private val SAVE_LOCATION_KEY = booleanPreferencesKey("save_location")
        private val MIRROR_IMAGE_KEY = booleanPreferencesKey("mirror_image")
        private val GRID_LINES_KEY = booleanPreferencesKey("grid_lines")
        private val FLIP_CAMERA_KEY = booleanPreferencesKey("flip_input")
        private val WATERMARK_KEY = booleanPreferencesKey("watermark")
        private val HEIF_PICTURES_KEY = booleanPreferencesKey("heif_pictures")
        private val HEVC_VIDEOS_KEY = booleanPreferencesKey("hevc_videos")



    }

    // read
    val enableShutterSound = context.datastore.data.map { preferences ->
        preferences[SHUTTER_SOUND_KEY] ?: true
    }
    val enableSaveLocation = context.datastore.data.map { preferences ->
        preferences[SAVE_LOCATION_KEY] ?: false
    }
    val enableMirrorImage = context.datastore.data.map { preferences ->
        preferences[MIRROR_IMAGE_KEY] ?: false
    }
    val enableGridLines = context.datastore.data.map { preferences ->
        preferences[GRID_LINES_KEY] ?: false
    }
    val enableFlipCamera = context.datastore.data.map { preferences ->
        preferences[FLIP_CAMERA_KEY] ?: true
    }
    val enableWatermark = context.datastore.data.map { preferences ->
        preferences[WATERMARK_KEY] ?: false
    }
    val enableHeifPictures = context.datastore.data.map { preferences ->
        preferences[HEIF_PICTURES_KEY] ?: false
    }
    val enableHevcVideos = context.datastore.data.map { preferences ->
        preferences[HEVC_VIDEOS_KEY] ?: false
    }

    // write
    suspend fun setShutterSoundSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[SHUTTER_SOUND_KEY] ?: true
            preferences[SHUTTER_SOUND_KEY] = !enable
        }
    }
    suspend fun setSaveLocationSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[SAVE_LOCATION_KEY] ?: false
            preferences[SAVE_LOCATION_KEY] = !enable
        }
    }

    // preview mirror
    suspend fun setMirrorImageSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[MIRROR_IMAGE_KEY] ?: false
            preferences[MIRROR_IMAGE_KEY] = !enable
        }
    }
    suspend fun setGridLinesSeting(){
        context.datastore.edit { preferences ->
            val enable = preferences[GRID_LINES_KEY] ?: false
            preferences[GRID_LINES_KEY] = !enable
        }
    }

    // flip using swipe
    suspend fun setEnableFlipCameraSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[FLIP_CAMERA_KEY] ?: true
            preferences[FLIP_CAMERA_KEY] = !enable
        }
    }

    suspend fun setWatermarkSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[WATERMARK_KEY] ?: false
            preferences[WATERMARK_KEY] = !enable
        }
    }
    suspend fun setHeifPicturesSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[HEIF_PICTURES_KEY] ?: false
            preferences[HEIF_PICTURES_KEY] = !enable
        }
    }
    suspend fun setHevcVideosSetting(){
        context.datastore.edit { preferences ->
            val enable = preferences[HEVC_VIDEOS_KEY] ?: false
            preferences[HEVC_VIDEOS_KEY] = !enable
        }
    }

    suspend fun getState(): CameraSettingsState {
        return CameraSettingsState(
            shutterSound = enableShutterSound.first(),
            saveLocation = enableSaveLocation.first(),
            mirrorImage = enableMirrorImage.first(),
            gridLines = enableGridLines.first(),
            flipCameraUsingSwipe = enableFlipCamera.first(),
            watermark = enableWatermark.first(),
            heifPictures = enableHeifPictures.first(),
            hevcVideos = enableHevcVideos.first()
        )
    }





}