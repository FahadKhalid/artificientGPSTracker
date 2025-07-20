package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.time.LocalDateTime

class DeleteTripUseCaseTest {
    
    private lateinit var deleteTripUseCase: DeleteTripUseCase
    private lateinit var mockTripRepository: TripRepository
    
    @Before
    fun setup() {
        mockTripRepository = mock()
        deleteTripUseCase = DeleteTripUseCase(mockTripRepository)
    }
    
    @Test
    fun `invoke calls repository deleteTrip method`() = runTest {
        // Given
        val trip = Trip(id = 1, startTime = LocalDateTime.now(), distance = 1000.0)
        
        // When
        deleteTripUseCase(trip)
        
        // Then
        verify(mockTripRepository).deleteTrip(trip)
        verifyNoMoreInteractions(mockTripRepository)
    }
} 