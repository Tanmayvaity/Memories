package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import kotlinx.coroutines.flow.Flow


@Dao
interface MemoryDao {

    @Insert
    suspend fun insertMemory(memory: MemoryEntity)

    @Transaction
    @Query("SELECT * FROM MemoryEntity where hidden = 0")
    fun getAllMemoriesWithMedia(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity where memory_id = :id")
    suspend fun getMemoryById(id : String): MemoryWithMedia?

    @Query("UPDATE memoryentity set hidden = :isHidden where memory_id = :id")
    suspend fun updateHidden(id:String,isHidden : Boolean)


    @Query("UPDATE memoryentity set favourite = :isFavourite where memory_id = :id")
    suspend fun updateFavourite(id:String,isFavourite : Boolean)






}