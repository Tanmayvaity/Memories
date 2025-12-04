package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef

@Dao
interface MemoryTagCrossRefDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertMemoryTagCrossRef(memoryTagCrossRef: MemoryTagCrossRef)
}
