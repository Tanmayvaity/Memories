package com.example.memories.feature.feature_memory.domain.usecase

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import coil3.toUri
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import java.lang.NullPointerException
import javax.inject.Inject
import kotlin.collections.map

class MemoryCreateUseCase @Inject constructor(
    val memoryRepository: MemoryRepository
) {
    companion object{
        private const val TAG = "MemoryCreateUseCase"
    }
     suspend operator fun invoke(
         uriList : List<UriType>,
         title : String,
         content : String
     ): Result<String>{
         Log.d(TAG, "invoke: MemoryCreateUseCase called")

         if(title.isEmpty() || title.isBlank()){
             return Result.Error(IllegalArgumentException("title cannot be null"))
         }
         if(content.isBlank() || content.isEmpty()){
             return Result.Error(IllegalArgumentException("content cannot be null"))
         }


         val permanentUriList = memoryRepository.saveToInternalStorage(uriList.map { it -> it.uri!!.toUri() })
         if(permanentUriList is Result.Success){
             val memoryModel = MemoryModel(title = title,content = content)
             memoryRepository.insertMemory(
                 memoryModel
             )

             if(permanentUriList.data == null){
                 return Result.Error(NullPointerException("data is null"))
             }
             val mediaModelList = permanentUriList.data?.map { it -> MediaModel(
                 memoryId = memoryModel.memoryId,
                 uri = it.toString()
             ) }


             memoryRepository.insertMedia(mediaModelList!!)
         }

         if(permanentUriList is Result.Error){
             Log.d("MemoryCreateUseCase", "invoke: ${permanentUriList.error}")
             return Result.Error(permanentUriList.error)
         }

         return Result.Success("Memory Created Successfully")

    }

}