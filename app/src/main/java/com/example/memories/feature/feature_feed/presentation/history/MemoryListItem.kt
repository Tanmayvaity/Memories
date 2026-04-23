package com.example.memories.feature.feature_feed.presentation.history

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.core.domain.model.MemoryWithMediaModel
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

sealed interface MemoryListItem {
    data class Entry(val memory: MemoryWithMediaModel) : MemoryListItem
    data class MonthHeader(val yearMonth: YearMonth,val anchorId : String) : MemoryListItem
}

@RequiresApi(Build.VERSION_CODES.O)
fun MemoryListItem.Entry.yearMonth(
    zone: ZoneId = ZoneId.systemDefault()
): YearMonth =
    YearMonth.from(Instant.ofEpochMilli(this.memory.memory.memoryForTimeStamp!!).atZone(zone))