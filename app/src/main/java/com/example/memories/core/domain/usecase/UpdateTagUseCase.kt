package com.example.memories.core.domain.usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
    val tagRepository: TagRepository
) {
    suspend operator fun invoke(id: String, label: String): Result<TagModel> {
        return try {
            val trimmed = label.trim()
            if (trimmed.isEmpty()) {
                return Result.Error(IllegalArgumentException("Tag name cannot be empty"))
            }
            val existing = tagRepository.getTagByLabel(trimmed)
            if (existing != null && existing.tagId != id) {
                Log.d(TAG, "Tag label already taken by: $existing")
                return Result.Error(IllegalArgumentException("A tag named \"$trimmed\" already exists"))
            }
            tagRepository.updateTag(id, trimmed)
            val tag = TagModel(tagId = id, label = trimmed)
            Log.d(TAG, "Tag Updated Successfully : $tag")
            Result.Success(tag)
        } catch (e: Exception) {
            Log.e(TAG, "error while updating tag $id error : ${e.message}")
            Result.Error(e)
        }
    }

    companion object {
        private const val TAG = "UpdateTagUseCase"

    }

}