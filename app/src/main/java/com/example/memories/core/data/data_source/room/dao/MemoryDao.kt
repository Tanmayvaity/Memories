package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MemoryDao {

    @Insert
    suspend fun insertMemory(memory: MemoryEntity)

    @Insert
    suspend fun insertAllMedia(mediaList: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMemoryTagCrossRef(memoryTagCrossRef: List<MemoryTagCrossRef>)

    @Transaction
    suspend fun insertMemoryWithMediaAndTag(memory: MemoryEntity, mediaList: List<MediaEntity>, tagList: List<TagEntity>) {
        insertMemory(memory)
        mediaList.map { it.copy(memoryId = memory.memoryId) }.also { insertAllMedia(it) }
        tagList.map { tag -> MemoryTagCrossRef(memory.memoryId,tag.tagId) }.also {insertMemoryTagCrossRef(it)}
    }

    @Transaction
    @Query("SELECT * FROM MemoryEntity where hidden = 0 ORDER BY time_stamp DESC")
    fun getAllMemoriesWithMedia(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity where favourite = 1 and hidden = 0 ORDER BY time_stamp DESC")
    fun getAllFavouriteMemoriesWithMedia(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity where hidden = 1 ORDER BY time_stamp DESC")
    fun getAllHiddenMemoriesWithMedia(): Flow<List<MemoryWithMedia>>


    @Transaction
    @Query("SELECT * FROM MemoryEntity where title LIKE '%' || :query || '%'")
    fun getAllMemoriesWithMediaByTitle(query : String): Flow<List<MemoryWithMedia>>


    @Transaction
    @Query("SELECT * FROM MemoryEntity where memory_id = :id")
    suspend fun getMemoryById(id : String): MemoryWithMedia?

    @Query("UPDATE memoryentity set hidden = :isHidden where memory_id = :id")
    suspend fun updateHidden(id:String,isHidden : Boolean)


    @Query("UPDATE memoryentity set favourite = :isFavourite where memory_id = :id")
    suspend fun updateFavourite(id:String,isFavourite : Boolean)


//    @Query("DELETE FROM memoryentity where memory_id = :id")
//    suspend fun deleteById(id : String)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity) : Int

}