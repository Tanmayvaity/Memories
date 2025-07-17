package com.example.memories.core.data.data_source

import android.R.attr.bitmap
import android.R.id.input
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.memories.core.util.createTempFile
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MediaManager(
    val context: Context
) {

    companion object {
        private const val TAG = "MediaManager"
    }


    // reads the list of provided uri, copies them to the generated file and returns the list of File
    suspend fun sharedUriToInternalUri(
        uriList: List<Uri>
    ): List<File> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val internalFiles = mutableListOf<File>()
        try {
            uriList.forEach { uri ->
                val file = createTempFile(context)
                resolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file)?.use { output ->
                        input.copyTo(output)
                    }

                }
                internalFiles.add(file)
            }
        } catch (e: Exception) {
            Log.e(TAG, "sharedUriToInternalUri: ${e.message}")
            e.printStackTrace()
        }
        return@withContext internalFiles
    }


    suspend fun uriToBitmap(
        uri: Uri
    ): BitmapResult = withContext(Dispatchers.IO) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            return@withContext BitmapResult.Success(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext BitmapResult.Error(e)
        }
    }

    suspend fun downloadImageWithBitmap(
        bitmap: Bitmap
    ): MediaResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Memories")
        }
        val sharedImageUri =
            resolver.insert(imageCollection, imageDetails) ?: return@withContext MediaResult.Error(
                NullPointerException("Destination uri is null")
            )

        try {
            resolver.openOutputStream(sharedImageUri)?.use { output ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    output
                )
            }
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(sharedImageUri, imageDetails, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            resolver.delete(sharedImageUri, null, null)
            return@withContext MediaResult.Error(e)
        }
        Log.d(TAG, "sharedImageUri:${sharedImageUri} ")

        return@withContext MediaResult.Success("Image Saved Successfully")

    }

    suspend fun downloadVideo(
        uri : Uri
    ): MediaResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val videoDetails = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "video_${System.currentTimeMillis()}")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.IS_PENDING, 1)
            put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/Memories")
        }
        val sharedVideoUri =
            resolver.insert(videoCollection, videoDetails) ?: return@withContext MediaResult.Error(
                NullPointerException("Destination uri is null")
            )

        try {
            resolver.openOutputStream(sharedVideoUri)?.use { output ->
                resolver.openInputStream(uri)?.use{ input ->
                    input.copyTo(output)
                }
            }
            videoDetails.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(sharedVideoUri, videoDetails, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            resolver.delete(sharedVideoUri, null, null)
            return@withContext MediaResult.Error(e)
        }
        Log.d(TAG, "sharedVideoUri:${sharedVideoUri} ")

        return@withContext MediaResult.Success("Video Saved Successfully")

    }

    suspend fun saveBitmapToInternalStorage(
        bitmap: Bitmap?,
    ): CaptureResult =
        withContext(Dispatchers.IO) {
            if (bitmap == null) throw NullPointerException("Bitmap Null")

            val file = createTempFile(context = context)
            try {
                FileOutputStream(file)?.use { output ->

                    bitmap?.let {
                        it.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            output
                        )
                    }

                }


                val uri = Uri.fromFile(file)
                Log.i(TAG, "internal bitmap uri : ${uri.toString()}")
                return@withContext CaptureResult.Success(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext CaptureResult.Error(e)
            }
        }

    suspend fun fetchMediaFromShared(): Flow<MediaImage> = flow {
        val images = mutableListOf<MediaImage>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH
        )
//        val selection = if (fromApp) "${MediaStore.Images.Media.RELATIVE_PATH} = ?" else
//            "${MediaStore.Images.Media.RELATIVE_PATH} != ?"
//        val selectionArgs = arrayOf("Pictures/Memories/")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC "


        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri = ContentUris.withAppendedId(collection, id)
                val media = MediaImage(uri = contentUri, displayName = name)
                images.add(media)
                emit(media)
            }
        }
    }.flowOn(Dispatchers.IO)


    private fun getCollection(): Uri {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        return collection
    }


    suspend fun observeMediaChanges() = callbackFlow {
        val callback = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                trySend(Unit)
            }

        }

        context.contentResolver.registerContentObserver(
            getCollection(),
            true,
            callback
        )

        awaitClose {
            context.contentResolver.unregisterContentObserver(callback)

        }

    }


    suspend fun deleteMedia(
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "deleteMedia: uri to be delete : ${uri.toString()}")
        try {
            val resolver = context.contentResolver
            resolver.delete(uri, null, null)
            Log.d(TAG, "deleteMedia: deleted ")
        } catch (e: Exception) {
            Log.e(TAG, "deleteMedia: Error : ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun deleteMedias(
        uriList: List<Uri>
    ) = withContext(Dispatchers.IO) {

        uriList.forEach { uri ->
            Log.d(TAG, "deleteMedia: uri to be delete : ${uri.toString()}")
            try {
                val resolver = context.contentResolver
                resolver.delete(uri, null, null)
                Log.d(TAG, "deleteMedia: deleted ")
            } catch (e: Exception) {
                Log.e(TAG, "deleteMedia: Error : ${e.message}")
                e.printStackTrace()
            }
        }

    }


}