package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class GetAllTripsUseCaseTest {
    
    private lateinit var getAllTripsUseCase: GetAllTripsUseCase
    private lateinit var mockTripRepository: TripRepository
    
    @Before
    fun setup() {
        mockTripRepository = mock()
        getAllTripsUseCase = GetAllTripsUseCase(mockTripRepository)
    }
    
    @Test
    fun `invoke returns all trips from repository`() = runTest {
        // Given
        val mockTrips = listOf(
            Trip(id = 1, startTime = LocalDateTime.now(), distance = 1000.0),
            Trip(id = 2, startTime = LocalDateTime.now(), distance = 2000.0)
        )
        whenever(mockTripRepository.getAllTrips()).thenReturn(flowOf(mockTrips))
        
        // When
        val result = getAllTripsUseCase()
        
        // Then
        assertEquals(mockTrips, result.first())
    }
    
    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Given
        whenever(mockTripRepository.getAllTrips()).thenReturn(flowOf(emptyList()))
        
        // When
        val result = getAllTripsUseCase()
        
        // Then
        assertEquals(emptyList<Trip>(), result.first())
    }
} 