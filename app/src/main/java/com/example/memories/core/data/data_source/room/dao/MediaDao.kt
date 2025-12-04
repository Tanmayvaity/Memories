package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.MediaEntity

@Dao
interface MediaDao {

    @Query("SELECT * FROM MediaEntity")
    suspend fun getAllMedia(): List<MediaEntity>

    @Query("SELECT * FROM MediaEntity WHERE favourite = 1")
    suspend fun getAllFavouriteMedia(): List<MediaEntity>

    @Query("SELECT * FROM MediaEntity WHERE hidden = 1")
    suspend fun getAllHiddenMedia(): List<MediaEntity>

//    @Insert
//    suspend fun insertAllMedia(mediaList: List<MediaEntity>)

}