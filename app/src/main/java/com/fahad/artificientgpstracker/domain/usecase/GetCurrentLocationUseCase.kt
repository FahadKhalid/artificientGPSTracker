package com.fahad.artificientgpstracker.domain.usecase

import android.location.Location
import com.fahad.artificientgpstracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Location> = locationRepository.getCurrentLocation()
} 