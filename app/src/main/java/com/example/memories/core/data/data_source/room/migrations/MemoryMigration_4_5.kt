package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Create Search Entity Table
        db.execSQL("ALTER TABLE MemoryEntity ADD COLUMN memory_for_time_stamp INTEGER NOT NULL DEFAULT 0")

        db.execSQL("""
            UPDATE MemoryEntity
            SET memory_for_time_stamp = time_stamp
        """.trimIndent()
        )

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_MediaEntity_memory_id
            ON MediaEntity(memory_id)
        """)

    }
}