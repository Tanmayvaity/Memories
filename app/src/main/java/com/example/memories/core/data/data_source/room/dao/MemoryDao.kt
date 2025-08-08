package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia


@Dao
interface MemoryDao {

    @Insert
    suspend fun insertMemory(memory: MemoryEntity)

    @Transaction
    @Query("SELECT * FROM MemoryEntity")
    suspend fun getAllMemoriesWithMedia(): List<MemoryWithMedia>




}