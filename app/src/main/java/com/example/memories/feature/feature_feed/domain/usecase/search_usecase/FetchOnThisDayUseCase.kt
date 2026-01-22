package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.model.OnThisDayMemories
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class FetchOnThisDayUseCase(
    private val repository: MemoryRepository
){
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke() : List<OnThisDayMemories> {
//        Log.d("FetchOnThisDataUseCase", "invoke: inside")
        val today = LocalDate.now()
        val earliestTimestamp = repository.getEarliestMemoryTimeStamp() ?: return emptyList()

        val earliestYear = Instant.ofEpochMilli(earliestTimestamp)
            .atZone(ZoneId.systemDefault())
            .year
        val currentYear = today.year

        val buckets = mutableListOf<Pair<String, LocalDate>>()

        // Fixed intervals
        buckets.add("1 week ago" to today.minusWeeks(1))
        buckets.add("2 week ago" to today.minusWeeks(2))
        buckets.add("3 week ago" to today.minusWeeks(3))
        buckets.add("1 month ago" to today.minusMonths(1))
        buckets.add("2 month ago" to today.minusMonths(2))
        buckets.add("3 month ago" to today.minusMonths(3))
        buckets.add("6 months ago" to today.minusMonths(6))

        // Dynamic years
        for (yearsAgo in 1..(currentYear - earliestYear)) {
            val label = if (yearsAgo == 1) "1 year ago" else "$yearsAgo years ago"
            buckets.add(label to today.minusYears(yearsAgo.toLong()))
        }

        return buckets.mapNotNull { (label, targetDate) ->
            val startOfDay = targetDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val endOfDay = targetDate.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()

            val memories = repository.getMemoriesWithinRange(startOfDay, endOfDay)
//            Log.d("FetchOnThisDataUseCase", "invoke: inside ${memories}")
            Log.d("FetchOnThisDataUseCase", "Querying $label: $startOfDay to $endOfDay")

            if (memories.isNotEmpty()) {
                OnThisDayMemories(label, memories)
            } else null
        }

    }

}
