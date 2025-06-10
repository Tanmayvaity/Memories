package com.example.memories.model.camera

import android.content.Context
import android.util.Range
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.model.camera.CameraManager
import com.example.memories.model.models.AspectRatio
import com.example.memories.model.models.CaptureResult
import java.io.File

class CameraRepository {
    private var cameraManager : CameraManager = CameraManager()


    fun setSurfaceRequestCallback(callback : (SurfaceRequest)-> Unit){
        cameraManager.setSurfaceRequestCallback(callback)
    }

    suspend fun  bindToCameraUseCase(
        appContext : Context,
        lifecycleOwner : LifecycleOwner,
        lensFacing : Int,
        zoomScale : Float,
        torchEnabledState : Boolean,
        exposureScale : Int
        ){
        cameraManager.bindToCamera(
            appContext,lifecycleOwner,lensFacing,zoomScale,torchEnabledState,exposureScale
        )
    }


    fun toggleTorchState(torch : Boolean){
        cameraManager.toggleTorchState(torch)
    }

    fun tapToFocus(tapCoords : Offset){
        cameraManager.tapToFocus(tapCoords)
    }

    fun zoom(scale : Float){
        cameraManager.zoom(scale)
    }

    fun changeExposure(value : Int){
        cameraManager.changeExposure(value)
    }

    fun getExposureRange(): Range<Int> {
        return cameraManager.getExposureRange()
    }

    fun isExposureSupported():Boolean {
        return cameraManager.isExposureSupported()
    }

    suspend fun takePicture(
        file : File
    ) : CaptureResult {
        return cameraManager.takePicture(file)
    }

    fun setAspectRatio(aspectRatio : AspectRatio){
        cameraManager.setAspectRatio(aspectRatio)
    }



}