package com.example.memories.feature.feature_memory.domain.usecase

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import coil3.toUri
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.repository.MemoryRepository
import java.lang.NullPointerException
import javax.inject.Inject
import kotlin.collections.map

class MemoryCreateUseCase @Inject constructor(
    val memoryRepository: MemoryRepository
) {
    companion object {
        private const val TAG = "MemoryCreateUseCase"
    }

    suspend operator fun invoke(
        uriList: List<UriType>,
        title: String,
        content: String,
        tagList : List<TagModel>
    ): Result<String> {
        Log.d(TAG, "invoke: MemoryCreateUseCase called")

        if (title.isEmpty() || title.isBlank()) {
            return Result.Error(IllegalArgumentException("title cannot be null"))
        }
        if (content.isBlank() || content.isEmpty()) {
            return Result.Error(IllegalArgumentException("content cannot be null"))
        }
        val permanentUriList =
            memoryRepository.saveToInternalStorage(uriList.map { it -> it.uri!!.toUri() })

        when (permanentUriList) {
            is Result.Success -> {
                if (permanentUriList.data == null) {
                    return Result.Error(NullPointerException("data is null"))
                }


                memoryRepository.insertMemoryWithMediaAndTag(
                    memory = MemoryModel(title = title, content = content),
                    mediaList = permanentUriList.data.map { it -> MediaModel(memoryId = "", uri = it.toString()) },
                    tagList = tagList
                )


//                val memoryModel = MemoryModel(title = title, content = content)
//                runCatching {
//                    memoryRepository.insertMemory(
//                        memoryModel
//                    )
//                }.onFailure { error ->
//                    Log.e(TAG, "insert Memory Failed : Failed to insert memory in MemoryEntity")
//                    return Result.Error(error)
//                }


//                val mediaModelList = permanentUriList.data?.map { it ->
//                    MediaModel(
//                        memoryId = memoryModel.memoryId,
//                        uri = it.toString()
//                    )
//                }
//                runCatching {
//                    memoryRepository.insertMedia(mediaModelList!!)
//                }.onFailure { error ->
//                    Log.e(TAG, "insert Media Failed : Failed to insert media in MediaEntity")
//                    return Result.Error(error)
//                }
            }

            is Result.Error -> {
                Log.e("MemoryCreateUseCase", "Failed to save to media to internal storage ${permanentUriList.error}")
                return Result.Error(permanentUriList.error)
            }
        }

        return Result.Success("Memory Created Successfully")

    }

}