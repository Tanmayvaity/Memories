package com.example.memories.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.Range
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraExecutor
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureCapabilities
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.memories.model.models.AspectRatio
import com.example.memories.model.models.CaptureResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class CameraManager {

    private var camera: Camera? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    private  lateinit var previewUseCase: Preview
    private  lateinit var imageCaptureUseCase: ImageCapture

    private var _surfaceRequestCallback: ((SurfaceRequest) -> Unit)? = null
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null

    private val resolutionSelectorBuilder = ResolutionSelector.Builder()


    init {
        setAspectRatio(AspectRatio.RATIO_4_3)

        initUseCases()

    }

    private fun initUseCases(){
        previewUseCase = Preview.Builder()
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()

        previewUseCase!!.setSurfaceProvider { surfaceRequest ->
            _surfaceRequestCallback?.invoke(surfaceRequest)
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                surfaceRequest.resolution.width.toFloat(),
                surfaceRequest.resolution.height.toFloat()
            )
        }

        imageCaptureUseCase = ImageCapture.Builder()
            .setTargetRotation(previewUseCase!!.targetRotation)
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()
    }


    fun setSurfaceRequestCallback(callback: (SurfaceRequest) -> Unit) {
        _surfaceRequestCallback = callback
    }

    suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: Int,
        zoomScale: Float,
        torchEnabledState: Boolean,
        exposureScale: Int
    ) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            UseCaseGroup.Builder()
                .addUseCase(previewUseCase)
                .addUseCase(imageCaptureUseCase)
                .build()
        )

        cameraControl = camera?.cameraControl
        cameraInfo = camera?.cameraInfo



        cameraControl?.setLinearZoom(zoomScale)
        cameraControl?.enableTorch(torchEnabledState)
        cameraControl?.setExposureCompensationIndex(exposureScale)

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
            camera = null
        }

    }

    fun toggleCamera() {
        Log.d("CameraManager", "toggleCamera: ${cameraInfo?.lensFacing.toString()}")
    }


    fun toggleTorchState(torch: Boolean) {
        cameraControl?.enableTorch(torch)
    }

    fun tapToFocus(tapCoords: Offset) {
        val point: MeteringPoint? =
            surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)

        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }

    }

    fun zoom(scale: Float) {
        cameraControl?.setLinearZoom(scale)
    }


    fun changeExposure(value: Int) {
        if (!isExposureSupported()) {
            return
        }

        val range = getExposureRange()

        if (range.contains(value)) {
            cameraControl?.setExposureCompensationIndex(value)
        }
    }

    fun getExposureRange(): Range<Int> {
        return cameraInfo?.exposureState?.exposureCompensationRange!!
    }

    fun isExposureSupported(): Boolean {
        return cameraInfo?.exposureState?.isExposureCompensationSupported!!
    }


    suspend fun takePicture(
        file: File
    ): CaptureResult {
        if (imageCaptureUseCase == null) {
            val error = IllegalStateException("ImageCapture use case not initialized")
            Log.e(TAG, "${error.message}")
            return CaptureResult.Error(error)
        }

        return suspendCancellableCoroutine { continuation ->
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "${outputFileResults.savedUri}")
                    continuation.resume(CaptureResult.Success(outputFileResults.savedUri))
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "${exception.message}")
                    continuation.resume(CaptureResult.Error(exception))
                }
            }

            continuation.invokeOnCancellation {
                Log.d(TAG, "Coroutine Cancelled")
            }
            val executor: Executor = Executors.newSingleThreadExecutor()

            imageCaptureUseCase.takePicture(outputFileOptions, executor, imageSavedCallback)


        }

    }

    fun setAspectRatio(aspectRatio: AspectRatio = AspectRatio.RATIO_4_3) {
        val aspect = if(aspectRatio == AspectRatio.RATIO_4_3) AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
        else AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
        setAspect(aspect)

        initUseCases()


        Log.d("CameraManager", "Aspect Ratio : ${resolutionSelectorBuilder.build().aspectRatioStrategy}")
    }

    private fun setAspect(aspect: AspectRatioStrategy){
        resolutionSelectorBuilder.setAspectRatioStrategy(aspect)
    }

    companion object {
        private const val TAG = "CameraManager"
    }


}