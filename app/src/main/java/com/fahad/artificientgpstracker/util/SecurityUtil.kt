package com.fahad.artificientgpstracker.util

import android.content.Context
import com.fahad.artificientgpstracker.domain.model.AppError
import com.fahad.artificientgpstracker.domain.model.AppConstants


class SecurityUtil(
    private val context: Context
) {
    

    

    
    fun validateLocationData(latitude: Double, longitude: Double, accuracy: Float): Result<Unit> {
        return when {
            latitude.isNaN() || latitude.isInfinite() -> {
                Result.failure(AppError.validationError("Invalid latitude value", "latitude"))
            }
            longitude.isNaN() || longitude.isInfinite() -> {
                Result.failure(AppError.validationError("Invalid longitude value", "longitude"))
            }
            latitude !in -90.0..90.0 -> {
                Result.failure(AppError.validationError("Invalid latitude value", "latitude"))
            }
            longitude !in -180.0..180.0 -> {
                Result.failure(AppError.validationError("Invalid longitude value", "longitude"))
            }
            accuracy.isNaN() || accuracy.isInfinite() || accuracy < 0 || accuracy > AppConstants.MAX_ACCURACY_METERS -> {
                Result.failure(AppError.validationError("Invalid accuracy value", "accuracy"))
            }
            else -> Result.success(Unit)
        }
    }
    
    fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
    
    fun validateTripData(tripId: Long, distance: Double, duration: Long): Result<Unit> {
        return when {
            tripId <= 0 -> {
                Result.failure(AppError.validationError("Invalid trip ID", "tripId"))
            }
            distance.isNaN() || distance.isInfinite() || distance < 0 -> {
                Result.failure(AppError.validationError("Invalid distance value", "distance"))
            }
            duration < 0 -> {
                Result.failure(AppError.validationError("Invalid duration value", "duration"))
            }
            else -> Result.success(Unit)
        }
    }
} 