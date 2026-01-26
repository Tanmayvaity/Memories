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
import com.example.memories.feature.feature_memory.domain.model.MediaSlot
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
        orderedMediaSlots : List<MediaSlot> = emptyList()
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

            val finalMediaList = mutableListOf<MediaModel>()
            orderedMediaSlots.forEachIndexed { index, mediaSlot ->
                when(mediaSlot){
                    is MediaSlot.Existing -> {
                        finalMediaList.add(mediaSlot.mediaModel.copy(position = index))
                    }

                    is MediaSlot.New -> {
                        val savedUri = memoryRepository.saveToInternalStorage(listOf(mediaSlot.uriType.uri!!.toUri()))
                        when(savedUri){
                            is Result.Error -> {
                                return@runCatching Result.Error(savedUri.error)
                            }

                            is Result.Success -> {
                                savedUri.data?.firstOrNull()?.let { uri ->
                                    finalMediaList.add(
                                        MediaModel(
                                            memoryId = item.memoryId,
                                            uri = uri.toString(),
                                            position = index
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val keptIds = finalMediaList.map { it.mediaId }
            val urisToDelete = if(keptIds.isNotEmpty()){
                val uris = memoryRepository.getMediaUrisToDelete(
                    item.memoryId, keptIds
                )
                Log.d(TAG, "invoke: uri to delete ${uris}")
                uris
            }else{
                val allUris = memory.mediaList.map { it.uri }
                Log.d(TAG, "invoke: all uri to delete ${allUris}")
                allUris
            }
            
            
            memoryRepository.updateMemory(
                memory = item,
                mediaList = finalMediaList,
                tagList = memory.tagsList
            )

            // delete media not part of the memory
            if(urisToDelete.isNotEmpty()){
                memoryRepository.deleteInternalMedia(urisToDelete.map { it.toUri() })
                Log.d(TAG, "invoke: uris To Delete ${urisToDelete}")
            }


        }.onFailure { e ->
            Log.e(TAG, "invoke: ${e.message}")
            return Result.Error(e)
        }.onSuccess {
            Log.d(TAG, "invoke: Memory Updated Successfully")
            return Result.Success("Memory Updated Successfully")
        }

       return Result.Error(Exception("Creation Failed"))
    }

}