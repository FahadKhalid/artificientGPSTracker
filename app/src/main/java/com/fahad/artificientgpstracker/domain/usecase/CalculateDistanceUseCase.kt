package com.fahad.artificientgpstracker.domain.usecase

import android.location.Location
import javax.inject.Inject

class CalculateDistanceUseCase @Inject constructor() {
    
    operator fun invoke(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
    
    fun calculateDistance(location1: Location, location2: Location): Double {
        return invoke(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude
        )
    }
} 