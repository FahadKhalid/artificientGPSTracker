package com.fahad.artificientgpstracker.domain.model

import java.time.LocalDateTime

data class Trip(
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val duration: Long = 0L,
    val distance: Double = 0.0,
    val isCompleted: Boolean = false
) 