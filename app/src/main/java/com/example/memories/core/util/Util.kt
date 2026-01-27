package com.example.memories.core.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.memories.core.domain.model.Type
import java.io.File
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.text.startsWith

const val TAG = "CoreUtil"
fun isPermissionGranted(
    context : Context,
    permission: String
): Boolean {
    val isGranted = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
    Log.i("PermissionStatus", "isPermissionGranted: ${permission}:${isGranted}")
    return isGranted
}

fun Context.getVersionName() : String?{
    return  try {
        val pInfo: PackageInfo =
            this.packageManager.getPackageInfo(this.packageName, 0)
        pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }


}

fun createTempFile(
    context: Context,
    directory : String = "images",
    parent : File = context.cacheDir,
    prefix : String = "temp_",
): File? {
    return try {
        val imageDirPath = File(parent, directory).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val tempImageFile = File.createTempFile(prefix, ".jpg", imageDirPath)
        Log.d(TAG, "createTempFile: $tempImageFile")
        tempImageFile
    } catch (e: Exception) {
        Log.e(TAG, "Failed to create temp file", e)
        null
    }
}

fun createVideoFile(
    context: Context,
    directory : String = "videos",
    parent : File = context.cacheDir,
    prefix : String = "temp_",

): File {
    val imageDirPath = File(parent, directory).apply {
        if (!exists()) {
            mkdir()
        }
    }
    val tempImageFile = File.createTempFile(prefix, ".mp4", imageDirPath)
    return tempImageFile
}

fun createSettingsIntent(
    context : Context
){
    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", context.packageName, null)
    settingsIntent.data = uri
    context.startActivity(settingsIntent)
}

fun isSdkSmallerOrEqualToX(
    sdkVersion : Int
): Boolean{
    return android.os.Build.VERSION.SDK_INT <= sdkVersion
}

fun isVideoFile(path: String?): Boolean {
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}
fun isImageFile(path: String?): Boolean {
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("image")
}

//TODO : refactor mapToType and mapContentUriToType
fun Uri?.mapToType(): Type{
    if(this == null) {
        throw NullPointerException("Uri is null")
    }

    return if(isVideoFile(this.toString())){
        Type.VIDEO_MP4
    }else{
        Type.IMAGE_JPG
    }
}

fun Uri?.mapContentUriToType(context : Context): Type {
    if(this == null) {
        throw NullPointerException("Uri is null")
    }

    return if (context.contentResolver.getType(this)
            ?.startsWith("video") == true
    ) Type.VIDEO_MP4 else Type.IMAGE_JPG
}

fun Long.formatTime():String{
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return format.format(date)
}

fun Long.formatTime(format : String = "dd/MMM/yyyy"):String{
    val date = Date(this)
    val format = SimpleDateFormat(format, Locale.getDefault())
    return format.format(date)
}

fun formatTime(hour : Int, minute : Int, format : String = "hh:mm a"):String{
    val time = LocalTime.of(hour, minute)
    return time.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
}


fun getExoPlayer(
    context : Context,
    uri : String,
    playWhenReady : Boolean = false
) : ExoPlayer{
    return ExoPlayer.Builder(context).build().apply {
        val mediaItem = MediaItem.fromUri(uri.toUri())
        setMediaItem(mediaItem)
        this.playWhenReady = playWhenReady
        prepare()
    }
}


fun Context.hasPostNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true // Granted by default on API < 33
    }
}


fun Context.startChooser(uri : Uri?){
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    this.startActivity(Intent.createChooser(shareIntent, "Share Chooser"))
}

