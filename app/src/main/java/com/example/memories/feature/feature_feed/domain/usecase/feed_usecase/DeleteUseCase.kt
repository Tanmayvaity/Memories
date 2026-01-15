package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import android.R.attr.type
import android.util.Log
import androidx.core.net.toUri
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class DeleteUseCase(
    private val repository: MemoryRepository,
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        memory: MemoryModel,
        uriList: List<String>
    ): Result<String> {
        try {

            val result = repository.delete(memory)
            if (result == 1) {
                return mediaRepository.deleteMedia(uriList.map { it -> it.toUri() })
            }
        }catch (e : Exception){
            Log.e("DeleteUseCase", "invoke: error : ${e}", )
        }

        return Result.Error(Throwable("Memory Not Deleted"))
    }
}