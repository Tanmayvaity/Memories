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