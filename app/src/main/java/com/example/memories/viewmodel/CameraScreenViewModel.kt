package com.example.memories.viewmodel

import android.content.Context
import android.util.Range
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExposureState
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.exp

class CameraScreenViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
    val lensFacing = _lensFacing.asStateFlow()

    private val _zoomScale = MutableStateFlow(0f)
    val zoomScale = _zoomScale.asStateFlow()

    private val _exposureValue : MutableStateFlow<Int> = MutableStateFlow(0)
    val exposureValue = _exposureValue.asStateFlow()

    private val _torchState  = MutableStateFlow(false)
    val torchState = _torchState.asStateFlow()

    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo : CameraInfo? = null
    private var camera:Camera? = null


    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
        }

    }

    fun toggleCamera() {
        if (_lensFacing.value == CameraSelector.LENS_FACING_BACK) {
            _lensFacing.update { CameraSelector.LENS_FACING_FRONT }
        } else {
            _lensFacing.update { CameraSelector.LENS_FACING_BACK }
        }
    }


    suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(_lensFacing.value)
            .build()
       camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, cameraPreviewUseCase
        )

        cameraControl = camera?.cameraControl
        cameraInfo = camera?.cameraInfo

        cameraControl?.setLinearZoom(_zoomScale.value)
        cameraControl?.setExposureCompensationIndex(_exposureValue.value)
        cameraControl?.enableTorch(_torchState.value)



        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
            cameraControl = null
            cameraInfo = null
            camera = null
        }
    }

    fun tapToFocus(tapCoords : Offset){
        val point : MeteringPoint? = surfaceMeteringPointFactory?.createPoint(tapCoords.x,tapCoords.y)

        if(point!=null){
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
        _exposureValue.update{0}
        cameraControl?.setExposureCompensationIndex(_exposureValue.value)

    }

    fun zoom(scale : Float){
        _zoomScale.update { scale }
        cameraControl?.setLinearZoom(_zoomScale.value)

    }

    fun changeExposure(value : Int){
        _exposureValue.update { value }
        if(!isExposureSupported()){
            return
        }

        val range = getExposureRange()

        if(range.contains(value)){
            cameraControl?.setExposureCompensationIndex(value)
        }
    }

    fun getExposureRange(): Range<Int> {
        return cameraInfo?.exposureState?.exposureCompensationRange!!
    }

    fun isExposureSupported():Boolean {
        return cameraInfo?.exposureState?.isExposureCompensationSupported!!
    }

    fun toggleTorch(){
        val torch = !(_torchState.value)
        _torchState.update { !_torchState.value }
        cameraControl?.enableTorch(torch)
    }



}