package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class StartTrackingUseCaseTest {
    
    @Mock
    private lateinit var tripRepository: TripRepository
    
    private lateinit var startTrackingUseCase: StartTrackingUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        startTrackingUseCase = StartTrackingUseCase(tripRepository)
    }
    
    @Test
    fun `invoke returns trip ID when successful`() = runTest {
        // Given
        val expectedTripId = 1L
        whenever(tripRepository.startNewTrip()).thenReturn(expectedTripId)
        
        // When
        val result = startTrackingUseCase()
        
        // Then
        assertEquals(expectedTripId, result)
    }
} 