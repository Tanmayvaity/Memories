package com.example.memories.feature.feature_memory.data.data_source

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class ImageTagSuggester(
    private val context: Context,
) {
    private val labeler by lazy {
        ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(CONFIDENCE_THRESHOLD)
                .build()
        )
    }

    suspend fun suggest(uri: Uri): List<String> {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            suspendCancellableCoroutine { continuation ->
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        val result = labels
                            .sortedByDescending { it.confidence }
                            .take(MAX_SUGGESTIONS)
                            .map { it.text }
                        continuation.resume(result)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "labeling failed: ${e.message}")
                        continuation.resume(emptyList())
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "suggest error: ${e.message}")
            emptyList()
        }
    }

    companion object {
        private const val TAG = "ImageTagSuggester"
        private const val CONFIDENCE_THRESHOLD = 0.6f
        private const val MAX_SUGGESTIONS = 8
    }
}
