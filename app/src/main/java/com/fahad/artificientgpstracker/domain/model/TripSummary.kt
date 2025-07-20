package com.fahad.artificientgpstracker.domain.model

import java.time.LocalDateTime

data class TripSummary(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val duration: Long,
    val distance: Double,
    val isCompleted: Boolean,
    val averageSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val locationPointsCount: Int = 0
) 