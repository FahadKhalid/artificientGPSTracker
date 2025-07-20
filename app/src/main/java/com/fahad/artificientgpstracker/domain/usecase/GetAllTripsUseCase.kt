package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(): Flow<List<Trip>> = tripRepository.getAllTrips()
} 