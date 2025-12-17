package com.example.memories.feature.feature_memory.domain.usecase

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import coil3.toUri
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.repository.MemoryRepository
import java.lang.NullPointerException
import javax.inject.Inject
import kotlin.collections.map

class MemoryUpdateUseCase @Inject constructor(
    val memoryRepository: MemoryRepository
) {
    companion object {
        private const val TAG = "MemoryUpdateUseCase"
    }

    suspend operator fun invoke(
        memory : MemoryWithMediaModel,
    ): Result<String> {
        Log.d(TAG, "invoke: MemoryUpdateUseCase called")
        val item = memory.memory

        if (item.title.isEmpty() || item.title.isBlank()) {
            return Result.Error(IllegalArgumentException("title cannot be null"))
        }
        if (item.content.isBlank() || item.content.isEmpty()) {
            return Result.Error(IllegalArgumentException("content cannot be null"))
        }

        runCatching {
            memoryRepository.updateMemory(
                memory = item,
                mediaList = memory.mediaList,
                tagList = memory.tagsList
            )

        }.onFailure { e ->
            Log.e(TAG, "invoke: ${e.message}")
            return Result.Error(e)
        }
        Log.d(TAG, "invoke: Memory Updated Successfully")
        return Result.Success("Memory Updated Successfully")

    }

}