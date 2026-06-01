package com.example.memories.feature.feature_other.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.core.domain.model.DailyStat
import com.example.memories.core.domain.model.MediaBreakdown
import com.example.memories.feature.feature_other.domain.model.AnalyticsData
import com.example.memories.feature.feature_other.domain.model.AnalyticsPeriod
import com.example.memories.feature.feature_other.domain.model.DayActivity
import com.example.memories.feature.feature_other.domain.model.PeriodCount
import com.example.memories.feature.feature_other.domain.model.StreakInfo
import com.example.memories.feature.feature_other.domain.model.TagCount
import com.example.memories.feature.feature_other.domain.model.WordPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import java.util.Locale

/**
 * Pure transformation of the small SQL-aggregated inputs into the shapes the dashboard renders.
 * No I/O — fully testable. Gated on API 26 for java.time (the app has no core-library desugaring).
 */
@RequiresApi(Build.VERSION_CODES.O)
object AnalyticsCalculator {

    private const val HEATMAP_WEEKS = 26
    private const val WEEK_BUCKETS = 8
    private const val MONTH_BUCKETS = 12

    fun compute(
        dailyStats: List<DailyStat>,
        media: MediaBreakdown,
        topTags: List<TagCount>,
        total: Int,
        period: AnalyticsPeriod,
        today: LocalDate = LocalDate.now(),
    ): AnalyticsData {
        val byDay: Map<LocalDate, DailyStat> = dailyStats
            .mapNotNull { stat -> parseDay(stat.day)?.let { it to stat } }
            .toMap()

        return AnalyticsData(
            total = total,
            streak = computeStreak(byDay.keys, today),
            media = media,
            topTags = topTags,
            heatmap = buildHeatmap(byDay, today),
            perPeriod = buildPeriodCounts(byDay.values.toList() to byDay, period, today),
            wordTrend = buildWordTrend(byDay, period, today),
        )
    }

    private fun parseDay(day: String): LocalDate? =
        runCatching { LocalDate.parse(day) }.getOrNull()

    // ── Streak ──────────────────────────────────────────────────────────────
    private fun computeStreak(activeDays: Set<LocalDate>, today: LocalDate): StreakInfo {
        if (activeDays.isEmpty()) return StreakInfo(0, 0)

        // Current streak: walk back from today while each day is active.
        var current = 0
        var cursor = today
        while (cursor in activeDays) {
            current++
            cursor = cursor.minusDays(1)
        }

        // Longest streak across the full history.
        val sorted = activeDays.sorted()
        var longest = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i] == sorted[i - 1].plusDays(1)) run + 1 else 1
            if (run > longest) longest = run
        }
        return StreakInfo(current = current, longest = longest)
    }

    // ── Heatmap (last N weeks, including empty days) ─────────────────────────
    private fun buildHeatmap(
        byDay: Map<LocalDate, DailyStat>,
        today: LocalDate,
    ): List<DayActivity> {
        // Monday of the current week, then back N-1 weeks → aligned grid start.
        val mondayThisWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val start = mondayThisWeek.minusWeeks((HEATMAP_WEEKS - 1).toLong())
        val days = mutableListOf<DayActivity>()
        var d = start
        while (!d.isAfter(today)) {
            days.add(DayActivity(d, byDay[d]?.count ?: 0))
            d = d.plusDays(1)
        }
        return days
    }

    // ── Period buckets (memories per week/month) ─────────────────────────────
    private fun buildPeriodCounts(
        statsAndMap: Pair<List<DailyStat>, Map<LocalDate, DailyStat>>,
        period: AnalyticsPeriod,
        today: LocalDate,
    ): List<PeriodCount> {
        val byDay = statsAndMap.second
        val grouped = LinkedHashMap<String, Int>()
        val keys = recentBucketKeys(period, today)
        keys.forEach { (key, _) -> grouped[key] = 0 }
        byDay.forEach { (date, stat) ->
            val key = bucketKey(date, period)
            if (grouped.containsKey(key)) grouped[key] = (grouped[key] ?: 0) + stat.count
        }
        return keys.map { (key, label) -> PeriodCount(label, grouped[key] ?: 0) }
    }

    private fun buildWordTrend(
        byDay: Map<LocalDate, DailyStat>,
        period: AnalyticsPeriod,
        today: LocalDate,
    ): List<WordPoint> {
        val grouped = LinkedHashMap<String, Int>()
        val keys = recentBucketKeys(period, today)
        keys.forEach { (key, _) -> grouped[key] = 0 }
        byDay.forEach { (date, stat) ->
            val key = bucketKey(date, period)
            if (grouped.containsKey(key)) grouped[key] = (grouped[key] ?: 0) + stat.words
        }
        return keys.map { (key, label) -> WordPoint(label, grouped[key] ?: 0) }
    }

    /** The most recent [MAX_PERIOD_BUCKETS] bucket keys (oldest→newest) with display labels. */
    private fun recentBucketKeys(period: AnalyticsPeriod, today: LocalDate): List<Pair<String, String>> {
        return when (period) {
            AnalyticsPeriod.MONTH -> {
                val labelFormat = DateTimeFormatter.ofPattern("MMM yy", Locale.getDefault())
                val start = YearMonth.from(today).minusMonths((MONTH_BUCKETS - 1).toLong())
                (0 until MONTH_BUCKETS).map { i ->
                    val ym = start.plusMonths(i.toLong())
                    // e.g. "Mar 26"
                    monthKey(ym) to ym.atDay(1).format(labelFormat)
                }
            }

            AnalyticsPeriod.WEEK -> {
                val labelFormat = DateTimeFormatter.ofPattern("d MMM yy", Locale.getDefault())
                (0 until WEEK_BUCKETS).map { i ->
                    val date = today.minusWeeks((WEEK_BUCKETS - 1 - i).toLong())
                    // label each week by its Monday, e.g. "4 Mar 26"
                    val weekStart = date.minusDays((date.dayOfWeek.value - 1).toLong())
                    weekKey(date) to weekStart.format(labelFormat)
                }
            }
        }
    }

    private fun bucketKey(date: LocalDate, period: AnalyticsPeriod): String =
        when (period) {
            AnalyticsPeriod.MONTH -> monthKey(YearMonth.from(date))
            AnalyticsPeriod.WEEK -> weekKey(date)
        }

    private fun monthKey(ym: YearMonth): String = "%04d-%02d".format(ym.year, ym.monthValue)

    private fun weekKey(date: LocalDate): String {
        val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val weekYear = date.get(IsoFields.WEEK_BASED_YEAR)
        return "%04d-W%02d".format(weekYear, week)
    }
}
