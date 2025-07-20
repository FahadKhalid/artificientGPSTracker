package com.fahad.artificientgpstracker.domain.model

enum class LocationUpdateInterval(val intervalMs: Long, val displayName: String) {
    ONE_SECOND(1000L, "1 second"),
    FIVE_SECONDS(5000L, "5 seconds"),
    TEN_SECONDS(10000L, "10 seconds"),
    THIRTY_SECONDS(30000L, "30 seconds");
    
    companion object {
        fun fromInterval(intervalMs: Long): LocationUpdateInterval {
            return values().find { it.intervalMs == intervalMs } ?: FIVE_SECONDS
        }
    }
} 