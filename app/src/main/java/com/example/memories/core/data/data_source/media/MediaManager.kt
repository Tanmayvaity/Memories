package com.example.memories.core.data.data_source.media

import android.R
import android.R.attr.bitmap
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import com.example.memories.core.util.createTempFile
import com.example.memories.core.util.createVideoFile
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.UnsupportedOperationException

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
                val file = if (context.contentResolver.getType(uri)?.startsWith("video") == true) {
                    createVideoFile(context)
                } else {
                    createTempFile(context)
                }
                resolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file)?.use { output ->
                        input.copyTo(output)
                    }

                }
                if (file != null) {
                    internalFiles.add(file)
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "sharedUriToInternalUri: ${e.message}")
            e.printStackTrace()
        }
        return@withContext internalFiles
    }

    // takes list of uri in cache directory and copies them to external storage
    suspend fun saveToInternalStorage2(uriList: List<Uri>): Result<List<Uri>> =
        withContext(Dispatchers.IO) {
            val resultUriList = mutableListOf<Uri>()
            val resolver = context.contentResolver
            try {
                Log.d(TAG, "saveToInternalStorage: ${uriList == null}")
                uriList.forEach { uri ->
                    Log.d(TAG, "saveToInternalStorage: ${uri} ${R.attr.type}")
                    val type = uri.mapToType()
                    val file = if (type == Type.IMAGE_JPG || type == Type.IMAGE_PNG) {
                        createTempFile(
                            context,
                            "images",
                            context.getExternalFilesDir(null)!!,
                            "IMG_"
                        )
                    } else {
                        createVideoFile(
                            context,
                            "videos",
                            context.getExternalFilesDir(null)!!,
                            "VID_"
                        )
                    }
                    Log.d(TAG, "saveToInternalStorage: file : ${file!!.path}")

                    resolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    file?.let {
//                        val uriType = UriType(
//                            uri = file.toUri().toString(),
//                            type = type
//                        )
                        resultUriList.add(file.toUri())
                        Log.d(TAG, "saveToInternalStorage: ${uri.path}")
                        Log.i(TAG, "uri saved successfully")
                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "saveToInternalStorage: ${e.message}")
                return@withContext Result.Error(e)
            }

            return@withContext Result.Success(resultUriList)
        }


    suspend fun saveToInternalStorage(
        uriList: List<Uri>,
    ): Result<List<Uri>> = withContext(Dispatchers.IO) {

        val resolver = context.contentResolver
        val baseDir = context.getExternalFilesDir(null)
            ?: throw IllegalStateException("External files dir not available")
        runCatching {
            uriList.map { uri ->

                val mimeType = getMimeType(uri)

                val extension = getExtension(mimeType)

                val type = getType(mimeType)
                if (type == Type.UNKNOWN_TYPE) throw IllegalArgumentException("Unknown type")

                if (type == Type.VIDEO_MP4) throw UnsupportedOperationException("Video saving not implemented")


                Log.i(
                    TAG,
                    "saveToInternalStorage: mimeType -> $mimeType extension -> $extension type -> $type"
                )
                val dirName = if (type.isImageFile()) "images" else "videos"
                val prefix = if (type.isImageFile()) "IMG_" else "VID_"

                val targetDir = File(baseDir, dirName).apply {
                    if (!exists()) {
                        mkdirs()
                    }

                }
                val file = File(
                    targetDir,
                    "$prefix${System.currentTimeMillis()}.$extension"
                )
                val bitmap = uriToBitmap(uri).getOrNull()
                    ?: throw IllegalStateException("Bitmap is null")
                val size = getBitmapSize(bitmap)
                Log.i(TAG, "saveToInternalStorage: bitmap size -> ${size.toFloat() / 1024 / 1024}")

                resolver.openOutputStream(file.toUri())?.use { output ->
                    val format = when {
                        type == Type.IMAGE_JPG -> Bitmap.CompressFormat.JPEG
                        type == Type.IMAGE_PNG -> Bitmap.CompressFormat.PNG
                        else -> Bitmap.CompressFormat.JPEG
                    }
                    bitmap.compress(format, 80, output)
                    Log.i(
                        TAG,
                        "saveToInternalStorage: bitmap size after compression -> ${
                            file.length().toFloat() / 1024 / 1024
                        }"
                    )
                } ?: throw IllegalStateException("Unable to open output stream")
                Log.d(TAG, "saveToInternalStorage: file name : ${file.name} at ${file.path}")

                bitmap.recycle()

                file.toUri()
            }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    suspend fun saveToCacheStorage(
        uri: Uri,
        bitmap: Bitmap
    ): Result<Uri> = withContext(Dispatchers.IO) {

        val resolver = context.contentResolver
        val baseDir = context.cacheDir
            ?: throw IllegalStateException("Cache files dir not available")
        runCatching {
            val mimeType = getMimeType(uri)

            val extension = getExtension(mimeType)

            val type = getType(mimeType)
            if (type == Type.UNKNOWN_TYPE) throw IllegalArgumentException("Unknown type")

            if (type == Type.VIDEO_MP4) throw UnsupportedOperationException("Video saving not implemented")


            Log.i(
                TAG,
                "saveToCacheStorage: mimeType -> $mimeType extension -> $extension type -> $type"
            )
            val dirName = if (type.isImageFile()) "images" else "videos"
            val prefix = if (type.isImageFile()) "IMG_" else "VID_"

            val targetDir = File(baseDir, dirName).apply {
                if (!exists()) {
                    mkdirs()
                }

            }
            val file = File(
                targetDir,
                "$prefix${System.currentTimeMillis()}.$extension"
            )
//            val bitmap = uriToBitmap(uri).getOrNull()
//                ?: throw IllegalStateException("Bitmap is null")
            val size = getBitmapSize(bitmap)
            Log.i(TAG, "saveToCacheStorage: bitmap size -> ${size.toFloat() / 1024 / 1024}")

//            val contentUri = FileProvider.getUriForFile(
//                context,
//                "com.example.memories.fileprovider",
//                file
//            )

            file.outputStream()?.use { output ->
                val format = when (type) {
                    Type.IMAGE_PNG -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }
                bitmap.compress(format, 100, output)
                Log.i(
                    TAG,
                    "saveToCacheStorage: bitmap size after compression -> ${
                        file.length().toFloat() / 1024 / 1024
                    }"
                )
            } ?: throw IllegalStateException("Unable to open output stream")

//            resolver.openOutputStream(contentUri)?.use { output ->
//                val format = when {
//                    type == Type.IMAGE_JPG -> Bitmap.CompressFormat.JPEG
//                    type == Type.IMAGE_PNG -> Bitmap.CompressFormat.PNG
//                    else -> Bitmap.CompressFormat.JPEG
//                }
//                bitmap.compress(format, 100, output)
//                Log.i(
//                    TAG,
//                    "saveToCacheStorage: bitmap size after compression -> ${
//                        file.length().toFloat() / 1024 / 1024
//                    }"
//                )
//            } ?: throw IllegalStateException("Unable to open output stream")
            Log.d(TAG, "saveToCacheStorage  : file name : ${file.name} at ${file.path}")
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            bitmap.recycle()

            contentUri
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    suspend fun saveToCacheStorageWithUri(
        uri: Uri,
    ): Result<Uri> = withContext(Dispatchers.IO) {

        val resolver = context.contentResolver
        val baseDir = context.cacheDir
            ?: throw IllegalStateException("Cache files dir not available")
        runCatching {
            val mimeType = getMimeType(uri)

            val extension = getExtension(mimeType)

            val type = getType(mimeType)
            if (type == Type.UNKNOWN_TYPE) throw IllegalArgumentException("Unknown type")

            if (type == Type.VIDEO_MP4) throw UnsupportedOperationException("Video saving not implemented")


            Log.i(
                TAG,
                "saveToCacheStorage: mimeType -> $mimeType extension -> $extension type -> $type"
            )
            val dirName = if (type.isImageFile()) "images" else "videos"
            val prefix = if (type.isImageFile()) "IMG_" else "VID_"

            val targetDir = File(baseDir, dirName).apply {
                if (!exists()) {
                    mkdirs()
                }

            }
            val file = File(
                targetDir,
                "$prefix${System.currentTimeMillis()}.$extension"
            )

            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file)?.use { output ->
                    input.copyTo(output)
                }

            }
            Log.d(TAG, "saveToCacheStorage  : file name : ${file.name} at ${file.path}")
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            contentUri
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }
    private fun getMimeType(
        uri: Uri
    ): String {
        val resolver = context.contentResolver
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                resolver.getType(uri)
            }

            ContentResolver.SCHEME_FILE -> {
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                extension?.let { it ->
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                }
            }

            else -> null
        } ?: throw IllegalArgumentException("Unable to determine mimetype")
    }

    private fun getExtension(mimeType: String): String {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?: throw IllegalArgumentException("Unable to determine extension from mimetype")
    }

    private fun getType(mimeType: String): Type {
        return when (mimeType) {
            "image/jpeg" -> Type.IMAGE_JPG
            "image/jpg" -> Type.IMAGE_JPG
            "image/png" -> Type.IMAGE_PNG
            "video/mp4" -> Type.VIDEO_MP4
            else -> Type.UNKNOWN_TYPE
        }
    }

    private fun getBitmapSize(bitmap: Bitmap): Int = bitmap.allocationByteCount


    suspend fun uriToBitmap(
        uri: Uri,
        degrees: Float = 0f
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        if (uri == null) throw IllegalArgumentException("Uri is null")

        try {
            val source = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            val normalizedRotation = degrees.normalizeRotation()
            val bitmap = if (normalizedRotation != 0f && source != null) {
                val matrix = Matrix().apply {
                    postRotate(
                        normalizedRotation,
                        source.width / 2f,
                        source.height / 2f

                    )
                }

                val rotated = Bitmap.createBitmap(
                    source,
                    0,
                    0,
                    source.width,
                    source.height,
                    matrix,
                    true
                )
                if (rotated != source) source.recycle()

                rotated

            } else {
                source
            }

            return@withContext Result.Success(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.Error(e)
        }
    }

    suspend fun downloadImageWithBitmap(
        bitmap: Bitmap
    ): Result<String> = withContext(Dispatchers.IO) {
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
            resolver.insert(imageCollection, imageDetails) ?: return@withContext Result.Error(
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
            return@withContext Result.Error(e)
        }
        Log.d(TAG, "sharedImageUri:${sharedImageUri} ")

        return@withContext Result.Success("Image Saved Successfully")

    }

    suspend fun downloadVideo(
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
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
            resolver.insert(videoCollection, videoDetails) ?: return@withContext Result.Error(
                NullPointerException("Destination uri is null")
            )

        try {
            resolver.openOutputStream(sharedVideoUri)?.use { output ->
                resolver.openInputStream(uri)?.use { input ->
                    input.copyTo(output)
                }
            }
            videoDetails.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(sharedVideoUri, videoDetails, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            resolver.delete(sharedVideoUri, null, null)
            return@withContext Result.Error(e)
        }
        Log.d(TAG, "sharedVideoUri:${sharedVideoUri} ")

        return@withContext Result.Success("Video Saved Successfully")

    }

    suspend fun saveBitmapToInternalStorage(
        bitmap: Bitmap?,
    ): Result<Uri> =
        withContext(Dispatchers.IO) {
            if (bitmap == null) {
                return@withContext Result.Error(Throwable("Bitmap is Null"))
            }

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
                return@withContext Result.Success(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.Error(e)
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun fetchMediaFromShared(
        offset: Int = 0,
        limit: Int = 10
    ): List<MediaObject> = withContext(Dispatchers.IO) {
        val images = mutableListOf<MediaObject>()
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


        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("Pictures/Memories/")
        val sortOrder = MediaStore.Images.Media.DATE_ADDED

        val queryArgs = Bundle()

        queryArgs.also {
//            it.putString(ContentResolver.QUERY_ARG_SQL_SELECTION,selection)
//            it.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,selectionArgs)
            it.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(sortOrder))
            it.putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            it.putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
            it.putInt(ContentResolver.QUERY_ARG_OFFSET, offset)

        }
        var count = 0;

        context.contentResolver.query(
            collection,
            projection,
            queryArgs,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri = ContentUris.withAppendedId(collection, id)
                val bitmap = context.contentResolver.loadThumbnail(contentUri, Size(640, 480), null)
                val media = MediaObject(uri = contentUri, displayName = name, bitmap = bitmap)
                images.add(media)
                count = count + 1
            }
        }
        Log.d(TAG, "fetchMediaFromShared: rows : ${count}")
        return@withContext images
    }


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

    suspend fun deleteInternalMedia(
        uriList: List<Uri>
    ): Result<String> = withContext(Dispatchers.IO) {
        uriList.forEach { uri ->
            try {
                uri.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteInternalMedia: ${e}")
                Result.Error(e)
            }

        }
        Result.Success("Media Deleted Successfully")
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getMediaThumbnail(
        uri: Uri,
        size: Size
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val bitmap = context.contentResolver.loadThumbnail(
                uri, size, null
            )

            return@withContext Result.Success(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.Error(e)
        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun applyFilter(
        uri: Uri,
        shaderCode: String,
        rotationDegrees: Float = 0f
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val source = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, uri)
            )

            // Convert to software bitmap for shader processing
            var bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
            if (bitmap != source) source.recycle()

            val normalizedRotation = rotationDegrees.normalizeRotation()
            if (normalizedRotation != 0f && bitmap != null) {
                val matrix = Matrix().apply {
                    postRotate(normalizedRotation, bitmap.width / 2f, bitmap.height / 2f)
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0, 0,
                    bitmap.width, bitmap.height,
                    matrix,
                    true
                )
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                    bitmap = rotatedBitmap
                }
            }

            if (bitmap == null) return@withContext null


            val runtimeShader = RuntimeShader(shaderCode.trimIndent())
            bitmap?.let {
                runtimeShader.setInputShader(
                    "inputShader",
                    BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                )
            }

//            val output = if(bitmap != null){
//                Bitmap.createBitmap(
//                    bitmap.width,
//                    bitmap.height,
//                    Bitmap.Config.ARGB_8888
//                )
//            }else{
//                null
//            }

//            output?.let{
//                Canvas(output!!)
//                    .drawRect(0f,0f,it.width.toFloat(),it.height.toFloat(), Paint().apply { shader = runtimeShader })
//                // Convert Picture to Bitma
//            }

            val paint = Paint().apply { shader = runtimeShader }

            // Use Picture for hardware accelerated recording
            val picture = Picture()
            val canvas = picture.beginRecording(source.width, source.height)
            canvas.drawRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), paint)
            picture.endRecording()

            // Convert Picture to Bitmap
            val output = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Bitmap.createBitmap(picture)
            } else {
                val bitmap =
                    Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
                Canvas(bitmap).drawPicture(picture)
                bitmap
            }

            bitmap?.recycle()
            output
        } catch (e: Exception) {
            Log.e(TAG, "applyFilter: error ${e}")
            e.printStackTrace()
            null

        }

    }


    fun Float.normalizeRotation(): Float {
        val normalized = this % 360f
        return if (normalized < 0) normalized + 360f else normalized
    }


}