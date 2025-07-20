package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import javax.inject.Inject

class ExportTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend fun exportToCSV(tripId: Long): String = tripRepository.exportTripToCSV(tripId)
    
    suspend fun exportToJSON(tripId: Long): String = tripRepository.exportTripToJSON(tripId)
} 