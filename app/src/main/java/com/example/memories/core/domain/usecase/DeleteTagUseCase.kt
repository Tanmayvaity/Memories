package com.example.memories.core.domain.usecase

import android.R.attr.tag
import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    val tagRepository: TagRepository
) {
    suspend operator fun invoke(id : String) : Result<String> {
        try {
            tagRepository.deleteTag(id)
        }catch (e : Exception){
            e.printStackTrace()
            Log.e(TAG, "invoke: error while deleting tag $e", )
            return Result.Error(e)
        }
        Log.i(TAG, "invoke: Tag Deleted successfully")
        return Result.Success("Tag Deleted Successfully")

    }

    companion object {
        private const val TAG = "DeleteTagUseCase"

    }

}