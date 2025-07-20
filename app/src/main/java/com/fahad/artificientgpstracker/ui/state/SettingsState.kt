package com.fahad.artificientgpstracker.ui.state

data class SettingsState(
    val locationUpdateInterval: Long = 5000L,
    val backgroundTrackingEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val availableIntervals = listOf(
        1000L to "1 second",
        5000L to "5 seconds", 
        10000L to "10 seconds",
        30000L to "30 seconds"
    )
    
    val selectedIntervalText: String
        get() = availableIntervals.find { it.first == locationUpdateInterval }?.second ?: "5 seconds"
} 