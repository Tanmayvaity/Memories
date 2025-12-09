package com.example.memories.core.domain.usecase

import android.R.attr.tag
import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    val tagRepository: TagRepository
) {
    suspend operator fun invoke(label : String) : Result<TagModel>{
        try {
            val tag = TagModel(label = label)
            tagRepository.insertTag(tag)
            Log.d(TAG, "Tag Added Successfully : $tag")
            return Result.Success(tag)
        }catch (e : Exception){
            Log.e(TAG, "error while adding tag ${tag} error : ${e.message}", )
            return Result.Error(e)
        }
    }

    companion object {
        private const val TAG = "AddTagUseCase"

    }

}