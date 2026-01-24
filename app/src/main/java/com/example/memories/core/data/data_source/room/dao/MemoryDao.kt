package com.example.memories.core.data.data_source.room.dao

import androidx.paging.PagingSource
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
import com.example.memories.core.domain.model.MemoryWithMediaModel
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
    suspend fun insertMemoryWithMediaAndTag(
        memory: MemoryEntity,
        mediaList: List<MediaEntity>,
        tagList: List<TagEntity>
    ) {
        insertMemory(memory)
        mediaList.map { it.copy(memoryId = memory.memoryId) }.also { insertAllMedia(it) }
        tagList.map { tag -> MemoryTagCrossRef(memory.memoryId, tag.tagId) }
            .also { insertMemoryTagCrossRef(it) }
    }

    @Transaction
    suspend fun updateMemory(
        memory: MemoryEntity,
        mediaList: List<MediaEntity>,
        tags: List<TagEntity>
    ) {
        insertMemory(memory)

        val incomingTagsIds = tags.map { it.tagId }
        if (incomingTagsIds.isEmpty()) {
            deleteAllTagsForMemory(memory.memoryId)
            return
        }
        val tagsToRemove = getTagIdsToRemove(memory.memoryId, incomingTagsIds)
        if (tagsToRemove.isNotEmpty()) {
            deleteCrossRefs(memory.memoryId, tagsToRemove)
        }
        incomingTagsIds.map { MemoryTagCrossRef(memory.memoryId, it) }
            .also { insertMemoryTagCrossRef(it) }
    }

    @Query("DELETE FROM MediaEntity WHERE memory_id = :memoryId")
    suspend fun deleteAllMediaForMemory(memoryId: String)

    @Query("DELETE FROM MediaEntity WHERE media_id IN (:mediaIds)")
    suspend fun deleteMediaByIds(mediaIds: List<String>)

    @Query("""
        SELECT media_id FROM MediaEntity
        WHERE memory_id = :memoryId AND media_id NOT IN (:incomingMediaIds)
    """)
    suspend fun getMediaIdsToDelete(memoryId: String, incomingMediaIds: List<String>): List<String>

    @Query("""
        SELECT tag_id FROM MemoryTagCrossRef
        WHERE memory_id = :memoryId AND tag_id NOT IN (:incomingTagIds)
    """)
    suspend fun getTagIdsToRemove(memoryId: String, incomingTagIds: List<String>): List<String>

    @Query("DELETE FROM MemoryTagCrossRef WHERE memory_id = :memoryId AND tag_id IN (:tagIds)")
    suspend fun deleteCrossRefs(memoryId: String, tagIds: List<String>)

    @Query("DELETE FROM MemoryTagCrossRef WHERE memory_id = :memoryId")
    suspend fun deleteAllTagsForMemory(memoryId: String)

    // ==================== PAGED QUERIES - ALL MEMORIES ====================

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY time_stamp DESC limit :limit")
    fun getRecentMemories(limit : Int) : Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY time_stamp DESC")
    fun getAllMemoriesWithMedia(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY time_stamp ASC")
    fun getAllMemoriesWithMediaAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY memory_for_time_stamp DESC")
    fun getAllMemoriesWithMediaByMemoryForTimeStamp(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY memory_for_time_stamp ASC")
    fun getAllMemoriesWithMediaByMemoryForTimeStampAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY title COLLATE NOCASE DESC")
    fun getAllMemoriesWithMediaByTitle(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 0 ORDER BY title COLLATE NOCASE ASC")
    fun getAllMemoriesWithMediaByTitleAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE memory_id IN (:memoryIds) AND hidden = 0")
    fun getAllMemoriesWithMediaByTag(memoryIds: List<String>): PagingSource<Int, MemoryWithMedia>

    // ==================== PAGED QUERIES - FAVOURITES ====================

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY time_stamp DESC")
    fun getAllFavouriteMemoriesWithMedia(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY time_stamp ASC")
    fun getAllFavouriteMemoriesWithMediaAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY memory_for_time_stamp DESC")
    fun getAllFavouriteMemoriesWithMediaByMemoryForTimeStamp(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY memory_for_time_stamp ASC")
    fun getAllFavouriteMemoriesWithMediaByMemoryForTimeStampAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY title COLLATE NOCASE DESC")
    fun getAllFavouriteMemoriesWithMediaByTitle(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE favourite = 1 AND hidden = 0 ORDER BY title COLLATE NOCASE ASC")
    fun getAllFavouriteMemoriesWithMediaByTitleAscending(): PagingSource<Int, MemoryWithMedia>

    // ==================== PAGED QUERIES - HIDDEN ====================

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY time_stamp DESC")
    fun getAllHiddenMemoriesWithMedia(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY time_stamp ASC")
    fun getAllHiddenMemoriesWithMediaAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY memory_for_time_stamp DESC")
    fun getAllHiddenMemoriesWithMediaByMemoryForTimeStamp(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY memory_for_time_stamp ASC")
    fun getAllHiddenMemoriesWithMediaByMemoryForTimeStampAscending(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY title COLLATE NOCASE DESC")
    fun getAllHiddenMemoriesWithMediaByTitle(): PagingSource<Int, MemoryWithMedia>

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE hidden = 1 ORDER BY title COLLATE NOCASE ASC")
    fun getAllHiddenMemoriesWithMediaByTitleAscending(): PagingSource<Int, MemoryWithMedia>

    // ==================== PAGED QUERY - SEARCH ====================

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE title LIKE '%' || :query || '%' ORDER BY time_stamp DESC")
    fun getAllMemoriesWithMediaBySearch(query: String): Flow<List<MemoryWithMedia>>

    // ==================== NON-PAGED QUERIES ====================

    @Transaction
    @Query("SELECT * FROM MemoryEntity WHERE memory_id = :id")
    suspend fun getMemoryById(id: String): MemoryWithMedia?

    @Query("UPDATE MemoryEntity SET hidden = :isHidden WHERE memory_id = :id")
    suspend fun updateHidden(id: String, isHidden: Boolean)

    @Query("UPDATE MemoryEntity SET favourite = :isFavourite WHERE memory_id = :id")
    suspend fun updateFavourite(id: String, isFavourite: Boolean)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity): Int

    @Query("SELECT MIN(memory_for_time_stamp) FROM MemoryEntity WHERE hidden = 0")
    suspend fun getEarliestMemoryTimestamp(): Long?

    @Transaction
    @Query("""
        SELECT * FROM MemoryEntity 
        WHERE hidden = 0 
        AND memory_for_time_stamp >= :startTimestamp 
        AND memory_for_time_stamp < :endTimestamp
        ORDER BY memory_for_time_stamp DESC
    """)
    fun getMemoriesBetweenTimestamps(startTimestamp: Long, endTimestamp: Long): Flow<List<MemoryWithMedia>>
}