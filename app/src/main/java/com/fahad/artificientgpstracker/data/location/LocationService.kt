package com.fahad.artificientgpstracker.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.fahad.artificientgpstracker.R
import com.fahad.artificientgpstracker.domain.model.AppError
import com.fahad.artificientgpstracker.util.SecurityUtil
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationService @Inject constructor(
    private val context: Context,
    private val securityUtil: SecurityUtil
) {
    
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var currentLocationCallback: LocationCallback? = null
    
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun getLocationUpdates(intervalMs: Long = 5000): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(AppError.permissionError(context.getString(R.string.msg_location_permission_required)))
            return@callbackFlow
        }
        
        try {
            val locationRequest = LocationRequest.Builder(intervalMs)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(5f)
                .setMinUpdateIntervalMillis(intervalMs / 2)
                .build()
            
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        // Validate location data before sending
                        val validationResult = securityUtil.validateLocationData(
                            location.latitude,
                            location.longitude,
                            location.accuracy
                        )
                        
                        if (validationResult.isSuccess) {
                            trySend(location)
                        } else {
                            close(validationResult.exceptionOrNull() ?: AppError.locationError("Invalid location data"))
                        }
                    }
                }
                
                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        close(AppError.locationError("Location service unavailable"))
                    }
                }
            }
            
            // Store callback reference for cleanup
            currentLocationCallback = locationCallback
            
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                ).addOnFailureListener { exception ->
                    close(AppError.locationError("Failed to request location updates", exception))
                }
            } catch (e: SecurityException) {
                close(AppError.permissionError("Location permission denied"))
            }
            
            awaitClose {
                currentLocationCallback?.let { callback ->
                    fusedLocationClient.removeLocationUpdates(callback)
                    currentLocationCallback = null
                }
            }
        } catch (e: Exception) {
            close(AppError.locationError("Location service error", e))
        }
    }
    
    fun getCurrentLocation(): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(AppError.permissionError(context.getString(R.string.msg_location_permission_required)))
            return@callbackFlow
        }
        
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let { loc ->
                        // Validate location data before sending
                        val validationResult = securityUtil.validateLocationData(
                            loc.latitude,
                            loc.longitude,
                            loc.accuracy
                        )
                        
                        if (validationResult.isSuccess) {
                            trySend(loc)
                        } else {
                            close(validationResult.exceptionOrNull() ?: AppError.locationError("Invalid location data"))
                        }
                    } ?: run {
                        close(AppError.locationError("No location available"))
                    }
                    close()
                }
                .addOnFailureListener { exception ->
                    close(AppError.locationError("Failed to get current location", exception))
                }
            
            awaitClose { }
        } catch (e: SecurityException) {
            close(AppError.permissionError("Location permission denied"))
        } catch (e: Exception) {
            close(AppError.locationError("Location service error", e))
        }
    }
    
    fun cleanup() {
        currentLocationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            currentLocationCallback = null
        }
    }
    
    fun isLocationEnabled(): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            false
        }
    }
    
    fun getContext(): Context {
        return context
    }
} 