package com.fahad.artificientgpstracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val distance: Double = 0.0, // in meters
    val duration: Long = 0, // in milliseconds
    val isCompleted: Boolean = false
) 