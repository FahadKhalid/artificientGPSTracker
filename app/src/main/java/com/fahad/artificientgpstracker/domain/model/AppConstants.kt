package com.fahad.artificientgpstracker.domain.model

object AppConstants {
    // Location Constants
    const val EARTH_RADIUS_METERS = 6371000.0
    const val MIN_DISTANCE_METERS = 5.0f
    
    // Time Constants
    const val MILLISECONDS_PER_SECOND = 1000L
    const val SECONDS_PER_MINUTE = 60L
    const val MINUTES_PER_HOUR = 60L
    const val HOURS_PER_DAY = 24L
    
    // Speed Constants
    const val MPS_TO_KMH_MULTIPLIER = 3.6f
    const val KMH_TO_MPS_MULTIPLIER = 0.277778f
    
    // Distance Constants
    const val METERS_PER_KILOMETER = 1000.0
    
    // Database Constants
    const val DATABASE_NAME = "gps_tracker_database"
    const val DATABASE_VERSION = 1
    
    // Settings Constants
    const val DEFAULT_LOCATION_UPDATE_INTERVAL = 5000L
    const val DEFAULT_BACKGROUND_TRACKING_ENABLED = false
    
    // File Export Constants
    const val CSV_HEADER = "Trip ID,Start Time,End Time,Duration (ms),Distance (km),Is Completed"
    const val CSV_LOCATION_HEADER = "Timestamp,Latitude,Longitude,Speed (m/s),Accuracy (m)"
    
    // Timeout Constants
    const val LOCATION_TIMEOUT_MS = 10000L
    const val TRACKING_START_TIMEOUT_MS = 5000L
    
    // UI Constants
    const val LOADING_TIMEOUT_MS = 5000L
    const val USER_MESSAGE_DISPLAY_TIME_MS = 3000L
    
    // Validation Constants
    const val MIN_LOCATION_INTERVAL_MS = 1000L
    const val MAX_LOCATION_INTERVAL_MS = 60000L
    const val MAX_ACCURACY_METERS = 1000.0f
} 