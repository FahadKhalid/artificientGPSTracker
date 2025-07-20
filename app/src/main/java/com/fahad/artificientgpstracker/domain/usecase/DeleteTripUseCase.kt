package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        tripRepository.deleteTrip(trip)
    }
} 