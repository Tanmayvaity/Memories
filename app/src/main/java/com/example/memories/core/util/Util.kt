package com.example.memories.core.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File

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


fun createTempFile(
    context: Context,
    directory : String = "images"
): File {
    val imageDirPath = File(context.cacheDir, directory).apply {
        if (!exists()) {
            mkdir()
        }
    }
    val tempImageFile = File.createTempFile("temp_", ".jpg", imageDirPath)
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