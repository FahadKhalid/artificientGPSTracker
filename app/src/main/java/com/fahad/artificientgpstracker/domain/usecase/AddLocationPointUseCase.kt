package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import javax.inject.Inject

class AddLocationPointUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(
        tripId: Long,
        latitude: Double,
        longitude: Double,
        speed: Float,
        accuracy: Float
    ) {
        tripRepository.addLocationPoint(tripId, latitude, longitude, speed, accuracy)
    }
} 