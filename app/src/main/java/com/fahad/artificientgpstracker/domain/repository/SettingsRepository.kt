package com.fahad.artificientgpstracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLocationUpdateInterval(): Flow<Long>
    suspend fun setLocationUpdateInterval(intervalMs: Long)
    fun getBackgroundTrackingEnabled(): Flow<Boolean>
    suspend fun setBackgroundTrackingEnabled(enabled: Boolean)
} 