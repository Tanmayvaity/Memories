package com.example.memories.feature.feature_camera.data.data_source

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.net.Uri
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
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import com.example.memories.core.domain.model.Result
import com.example.memories.core.util.createTempFile
import com.example.memories.core.util.createVideoFile
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume


class CameraManager(
    val context: Context
) {
    companion object {
        private const val TAG = "CameraManager"
    }


    private var surfaceRequestCallback: ((SurfaceRequest) -> Unit)? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    private lateinit var cameraPreviewUseCase: Preview
    private lateinit var imageCaptureUseCase: ImageCapture
    private lateinit var videoCaptureUseCase: VideoCapture<Recorder>
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var recorder: Recorder
    private var videoAspectRatio: Int = androidx.camera.core.AspectRatio.RATIO_4_3
    private var cameraRecording: Recording? = null

    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory
    private val resolutionSelectorBuilder = ResolutionSelector.Builder()


    private val qualitySelector = QualitySelector.fromOrderedList(
        listOf(
            Quality.UHD, Quality.FHD, Quality.HD, Quality.SD,
        ),
        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
    )


    init {
        setAspectRatio(AspectRatio.RATIO_4_3)

//        initUseCases()

    }


    fun initUseCases() {
//        val executor: Executor = Executors.newSingleThreadExecutor()


        cameraPreviewUseCase = Preview.Builder()
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()

        cameraPreviewUseCase!!.setSurfaceProvider { surfaceRequest ->
            surfaceRequestCallback?.invoke(surfaceRequest)
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                surfaceRequest.resolution.width.toFloat(),
                surfaceRequest.resolution.height.toFloat()
            )
        }


        imageCaptureUseCase = ImageCapture.Builder()
            .setTargetRotation(cameraPreviewUseCase!!.targetRotation)
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .build()

        recorder = Recorder.Builder()
            .setExecutor(cameraExecutor)
            .setQualitySelector(qualitySelector)
            .setAspectRatio(videoAspectRatio)
            .build()
        videoCaptureUseCase = VideoCapture.withOutput(recorder)
//        videoCaptureUseCase = VideoCapture.Builder(recorder).build()
    }


    suspend fun bindToCamera(
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing = LensFacing.BACK,
        torch: Boolean = false
    ) {
        Log.d(TAG, "bindToCamera: lensFacing : ${lensFacing.toString()}")

        if (cameraRecording != null) {
            pauseRecording()
        }
        processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        unbind(processCameraProvider)
        try {
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(if (lensFacing == LensFacing.BACK) LENS_FACING_BACK else LENS_FACING_FRONT)
                .build()
            val camera = processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                UseCaseGroup.Builder()
                    .addUseCase(cameraPreviewUseCase)
                    .addUseCase(imageCaptureUseCase)
                    .addUseCase(videoCaptureUseCase)
                    .build()
            )

            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            cameraControl?.enableTorch(torch)
            if (cameraRecording != null) {
                resumeRecording()
            }


            Log.d(TAG, "Torch Value : ${torch}")

            // Cancellation signals we're done with the camera
            try {
                awaitCancellation()
            } finally {
                unbind(processCameraProvider)
            }
        } catch (e: Exception) {
            Log.e(TAG, "bindToCamera: ${e.message}")
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

        videoAspectRatio =
            if (aspectRatio == AspectRatio.RATIO_4_3) androidx.camera.core.AspectRatio.RATIO_4_3 else
                androidx.camera.core.AspectRatio.RATIO_16_9

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

    suspend fun takePicture(): Result<Uri> {
        val file = createTempFile(context)

        if(file == null) return Result.Error(NullPointerException("File is null"))

        if (imageCaptureUseCase == null) {
            val error = IllegalStateException("ImageCapture use case not initialized")
            Log.e(TAG, "${error.message}")
            return Result.Error(error)
        }

        return suspendCancellableCoroutine { continuation ->
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "${outputFileResults.savedUri}")
                    if (outputFileResults.savedUri == null) {
                        Log.e(TAG, "onImageSaved: savedUri is null")
                        return
                    }
                    continuation.resume(Result.Success(outputFileResults.savedUri))
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "${exception.message}")
                    continuation.resume(Result.Error(exception))
                }
            }

            continuation.invokeOnCancellation {
                Log.d(TAG, "Coroutine Cancelled")
            }
            val executor: Executor = Executors.newSingleThreadExecutor()

            imageCaptureUseCase.takePicture(outputFileOptions, executor, imageSavedCallback)


        }
    }

    @SuppressLint("MissingPermission")
    suspend fun takeVideo(): Result<Uri> = suspendCancellableCoroutine { continuation ->
        val file = createVideoFile(context)
        if (videoCaptureUseCase == null) {
            val error = IllegalStateException("VideoCaptureUseCase use case not initialized")
            Log.e(TAG, "${error.message}")
            return@suspendCancellableCoroutine continuation.resume(Result.Error(error))
        }
        val fileOutputOptions: FileOutputOptions = FileOutputOptions.Builder(file).build()
        Log.i(TAG, "takeVideo: file : ${fileOutputOptions.file.path}")
        var videoUri: Uri? = null

        cameraRecording = videoCaptureUseCase.output
            .prepareRecording(context, fileOutputOptions)
            .asPersistentRecording()
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context), object : Consumer<VideoRecordEvent> {
                override fun accept(value: VideoRecordEvent) {
                    when (value) {
                        is VideoRecordEvent.Start -> {
                            Log.i(TAG, "accept: Recording has started")
                        }

                        is VideoRecordEvent.Finalize -> {


                            if (!value.hasError()) {
                                videoUri = value.outputResults.outputUri
                                Log.i(
                                    TAG,
                                    "accept: successful video capture : ${videoUri.toString()}"
                                )
                                if (videoUri == null) {
                                    return continuation.resume(Result.Error(Throwable("Uri is null")))

                                }
                                continuation.resume(Result.Success(videoUri))
                            } else {
                                Log.e(
                                    TAG,
                                    "Recording has failed with an exception : ${value.cause?.message}"
                                )


                                continuation.resume(
                                    Result.Error(
                                        value.cause ?: Throwable("Cause is Null")
                                    )
                                )
                            }
                        }

                        is VideoRecordEvent.Pause -> {
                            Log.i(TAG, "accept: Recording has paused")
                        }

                        is VideoRecordEvent.Resume -> {
                            Log.i(TAG, "accept: Recording has resumed")
                        }
                    }

                }
            })


    }

    fun pauseRecording() {


        if (cameraRecording == null) {
            Log.e(TAG, "pauseRecording: cameraRecording is null")
            return
        }

        cameraRecording?.pause()
        Log.i(TAG, "pauseRecording")


    }

    fun resumeRecording() {

        if (cameraRecording == null) {
            Log.e(TAG, "resumeRecording: cameraRecording is null")
            return
        }

        cameraRecording?.resume()
        Log.i(TAG, "resume recording")

    }

    fun stopRecording() {


        if (cameraRecording == null) {
            Log.e(TAG, "stopRecording: cameraRecording is null")
            return
        }

        cameraRecording?.stop()
        cameraRecording = null
        Log.i(TAG, "stop recording")


    }

    fun cancelRecording() {
        if (cameraRecording == null) {
            Log.e(TAG, "cancelRecording : cameraRecording is null")
            return
        }

        cameraRecording?.close()
        cameraRecording = null
        Log.i(TAG, "cancelRecording: cancel recording")
    }

}




