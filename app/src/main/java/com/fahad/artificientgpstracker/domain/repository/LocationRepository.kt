package com.fahad.artificientgpstracker.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(intervalMs: Long): Flow<Location>
    fun getCurrentLocation(): Flow<Location>
    fun hasLocationPermission(): Boolean
} 