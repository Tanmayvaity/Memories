package com.example.memories.feature.feature_other.domain.model

import com.example.memories.core.domain.model.MediaBreakdown
import java.time.LocalDate

enum class AnalyticsPeriod { WEEK, MONTH }

/** A bucketed memory count for the bar chart (e.g. "Mar", "W12"). */
data class PeriodCount(val label: String, val count: Int)

/** A single day's activity for the calendar heatmap. */
data class DayActivity(val date: LocalDate, val count: Int)

data class StreakInfo(val current: Int, val longest: Int)

data class TagCount(val label: String, val count: Int)

/** A bucketed word total for the word-count trend. */
data class WordPoint(val label: String, val words: Int)

data class AnalyticsData(
    val total: Int = 0,
    val streak: StreakInfo = StreakInfo(0, 0),
    val media: MediaBreakdown = MediaBreakdown(0, 0, 0),
    val topTags: List<TagCount> = emptyList(),
    val heatmap: List<DayActivity> = emptyList(),
    val perPeriod: List<PeriodCount> = emptyList(),
    val wordTrend: List<WordPoint> = emptyList(),
)
