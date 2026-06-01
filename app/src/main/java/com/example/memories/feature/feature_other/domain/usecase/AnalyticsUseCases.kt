package com.example.memories.feature.feature_other.domain.usecase

import com.example.memories.core.domain.model.DailyStat
import com.example.memories.core.domain.model.MediaBreakdown
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.presentation.tags.SortBy
import com.example.memories.feature.feature_other.domain.model.TagCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDailyStatsUseCase(private val repository: MemoryRepository) {
    operator fun invoke(): Flow<List<DailyStat>> = repository.getDailyStats()
}

class GetMediaBreakdownUseCase(private val repository: MemoryRepository) {
    operator fun invoke(): Flow<MediaBreakdown> = repository.getMediaBreakdown()
}

class GetTotalMemoryCountUseCase(private val repository: MemoryRepository) {
    operator fun invoke(): Flow<Int> = repository.getTotalMemoryCount()
}

class GetTopTagsUseCase(private val tagRepository: TagRepository) {
    operator fun invoke(limit: Int = 8): Flow<List<TagCount>> =
        tagRepository.getTagsWithMemoryCount(SortOrder.Descending, SortBy.Count, "")
            .map { list -> list.take(limit).map { TagCount(it.tagLabel, it.memoryCount) } }
}

data class AnalyticsUseCases(
    val getDailyStats: GetDailyStatsUseCase,
    val getMediaBreakdown: GetMediaBreakdownUseCase,
    val getTotalMemoryCount: GetTotalMemoryCountUseCase,
    val getTopTags: GetTopTagsUseCase,
)
