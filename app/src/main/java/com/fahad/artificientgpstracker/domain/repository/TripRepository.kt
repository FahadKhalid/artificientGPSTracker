package com.fahad.artificientgpstracker.domain.repository

import com.fahad.artificientgpstracker.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    suspend fun getActiveTrip(): Trip?
    suspend fun startNewTrip(): Long
    suspend fun stopTrip(tripId: Long, finalDistance: Double, finalDuration: Long)
    suspend fun deleteTrip(trip: Trip)
    suspend fun addLocationPoint(tripId: Long, latitude: Double, longitude: Double, speed: Float, accuracy: Float)
} 