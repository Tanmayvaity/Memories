package com.example.memories.feature.feature_memory.domain.repository

import android.net.Uri
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType

interface MemoryRepository {

    suspend fun saveToInternalStorage(uriList : List<Uri>): Result<List<Uri>>

    suspend fun insertMemory(memory : MemoryModel)

    suspend fun insertMedia(mediaList : List<MediaModel>)

}