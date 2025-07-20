package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(tripId: Long, finalDistance: Double, finalDuration: Long) {
        tripRepository.stopTrip(tripId, finalDistance, finalDuration)
    }
} 