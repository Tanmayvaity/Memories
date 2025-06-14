package com.example.memories.model.media

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.example.memories.model.models.BitmapResult
import com.example.memories.model.models.CaptureResult
import com.example.memories.model.models.MediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MediaManager {

    suspend fun downloadImage(
        appContext: Context,
        uri: String
    ): MediaResult = withContext(Dispatchers.IO) {
        val resolver = appContext.contentResolver

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME,"image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING,1)
        }

        val sharedImageUri = resolver.insert(imageCollection,imageDetails)?: return@withContext MediaResult.Error(
            NullPointerException("Destination uri is null"))

        try{
            val imageUri = uri.toUri()
            resolver.openInputStream(imageUri)?.use{ input ->
                resolver.openOutputStream(sharedImageUri)?.use{ output ->
                    input.copyTo(output)
                }
            }

            imageDetails.put(MediaStore.Images.Media.IS_PENDING,0)
            resolver.update(sharedImageUri,imageDetails,null,null)

        }catch (e : Exception){
            e.printStackTrace()
            resolver.delete(sharedImageUri,null,null)
            return@withContext MediaResult.Error(e)
        }

        Log.d("MediaManager", "sharedImageUri:${sharedImageUri} ")

        return@withContext MediaResult.Success("Image Saved Successfully")

    }



    suspend fun downloadImageWithBitmap(
        appContext: Context,
        bitmap:Bitmap
    ): MediaResult = withContext(Dispatchers.IO) {
        val resolver = appContext.contentResolver

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME,"image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING,1)
        }

        val sharedImageUri = resolver.insert(imageCollection,imageDetails)?: return@withContext MediaResult.Error(
            NullPointerException("Destination uri is null"))



        try{
            resolver.openOutputStream(sharedImageUri)?.use{ output ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    output
                )
            }

            imageDetails.put(MediaStore.Images.Media.IS_PENDING,0)
            resolver.update(sharedImageUri,imageDetails,null,null)

        }catch (e : Exception){
            e.printStackTrace()
            resolver.delete(sharedImageUri,null,null)
            return@withContext MediaResult.Error(e)
        }

        Log.d("MediaManager", "sharedImageUri:${sharedImageUri} ")

        return@withContext MediaResult.Success("Image Saved Succesfully")

    }



    suspend fun copyFromSharedStorage(
        context : Context,
        sharedUri : Uri,
        file : File
    ): CaptureResult = withContext(Dispatchers.IO){
        val resolver = context.contentResolver
        try{
            Log.d("MediaManager", "uri : ${file.toUri()} ")
            Log.d("MediaManager", "uri : ${sharedUri} ")

            resolver.openInputStream(sharedUri)?.use{input->
                resolver.openOutputStream(file.toUri())?.use{output ->
                    input.copyTo(output)
                }
            }
        }catch(e:Exception){
            e.printStackTrace()
            Log.e("MediaManager", "copyFromSharedStorage : ${e.message}")
            return@withContext CaptureResult.Error(e)
        }


        return@withContext CaptureResult.Success(file.toUri())
    }

    suspend fun uriToBitmap(uri:Uri, context : Context): BitmapResult = withContext(Dispatchers.IO){
        try{
            val bitmap =  if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            return@withContext BitmapResult.Success(bitmap)
        }catch(e : Exception){
            e.printStackTrace()
            return@withContext BitmapResult.Error(e)
        }
    }

    // cache or internal/external storage
    suspend fun saveBitmapToInternalStorage(
        bitmap:Bitmap,
        file : File
    ): CaptureResult = withContext(Dispatchers.IO){
        try{
            FileOutputStream(file)?.use { output->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    output
                )
            }


            val uri = Uri.fromFile(file)
            Log.i("MediaManager", "internal bitmap uri : ${uri.toString()}")
            return@withContext CaptureResult.Success(uri)
        }catch(e :Exception){
            e.printStackTrace()
            return@withContext CaptureResult.Error(e)
        }
    }

}