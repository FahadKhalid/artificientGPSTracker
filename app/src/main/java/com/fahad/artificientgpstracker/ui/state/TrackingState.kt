package com.fahad.artificientgpstracker.ui.state

import android.location.Location
import com.fahad.artificientgpstracker.domain.model.AppConstants
import com.fahad.artificientgpstracker.domain.model.LocationUpdateInterval
import com.fahad.artificientgpstracker.domain.model.NetworkStatus
import com.fahad.artificientgpstracker.domain.model.PermissionStatus
import com.fahad.artificientgpstracker.domain.model.TrackingStatus
import com.google.android.gms.maps.model.LatLng

data class TrackingState(
    val trackingStatus: TrackingStatus = TrackingStatus.IDLE,
    val currentLocation: Location? = null,
    val currentSpeed: Float = 0f,
    val distanceTraveled: Double = 0.0,
    val elapsedTime: Long = 0L,
    val activeTripId: Long? = null,
    val locationUpdateInterval: LocationUpdateInterval = LocationUpdateInterval.FIVE_SECONDS,
    val permissionStatus: PermissionStatus = PermissionStatus.DENIED,
    val networkStatus: NetworkStatus = NetworkStatus.UNKNOWN,
    val errorMessage: String? = null
) {
    val currentLatLng: LatLng?
        get() = currentLocation?.let { LatLng(it.latitude, it.longitude) }
    
    val speedInKmh: Float
        get() = currentSpeed * AppConstants.MPS_TO_KMH_MULTIPLIER
    
    val distanceInKm: Double
        get() = distanceTraveled / AppConstants.METERS_PER_KILOMETER
    
    val formattedElapsedTime: String
        get() {
            val hours = elapsedTime / (AppConstants.MILLISECONDS_PER_SECOND * AppConstants.SECONDS_PER_MINUTE * AppConstants.MINUTES_PER_HOUR)
            val minutes = (elapsedTime % (AppConstants.MILLISECONDS_PER_SECOND * AppConstants.SECONDS_PER_MINUTE * AppConstants.MINUTES_PER_HOUR)) / (AppConstants.MILLISECONDS_PER_SECOND * AppConstants.SECONDS_PER_MINUTE)
            val seconds = (elapsedTime % (AppConstants.MILLISECONDS_PER_SECOND * AppConstants.SECONDS_PER_MINUTE)) / AppConstants.MILLISECONDS_PER_SECOND
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    
    val hasLocationPermission: Boolean
        get() = permissionStatus == PermissionStatus.GRANTED
} 