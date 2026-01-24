package com.example.memories.feature.feature_feed.domain.usecase.history_usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class FetchTodayMemoriesUseCase (
    private val repository : MemoryRepository
){
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(date : LocalDate) : Flow<List<MemoryWithMediaModel>>{
        val start = date
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        val end = date
            .plusDays(1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        return repository.getMemoriesWithinRange(start,end)
    }
}