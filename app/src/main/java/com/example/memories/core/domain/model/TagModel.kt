package com.example.memories.core.domain.model

import java.util.UUID

data class TagModel(
    val tagId: String = UUID.randomUUID().toString(),
    val label: String,
)