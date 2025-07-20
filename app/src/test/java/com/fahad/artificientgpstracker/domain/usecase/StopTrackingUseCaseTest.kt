package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.TripRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class StopTrackingUseCaseTest {
    
    @Mock
    private lateinit var tripRepository: TripRepository
    
    private lateinit var stopTrackingUseCase: StopTrackingUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        stopTrackingUseCase = StopTrackingUseCase(tripRepository)
    }
    
    @Test
    fun `invoke calls repository with correct parameters`() = runTest {
        // Given
        val tripId = 1L
        val finalDistance = 1000.0
        val finalDuration = 3600000L
        
        // When
        stopTrackingUseCase(tripId, finalDistance, finalDuration)
        
        // Then
        verify(tripRepository).stopTrip(tripId, finalDistance, finalDuration)
    }
} 