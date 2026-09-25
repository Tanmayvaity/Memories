package com.example.memories.core.domain.repository

interface MemoryMediaRepository {

    suspend fun updateOwner(ownerId: String)
}
