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

    @Upsert
    suspend fun insertMemory(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    suspend fun updateMemory(
        memory : MemoryEntity,
        mediaList : List<MediaEntity>,
        tags : List<TagEntity>
    ){
        insertMemory(memory)

//        val incomingMediaIds = mediaList.map { it.mediaId }
//        if(incomingMediaIds.isEmpty()){
//            deleteAllMediaForMemory(memory.memoryId)
//            return
//        }
//
//        val mediasToRemove = getMediaIdsToDelete(memory.memoryId,incomingMediaIds)
//        if(mediasToRemove.isNotEmpty()){
//            deleteMediaByIds(mediasToRemove)
//        }
//
//        insertAllMedia(mediaList)

        val incomingTagsIds = tags.map { it.tagId }
        if(incomingTagsIds.isEmpty()){
            deleteAllTagsForMemory(memory.memoryId)
            return
        }
        val tagsToRemove = getTagIdsToRemove(memory.memoryId,incomingTagsIds)
        if(tagsToRemove.isNotEmpty()){
            deleteCrossRefs(memory.memoryId,tagsToRemove)
        }
        incomingTagsIds.map { MemoryTagCrossRef(memory.memoryId,it) }.also { insertMemoryTagCrossRef(it) }


    }

    @Query("""
    DELETE FROM MediaEntity
    WHERE memory_id = :memoryId
""")
    suspend fun deleteAllMediaForMemory(memoryId: String)

    @Query("""
    DELETE FROM MediaEntity
    WHERE media_id IN (:mediaIds)
""")
    suspend fun deleteMediaByIds(mediaIds: List<String>)

    @Query("""
    SELECT media_id
    FROM MediaEntity
    WHERE memory_id = :memoryId
    AND media_id NOT IN (:incomingMediaIds)
""")
    suspend fun getMediaIdsToDelete(
        memoryId: String,
        incomingMediaIds: List<String>
    ): List<String>

    @Query("""
    SELECT tag_id
    FROM MemoryTagCrossRef
    WHERE memory_id = :memoryId
    AND tag_id NOT IN (:incomingTagIds)
""")
    suspend fun getTagIdsToRemove(
        memoryId: String,
        incomingTagIds: List<String>
    ): List<String>

    @Query("""
    DELETE FROM MemoryTagCrossRef
    WHERE memory_id = :memoryId
    AND tag_id IN (:tagIds)
""")
    suspend fun deleteCrossRefs(memoryId: String, tagIds: List<String>)

    @Query("""
    DELETE FROM MemoryTagCrossRef
    WHERE memory_id = :memoryId
""")
    suspend fun deleteAllTagsForMemory(memoryId: String)

    @Transaction
    @Query("SELECT * FROM MemoryEntity where hidden = 0 ORDER BY time_stamp DESC")
    fun getAllMemoriesWithMedia(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE memory_id IN (:memoryIds) and hidden =0")
    fun getAllMemoriesWithMediaByTag(memoryIds : List<String>) : Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity where favourite = 1 and hidden = 0 ORDER BY time_stamp DESC")
    fun getAllFavouriteMemoriesWithMedia(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity where hidden = 1 ORDER BY time_stamp DESC")
    fun getAllHiddenMemoriesWithMedia(): Flow<List<MemoryWithMedia>>


    @Transaction
    @Query("SELECT * FROM MemoryEntity where title LIKE '%' || :query || '%' order by time_stamp desc")
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