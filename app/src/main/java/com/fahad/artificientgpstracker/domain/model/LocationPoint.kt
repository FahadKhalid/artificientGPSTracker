package com.fahad.artificientgpstracker.domain.model

import java.time.LocalDateTime

data class LocationPoint(
    val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val accuracy: Float,
    val timestamp: LocalDateTime
) 