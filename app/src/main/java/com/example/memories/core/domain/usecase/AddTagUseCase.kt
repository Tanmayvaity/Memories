package com.example.memories.core.domain.usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    val tagRepository: TagRepository
) {
    suspend operator fun invoke(label : String) : Result<TagModel>{
        return try {
            val trimmed = label.trim()
            val existing = tagRepository.getTagByLabel(trimmed)
            if (existing != null) {
                Log.d(TAG, "Tag already exists, reusing: $existing")
                return Result.Success(existing)
            }
            val tag = TagModel(label = trimmed)
            tagRepository.insertTag(tag)
            Log.d(TAG, "Tag Added Successfully : $tag")
            Result.Success(tag)
        } catch (e : Exception){
            Log.e(TAG, "error while adding tag $label error : ${e.message}")
            Result.Error(e)
        }
    }

    companion object {
        private const val TAG = "AddTagUseCase"

    }

}