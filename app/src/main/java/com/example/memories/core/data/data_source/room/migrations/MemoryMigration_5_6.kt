package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("ALTER TABLE MediaEntity ADD COLUMN position INTEGER NOT NULL DEFAULT 0")

        db.execSQL(
            """
      UPDATE MediaEntity
      SET position = (
          SELECT COUNT(*)
          FROM MediaEntity AS m2
          WHERE m2.memory_id = MediaEntity.memory_id
          AND (m2.time_stamp < MediaEntity.time_stamp
               OR (m2.time_stamp = MediaEntity.time_stamp
                   AND m2.rowid < MediaEntity.rowid))
      )
  """.trimIndent()
        )


    }
}