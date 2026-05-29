package com.example.memories.feature.feature_other.data.repository.data_source

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import com.example.memories.feature.feature_other.domain.model.StorageStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class SystemManager(
    private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStorageStats(): StorageStats {
        return withContext(Dispatchers.IO) {
            val storageStatsManager =
                context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageManager =
                context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

            val uuid = storageManager.getUuidForPath(context.dataDir)
            val stats = storageStatsManager.queryStatsForPackage(
                uuid,
                context.packageName,
                Process.myUserHandle()
            )
            StorageStats(
                appBytes = stats.appBytes,
                dataBytes = stats.dataBytes,
                cacheBytes = stats.cacheBytes,
                total = stats.appBytes + stats.dataBytes
            )
        }
    }

    suspend fun deleteCacheData() : Result<Long>{
        return withContext(Dispatchers.IO){
            runCatching {
                val baseDir = context.cacheDir
                    ?: throw IllegalStateException("Cache dir not available")
                val dirs = listOf(
                    File(baseDir, "images"),
                    File(baseDir, "videos")
                )
                var freed = 0L
                dirs.forEach { dir ->
                    if (dir.exists()) {
                        freed += dir.dirSize()
                        dir.deleteRecursively()
                    }
                }
                freed
            }.fold(
                onSuccess = {bytes ->
                    Result.success(bytes)
                },
                onFailure = { e ->
                    Result.failure(e)
                }
            )
        }

    }

    private fun File.dirSize(): Long =
        listFiles()?.sumOf { if (it.isFile) it.length() else it.dirSize() } ?: 0L

    private fun File.deleteContents() {
        listFiles()?.forEach { child ->
            if (child.isDirectory) child.deleteContents()
            child.delete()
        }
    }
}