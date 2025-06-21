package com.example.memories.view.utils

import android.content.Context
import android.content.pm.PackageManager
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
    context: Context
): File {
    val imageDirPath = File(context.cacheDir, "images").apply {
        if (!exists()) {
            mkdir()
        }
    }
    val tempImageFile = File.createTempFile("temp_", ".jpg", imageDirPath)
    return tempImageFile
}