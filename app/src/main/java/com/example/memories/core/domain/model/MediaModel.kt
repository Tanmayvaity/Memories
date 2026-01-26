package com.example.memories.core.domain.model

import java.util.UUID

data class MediaModel(
    val mediaId: String = UUID.randomUUID().toString(),
    val memoryId: String,
    val uri: String,
    val hidden: Boolean = false,
    val favourite: Boolean = false,
    val timeStamp : Long = System.currentTimeMillis(),
    val position : Int = 0
)