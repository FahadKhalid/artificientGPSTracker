package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.util.DateUtil
import javax.inject.Inject

class FormatTripDataUseCase @Inject constructor() {
    
    fun formatTripDate(trip: Trip): String {
        return DateUtil.formatDate(trip.startTime)
    }
    
    fun formatTripTime(trip: Trip): String {
        return DateUtil.formatTime(trip.startTime)
    }
    
    fun formatDuration(durationMs: Long): String {
        if (durationMs <= 0) return "0s"
        
        val hours = durationMs / (1000 * 60 * 60)
        val minutes = (durationMs % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (durationMs % (1000 * 60)) / 1000
        
        return when {
            hours > 0 -> String.format("%dh %dm", hours, minutes)
            minutes > 0 -> String.format("%dm %ds", minutes, seconds)
            else -> String.format("%ds", seconds)
        }
    }
    
    fun formatDistance(distanceMeters: Double): String {
        val distanceKm = distanceMeters / 1000
        return String.format("%.2f km", distanceKm)
    }
    
    fun formatSpeed(speedMps: Float): String {
        val speedKmh = speedMps * 3.6 // Convert m/s to km/h
        return String.format("%.1f km/h", speedKmh)
    }
} 