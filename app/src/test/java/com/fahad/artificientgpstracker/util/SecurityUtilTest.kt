package com.fahad.artificientgpstracker.util

import android.content.Context
import com.fahad.artificientgpstracker.domain.model.AppError
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class SecurityUtilTest {
    
    @Mock
    private lateinit var context: Context
    
    private lateinit var securityUtil: SecurityUtil
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        securityUtil = SecurityUtil(context)
    }
    
    @Test
    fun `validateLocationData returns success for valid coordinates`() {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val accuracy = 5.0f
        
        // When
        val result = securityUtil.validateLocationData(latitude, longitude, accuracy)
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `validateLocationData returns failure for invalid latitude`() {
        // Given
        val latitude = 100.0 // Invalid latitude
        val longitude = -74.0060
        val accuracy = 5.0f
        
        // When
        val result = securityUtil.validateLocationData(latitude, longitude, accuracy)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `validateLocationData returns failure for invalid longitude`() {
        // Given
        val latitude = 40.7128
        val longitude = 200.0 // Invalid longitude
        val accuracy = 5.0f
        
        // When
        val result = securityUtil.validateLocationData(latitude, longitude, accuracy)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `validateLocationData returns failure for negative accuracy`() {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val accuracy = -5.0f // Invalid accuracy
        
        // When
        val result = securityUtil.validateLocationData(latitude, longitude, accuracy)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `validateTripData returns success for valid trip data`() {
        // Given
        val tripId = 1L
        val distance = 1000.0
        val duration = 3600000L
        
        // When
        val result = securityUtil.validateTripData(tripId, distance, duration)
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `validateTripData returns failure for invalid trip ID`() {
        // Given
        val tripId = 0L // Invalid trip ID
        val distance = 1000.0
        val duration = 3600000L
        
        // When
        val result = securityUtil.validateTripData(tripId, distance, duration)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `validateTripData returns failure for negative distance`() {
        // Given
        val tripId = 1L
        val distance = -100.0 // Invalid distance
        val duration = 3600000L
        
        // When
        val result = securityUtil.validateTripData(tripId, distance, duration)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `validateTripData returns failure for negative duration`() {
        // Given
        val tripId = 1L
        val distance = 1000.0
        val duration = -1000L // Invalid duration
        
        // When
        val result = securityUtil.validateTripData(tripId, distance, duration)
        
        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }
    
    @Test
    fun `sanitizeFileName removes special characters`() {
        // Given
        val fileName = "test file (1).txt"
        
        // When
        val result = securityUtil.sanitizeFileName(fileName)
        
        // Then
        assertEquals("test_file__1_.txt", result)
    }
    
    @Test
    fun `sanitizeFileName keeps valid characters`() {
        // Given
        val fileName = "test-file_123.txt"
        
        // When
        val result = securityUtil.sanitizeFileName(fileName)
        
        // Then
        assertEquals("test-file_123.txt", result)
    }
    
    @Test
    fun `sanitizeFileName handles empty string`() {
        // Given
        val fileName = ""
        
        // When
        val result = securityUtil.sanitizeFileName(fileName)
        
        // Then
        assertEquals("", result)
    }
} 