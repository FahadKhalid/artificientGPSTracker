package com.fahad.artificientgpstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahad.artificientgpstracker.R
import com.fahad.artificientgpstracker.data.settings.SettingsDataStore
import com.fahad.artificientgpstracker.domain.model.AppConstants
import com.fahad.artificientgpstracker.ui.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsDataStore.locationUpdateInterval,
                settingsDataStore.backgroundTrackingEnabled
            ) { interval, backgroundTracking ->
                SettingsState(
                    locationUpdateInterval = interval,
                    backgroundTrackingEnabled = backgroundTracking
                )
            }.collect { settingsState ->
                _state.update { settingsState }
            }
        }
    }
    
    fun updateLocationInterval(intervalMs: Long) {
        viewModelScope.launch {
            try {
                // Validate interval value
                if (intervalMs < AppConstants.MIN_LOCATION_INTERVAL_MS || intervalMs > AppConstants.MAX_LOCATION_INTERVAL_MS) {
                    _state.update { it.copy(errorMessage = "Invalid interval value. Must be between 1 and 60 seconds.") }
                    return@launch
                }
                
                settingsDataStore.setLocationUpdateInterval(intervalMs)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Failed to update interval: ${e.message}") }
            }
        }
    }
    
    fun updateBackgroundTracking(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsDataStore.setBackgroundTrackingEnabled(enabled)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Failed to update background tracking: ${e.message}") }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
} 