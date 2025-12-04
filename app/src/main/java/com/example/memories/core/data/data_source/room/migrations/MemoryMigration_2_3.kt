package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Recreate TagEntity without memory_id
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS TagEntity_new (
                tag_id TEXT NOT NULL,
                label TEXT NOT NULL,
                PRIMARY KEY(tag_id)
            )
        """)

        // drop TagEntity and rename TagEntity_new to TagEntity
        db.execSQL("DROP TABLE TagEntity")
        db.execSQL("ALTER TABLE TagEntity_new RENAME TO TagEntity")

        // Create many-to-many crossref table between MemoryEntity and TagEntity
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS MemoryTagCrossRef (
                memory_id TEXT NOT NULL,
                tag_id TEXT NOT NULL,
                PRIMARY KEY(memory_id, tag_id),
                FOREIGN KEY(memory_id) REFERENCES MemoryEntity(memory_id) ON DELETE CASCADE,
                FOREIGN KEY(tag_id) REFERENCES TagEntity(tag_id) ON DELETE CASCADE
            )
        """)
    }
}

