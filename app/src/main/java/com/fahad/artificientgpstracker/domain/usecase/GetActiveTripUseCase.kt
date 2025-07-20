package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import com.fahad.artificientgpstracker.data.model.Trip
import javax.inject.Inject

class GetActiveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(): Trip? = tripRepository.getActiveTrip()
} 