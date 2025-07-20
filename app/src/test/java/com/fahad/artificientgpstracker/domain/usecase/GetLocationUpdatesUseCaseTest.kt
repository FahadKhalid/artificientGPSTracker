package com.fahad.artificientgpstracker.domain.usecase

import android.location.Location
import com.fahad.artificientgpstracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetLocationUpdatesUseCaseTest {
    
    private lateinit var getLocationUpdatesUseCase: GetLocationUpdatesUseCase
    private lateinit var mockLocationRepository: LocationRepository
    
    @Before
    fun setup() {
        mockLocationRepository = mock()
        getLocationUpdatesUseCase = GetLocationUpdatesUseCase(mockLocationRepository)
    }
    
    @Test
    fun `invoke returns location updates from repository`() = runTest {
        // Given
        val intervalMs = 5000L
        val mockLocation = Location("test").apply {
            latitude = 40.7128
            longitude = -74.0060
            accuracy = 10f
        }
        whenever(mockLocationRepository.getLocationUpdates(intervalMs))
            .thenReturn(flowOf(mockLocation))
        
        // When
        val result = getLocationUpdatesUseCase(intervalMs)
        
        // Then
        assertEquals(mockLocation, result.first())
    }
} 