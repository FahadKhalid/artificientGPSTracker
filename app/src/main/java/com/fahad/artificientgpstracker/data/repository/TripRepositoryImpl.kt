package com.fahad.artificientgpstracker.data.repository

import com.fahad.artificientgpstracker.data.local.TripDao
import com.fahad.artificientgpstracker.data.local.LocationPointDao
import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.data.model.LocationPoint
import com.fahad.artificientgpstracker.domain.model.AppError
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import com.fahad.artificientgpstracker.util.SecurityUtil
import com.fahad.artificientgpstracker.util.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    
    override suspend fun updateTripDistance(tripId: Long, distance: Double) {
        try {
            val trip = tripDao.getTripById(tripId)
            trip?.let {
                val validationResult = securityUtil.validateTripData(tripId, distance, it.duration)
                if (validationResult.isFailure) {
                    throw validationResult.exceptionOrNull() ?: AppError.validationError("Invalid trip data")
                }
                
                tripDao.updateTrip(it.copy(distance = distance))
            } ?: throw AppError.databaseError("Trip not found")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.databaseError("Failed to update trip distance", e)
        }
    }
    
    override suspend fun exportTripToCSV(tripId: Long): String {
        return try {
            val trip = tripDao.getTripById(tripId)
            val locationPoints = locationPointDao.getLocationPointsForTrip(tripId)
            
            if (trip == null) {
                throw AppError.databaseError("Trip not found")
            }
            
            val csvBuilder = StringBuilder()
            csvBuilder.append("Trip ID,Start Time,End Time,Duration (ms),Distance (km),Is Completed\n")
            
            val startTime = DateUtil.formatDateTime(trip.startTime)
            val endTime = trip.endTime?.let { DateUtil.formatDateTime(it) } ?: ""
            val duration = trip.duration
            val distance = trip.distance
            val isCompleted = if (trip.isCompleted) "Yes" else "No"
            
            csvBuilder.append("$tripId,$startTime,$endTime,$duration,$distance,$isCompleted\n")
            
            // Add location points
            csvBuilder.append("\nLocation Points\n")
            csvBuilder.append("Timestamp,Latitude,Longitude,Speed (m/s),Accuracy (m)\n")
            
            locationPoints.forEach { point ->
                val timestamp = DateUtil.formatDateTime(point.timestamp)
                csvBuilder.append("$timestamp,${point.latitude},${point.longitude},${point.speed},${point.accuracy}\n")
            }
            
            csvBuilder.toString()
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.fileError("Failed to export trip to CSV", e)
        }
    }
    
    override suspend fun exportTripToJSON(tripId: Long): String {
        return try {
            val trip = tripDao.getTripById(tripId) 
                ?: throw AppError.databaseError("Trip not found")
            
            val locationPoints = locationPointDao.getLocationPointsForTrip(tripId)
            
            val jsonBuilder = StringBuilder()
            jsonBuilder.append("{\n")
            jsonBuilder.append("  \"trip\": {\n")
            jsonBuilder.append("    \"id\": ${trip.id},\n")
            jsonBuilder.append("    \"startTime\": \"${trip.startTime}\",\n")
            jsonBuilder.append("    \"endTime\": ${if (trip.endTime != null) "\"${trip.endTime}\"" else "null"},\n")
            jsonBuilder.append("    \"duration\": ${trip.duration},\n")
            jsonBuilder.append("    \"distance\": ${trip.distance},\n")
            jsonBuilder.append("    \"isCompleted\": ${trip.isCompleted}\n")
            jsonBuilder.append("  },\n")
            jsonBuilder.append("  \"locationPoints\": [\n")
            
            locationPoints.forEachIndexed { index, point ->
                jsonBuilder.append("    {\n")
                jsonBuilder.append("      \"timestamp\": \"${point.timestamp}\",\n")
                jsonBuilder.append("      \"latitude\": ${point.latitude},\n")
                jsonBuilder.append("      \"longitude\": ${point.longitude},\n")
                jsonBuilder.append("      \"speed\": ${point.speed},\n")
                jsonBuilder.append("      \"accuracy\": ${point.accuracy}\n")
                jsonBuilder.append("    }${if (index < locationPoints.size - 1) "," else ""}\n")
            }
            
            jsonBuilder.append("  ]\n")
            jsonBuilder.append("}")
            
            jsonBuilder.toString()
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.fileError("Failed to export trip to JSON", e)
        }
    }

    override suspend fun exportAllTripsToCSV(): String {
        return try {
            val trips = tripDao.getAllTrips().first()
            
            val csvBuilder = StringBuilder()
            csvBuilder.append("Trip ID,Start Time,End Time,Duration (ms),Distance (km),Is Completed\n")
            
            trips.forEach { trip ->
                val startTime = DateUtil.formatDateTime(trip.startTime)
                val endTime = trip.endTime?.let { DateUtil.formatDateTime(it) } ?: ""
                val duration = trip.duration
                val distance = trip.distance
                val isCompleted = if (trip.isCompleted) "Yes" else "No"
                
                csvBuilder.append("${trip.id},$startTime,$endTime,$duration,$distance,$isCompleted\n")
            }
            
            csvBuilder.toString()
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.fileError("Failed to export all trips to CSV", e)
        }
    }
} 