package com.example.memories.core.domain.model

import java.util.UUID

data class MemoryModel(
    val memoryId : String = UUID.randomUUID().toString(),
    val title : String,
    val content : String,
    val hidden : Boolean = false,
    val favourite : Boolean = false,
    val timeStamp : Long = System.currentTimeMillis()
)


data class MemoryWithMediaModel(
    val memory : MemoryModel = MemoryModel(title = "Just A Title", content = "Content about a title"),
    val mediaList : List<MediaModel> = emptyList(),
    val tagsList : List<TagModel> = emptyList()
)

data class MemoryTagCrossRefModel(
    val memoryId : String,
    val tagId : String
)