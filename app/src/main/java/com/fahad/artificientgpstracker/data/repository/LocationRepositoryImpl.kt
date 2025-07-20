package com.fahad.artificientgpstracker.data.repository

import android.location.Location
import com.fahad.artificientgpstracker.data.location.LocationService
import com.fahad.artificientgpstracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRepository {
    
    override fun getLocationUpdates(intervalMs: Long): Flow<Location> = 
        locationService.getLocationUpdates(intervalMs)
    
    override fun getCurrentLocation(): Flow<Location> = 
        locationService.getCurrentLocation()
    
    override fun hasLocationPermission(): Boolean = 
        locationService.hasLocationPermission()
} 