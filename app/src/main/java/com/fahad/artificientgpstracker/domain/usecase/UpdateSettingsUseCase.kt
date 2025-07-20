package com.fahad.artificientgpstracker.domain.usecase

import com.fahad.artificientgpstracker.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun setLocationUpdateInterval(intervalMs: Long) {
        settingsRepository.setLocationUpdateInterval(intervalMs)
    }
    
    suspend fun setBackgroundTrackingEnabled(enabled: Boolean) {
        settingsRepository.setBackgroundTrackingEnabled(enabled)
    }
} 