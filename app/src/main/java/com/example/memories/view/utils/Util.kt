package com.example.memories.view.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.File

fun isPermissionGranted(
    context : Context,
    permission: String
): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
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