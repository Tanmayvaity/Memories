package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Create Search Entity Table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS SearchEntity (
                memory_id TEXT NOT NULL,
                time_stamp INTEGER NOT NULL,
                PRIMARY KEY (memory_id),
                FOREIGN KEY (memory_id) REFERENCES MemoryEntity(memory_id) ON DELETE CASCADE
                );
        """.trimIndent())
    }
}

