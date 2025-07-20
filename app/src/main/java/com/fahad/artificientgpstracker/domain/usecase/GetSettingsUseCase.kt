package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun getLocationUpdateInterval(): Flow<Long> = 
        settingsRepository.getLocationUpdateInterval()
    
    fun getBackgroundTrackingEnabled(): Flow<Boolean> = 
        settingsRepository.getBackgroundTrackingEnabled()
} 