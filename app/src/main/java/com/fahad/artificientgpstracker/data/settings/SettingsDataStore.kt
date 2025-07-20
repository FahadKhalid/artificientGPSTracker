package com.fahad.artificientgpstracker.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fahad.artificientgpstracker.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        private val LOCATION_UPDATE_INTERVAL = longPreferencesKey("location_update_interval")
        private val BACKGROUND_TRACKING_ENABLED = booleanPreferencesKey("background_tracking_enabled")
    }
    
    val locationUpdateInterval: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LOCATION_UPDATE_INTERVAL] ?: 5000L // Default 5 seconds
    }
    
    val backgroundTrackingEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BACKGROUND_TRACKING_ENABLED] ?: false
    }
    
    suspend fun setLocationUpdateInterval(intervalMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_UPDATE_INTERVAL] = intervalMs
        }
    }
    
    suspend fun setBackgroundTrackingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_TRACKING_ENABLED] = enabled
        }
    }
} 