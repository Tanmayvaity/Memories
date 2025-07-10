package com.example.memories.feature.feature_camera.data.data_source

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class CameraManager {
    companion object {
        private const val TAG = "CameraManager"
    }


    private var surfaceRequestCallback: ((SurfaceRequest) -> Unit)? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    private lateinit var cameraPreviewUseCase: Preview
    private lateinit var imageCaptureUseCase: ImageCapture
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory
    private val resolutionSelectorBuilder = ResolutionSelector.Builder()

//    private val cameraPreviewUseCase = Preview.Builder().build().apply {
//        setSurfaceProvider { surfaceRequest ->
//            surfaceRequestCallback?.invoke(surfaceRequest)
//        }
//
//    }
//
//    private val  imageCaptureUseCase  = ImageCapture.Builder()
//        .setTargetRotation(cameraPreviewUseCase!!.targetRotation)
//        .build()


    init {
        setAspectRatio(AspectRatio.RATIO_4_3)

//        initUseCases()

    }

    fun initUseCases() {
        cameraPreviewUseCase = Preview.Builder()
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()

        cameraPreviewUseCase!!.setSurfaceProvider { surfaceRequest ->
            surfaceRequestCallback?.invoke(surfaceRequest)
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                surfaceRequest.resolution.width.toFloat(),
                surfaceRequest.resolution.height.toFloat())
        }


        imageCaptureUseCase = ImageCapture.Builder()
            .setTargetRotation(cameraPreviewUseCase!!.targetRotation)
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()
    }


    suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing = LensFacing.BACK,
        torch: Boolean = false
    ) {

        processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        unbind(processCameraProvider)
        try{
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(if (lensFacing == LensFacing.BACK) LENS_FACING_BACK else LENS_FACING_FRONT)
                .build()
            val camera = processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                UseCaseGroup.Builder()
                    .addUseCase(cameraPreviewUseCase)
                    .addUseCase(imageCaptureUseCase)
                    .build()
            )

            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo

            cameraControl?.enableTorch(torch)

            Log.d(TAG, "Torch Value : ${torch}")

            // Cancellation signals we're done with the camera
            try {
                awaitCancellation()
            } finally {
                unbind(processCameraProvider)
            }
        }catch (e : Exception){
            Log.e(TAG, "bindToCamera: ${e.message}", )
            e.printStackTrace()
        }

    }

    fun unbind(processCameraProvider: ProcessCameraProvider) {
        processCameraProvider.unbindAll()
    }

    fun setSurfaceRequestCallback(callback: (SurfaceRequest) -> Unit) {
        surfaceRequestCallback = callback
    }

    fun tapToFocus(tapCoords: Offset) {
        Log.d(TAG, "tapToFocus: offset = ${tapCoords}")
        val point: MeteringPoint? =
            surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)

        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }

        Log.d(TAG, "tapToFocus: called")

    }

    fun setAspectRatio(aspectRatio: AspectRatio = AspectRatio.RATIO_4_3) {
        val aspect =
            if (aspectRatio == AspectRatio.RATIO_4_3) AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
            else AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY


        setAspect(aspect)


        initUseCases()


        Log.d(
            "CameraManager",
            "Aspect Ratio : ${resolutionSelectorBuilder.build().aspectRatioStrategy}"
        )
    }

    private fun setAspect(aspect: AspectRatioStrategy) {
        resolutionSelectorBuilder.setAspectRatioStrategy(aspect)
    }


    @Throws(NullPointerException::class)
    fun torchToggle(torch: Boolean) {
        if (cameraControl == null) throw NullPointerException("Camera Control Null")

        cameraControl?.enableTorch(torch)
    }

    fun zoom(scale: Float) {
        cameraControl?.setLinearZoom(scale)
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
                    if (outputFileResults.savedUri == null) {
                        Log.e(TAG, "onImageSaved: savedUri is null")
                    }
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


}




