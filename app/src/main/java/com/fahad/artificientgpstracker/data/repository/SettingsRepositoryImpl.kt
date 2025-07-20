package com.fahad.artificientgpstracker.data.repository

import com.fahad.artificientgpstracker.data.settings.SettingsDataStore
import com.fahad.artificientgpstracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    
    override fun getLocationUpdateInterval(): Flow<Long> = 
        settingsDataStore.locationUpdateInterval
    
    override suspend fun setLocationUpdateInterval(intervalMs: Long) {
        settingsDataStore.setLocationUpdateInterval(intervalMs)
    }
    
    override fun getBackgroundTrackingEnabled(): Flow<Boolean> = 
        settingsDataStore.backgroundTrackingEnabled
    
    override suspend fun setBackgroundTrackingEnabled(enabled: Boolean) {
        settingsDataStore.setBackgroundTrackingEnabled(enabled)
    }
} 