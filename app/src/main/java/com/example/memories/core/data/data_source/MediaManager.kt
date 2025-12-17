package com.example.memories.core.data.data_source

import android.R.attr.type
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.createTempFile
import com.example.memories.core.util.createVideoFile
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log

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
                val file = if (context.contentResolver.getType(uri)?.startsWith("video") == true){
                    createVideoFile(context)
                }else{
                    createTempFile(context)
                }
                resolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file)?.use { output ->
                        input.copyTo(output)
                    }

                }
                if(file!=null){
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
    suspend fun saveToInternalStorage(uriList : List<Uri>): Result<List<Uri>> =
        withContext(Dispatchers.IO){
            val resultUriList = mutableListOf<Uri>()
            val resolver = context.contentResolver
            try {
                Log.d(TAG, "saveToInternalStorage: ${uriList == null}")
                uriList.forEach { uri ->
                    Log.d(TAG, "saveToInternalStorage: ${uri} ${type}")
                    val type = uri.mapToType()
                    val file = if(type  == Type.IMAGE){
                        createTempFile(
                            context,
                            "images",
                            context.getExternalFilesDir(null)!!,
                            "IMG_"
                        )
                    }else{
                        createVideoFile(
                            context,
                            "videos",
                            context.getExternalFilesDir(null)!!,
                            "VID_"
                        )
                    }
                    Log.d(TAG, "saveToInternalStorage: file : ${file!!.path}")

                    resolver.openInputStream(uri)?.use {input ->
                        FileOutputStream(file)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    file?.let{
//                        val uriType = UriType(
//                            uri = file.toUri().toString(),
//                            type = type
//                        )
                        resultUriList.add(file.toUri())
                        Log.d(TAG, "saveToInternalStorage: ${uri.path}")
                        Log.i(TAG, "uri saved successfully")
                    }
                    


                }
            }catch (e : Exception){
                e.printStackTrace()
                Log.e(TAG, "saveToInternalStorage: ${e.message}", )
                return@withContext Result.Error(e)
            }

            return@withContext Result.Success(resultUriList)
        }



    suspend fun uriToBitmap(
        uri: Uri
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
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
        uri : Uri
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
                resolver.openInputStream(uri)?.use{ input ->
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
            if (bitmap == null){
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
        offset : Int = 0,
        limit : Int = 10
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
            it.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS,arrayOf(sortOrder))
            it.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
            it.putInt(ContentResolver.QUERY_ARG_LIMIT,limit)
            it.putInt(ContentResolver.QUERY_ARG_OFFSET,offset)

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
                val bitmap = context.contentResolver.loadThumbnail(contentUri, Size(640,480),null)
                val media = MediaObject(uri = contentUri, displayName = name,bitmap = bitmap)
                images.add(media)
                count  = count + 1
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
    ) : Result<String> = withContext(Dispatchers.IO){
        uriList.forEach { uri ->
            try {
                uri.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }catch (e : Exception){
                Log.e(TAG, "deleteInternalMedia: ${e}", )
                Result.Error(e)
            }

        }
        Result.Success("Media Deleted Successfully")
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getMediaThumbnail(
        uri : Uri,
        size : Size
    ): Result<Bitmap> = withContext(Dispatchers.IO){
        try{
            val bitmap = context.contentResolver.loadThumbnail(
                uri,size,null
            )

            return@withContext Result.Success(bitmap)
        }catch (e : Exception){
            e.printStackTrace()
            return@withContext Result.Error(e)
        }

    }


}