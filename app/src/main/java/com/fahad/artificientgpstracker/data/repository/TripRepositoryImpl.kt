package com.fahad.artificientgpstracker.data.repository

import com.fahad.artificientgpstracker.data.local.TripDao
import com.fahad.artificientgpstracker.data.local.LocationPointDao
import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.data.model.LocationPoint
import com.fahad.artificientgpstracker.domain.model.AppError
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import com.fahad.artificientgpstracker.util.SecurityUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.time.LocalDateTime
import javax.inject.Inject


class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val locationPointDao: LocationPointDao,
    private val securityUtil: SecurityUtil
) : TripRepository {
    
    override fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()
        .catch { exception ->
            throw AppError.databaseError("Failed to get all trips", exception)
        }
    
    override suspend fun getActiveTrip(): Trip? {
        return try {
            tripDao.getActiveTrip()
        } catch (e: Exception) {
            throw AppError.databaseError("Failed to get active trip", e)
        }
    }
    
    override suspend fun startNewTrip(): Long {
        return try {
            val trip = Trip(
                id = 0,
                startTime = LocalDateTime.now(),
                endTime = null,
                duration = 0L,
                distance = 0.0,
                isCompleted = false
            )
            tripDao.insertTrip(trip)
        } catch (e: Exception) {
            throw AppError.databaseError("Failed to start new trip", e)
        }
    }
    
    override suspend fun stopTrip(tripId: Long, finalDistance: Double, finalDuration: Long) {
        try {
            // Validate trip data before updating
            val validationResult = securityUtil.validateTripData(tripId, finalDistance, finalDuration)
            if (validationResult.isFailure) {
                throw validationResult.exceptionOrNull() ?: AppError.validationError("Invalid trip data")
            }
            
            val trip = tripDao.getTripById(tripId)
            trip?.let {
                val updatedTrip = it.copy(
                    endTime = LocalDateTime.now(),
                    duration = finalDuration,
                    distance = finalDistance,
                    isCompleted = true
                )
                tripDao.updateTrip(updatedTrip)
            } ?: throw AppError.databaseError("Trip not found")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.databaseError("Failed to stop trip", e)
        }
    }
    
    override suspend fun deleteTrip(trip: Trip) {
        try {
            tripDao.deleteTrip(trip)
            locationPointDao.deleteLocationPointsForTrip(trip.id)
        } catch (e: Exception) {
            throw AppError.databaseError("Failed to delete trip", e)
        }
    }
    
    override suspend fun addLocationPoint(
        tripId: Long,
        latitude: Double,
        longitude: Double,
        speed: Float,
        accuracy: Float
    ) {
        try {
            // Validate location data before saving
            val validationResult = securityUtil.validateLocationData(latitude, longitude, accuracy)
            if (validationResult.isFailure) {
                throw validationResult.exceptionOrNull() ?: AppError.validationError("Invalid location data")
            }
            
            val locationPoint = LocationPoint(
                id = 0,
                tripId = tripId,
                latitude = latitude,
                longitude = longitude,
                speed = speed,
                timestamp = LocalDateTime.now(),
                accuracy = accuracy
            )
            locationPointDao.insertLocationPoint(locationPoint)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.databaseError("Failed to add location point", e)
        }
    }
} 