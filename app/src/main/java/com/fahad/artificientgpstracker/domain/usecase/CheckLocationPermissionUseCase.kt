package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.LocationRepository
import javax.inject.Inject

class CheckLocationPermissionUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Boolean = locationRepository.hasLocationPermission()
} 