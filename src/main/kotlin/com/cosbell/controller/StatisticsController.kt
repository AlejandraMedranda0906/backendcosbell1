package com.cosbell.controller

import com.cosbell.dto.StatisticsDto
import com.cosbell.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/admin")
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping("/stats")
    fun getStats(
        @RequestParam("period", defaultValue = "day") period: String,
        @RequestParam("date", required = false) date: String?
    ): ResponseEntity<StatisticsDto> {
        val baseDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        return when (period) {
            "week" -> ResponseEntity.ok(statisticsService.getWeeklyStatistics(baseDate))
            "month" -> ResponseEntity.ok(statisticsService.getMonthlyStatistics(baseDate))
            else -> ResponseEntity.ok(statisticsService.getDailyStatistics(baseDate))
        }
    }
    @GetMapping("/stats/all")
    fun getAllStats(): ResponseEntity<StatisticsDto> {
        return ResponseEntity.ok(statisticsService.getAllTimeStatistics())
    }

}
