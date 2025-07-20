package com.fahad.artificientgpstracker.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahad.artificientgpstracker.R
import com.fahad.artificientgpstracker.data.model.Trip
import com.fahad.artificientgpstracker.domain.usecase.DeleteTripUseCase
import com.fahad.artificientgpstracker.domain.usecase.GetAllTripsUseCase
import com.fahad.artificientgpstracker.util.FileUtil
import com.fahad.artificientgpstracker.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripHistoryViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : ViewModel() {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()
    
    init {
        loadTrips()
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllTripsUseCase()
                    .catch { e ->
                        _errorMessage.value = "Failed to load trips: ${e.message}"
                    }
                    .collect { trips ->
                        _trips.value = trips
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load trips: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTrip(trip: Trip, context: Context) {
        viewModelScope.launch {
            try {
                deleteTripUseCase(trip)
                _userMessage.value = context.getString(R.string.msg_trip_deleted)
                loadTrips() // Reload trips after deletion
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.msg_failed_to_delete_trip) + ": ${e.message}"
            }
        }
    }
    

    
    fun downloadAllHistory(context: Context) {
        viewModelScope.launch {
            try {
                // Check if there are trips to export
                if (_trips.value.isEmpty()) {
                    _errorMessage.value = "No trips available to export"
                    return@launch
                }
                
                // Generate CSV content for all trips
                val csvBuilder = StringBuilder()
                csvBuilder.append(context.getString(R.string.export_csv_header_trip))
                csvBuilder.append("\n")
                
                _trips.value.forEach { trip ->
                    val startTime = DateUtil.formatDateTime(trip.startTime)
                    val endTime = trip.endTime?.let { DateUtil.formatDateTime(it) } ?: ""
                    val duration = trip.duration
                    val distance = trip.distance
                    val isCompleted = if (trip.isCompleted) context.getString(R.string.export_csv_completed_yes) else context.getString(R.string.export_csv_completed_no)
                    
                    csvBuilder.append("${trip.id},$startTime,$endTime,$duration,$distance,$isCompleted\n")
                }
                
                val exportData = csvBuilder.toString()
                val fileName = context.getString(R.string.export_file_name_all_trips, System.currentTimeMillis())
                FileUtil.saveToFile(context, exportData, fileName)?.let { uri ->
                    FileUtil.shareFile(context, uri, "text/csv", "All Trip History")
                }
                _userMessage.value = context.getString(R.string.msg_all_trips_exported)
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.msg_failed_to_export_all_trips_csv) + ": ${e.message}"
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun clearUserMessage() {
        _userMessage.value = null
    }
    
    companion object {
        fun formatTripDate(trip: Trip): String {
            return DateUtil.formatDate(trip.startTime)
        }
        
        fun formatTripTime(trip: Trip): String {
            return DateUtil.formatTime(trip.startTime)
        }
        
        @SuppressLint("DefaultLocale")
        fun formatDuration(duration: Long): String {
            if (duration <= 0) return "0s"
            
            val hours = duration / (1000 * 60 * 60)
            val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
            val seconds = (duration % (1000 * 60)) / 1000
            
            return when {
                hours > 0 -> String.format("%dh %dm", hours, minutes)
                minutes > 0 -> String.format("%dm %ds", minutes, seconds)
                else -> String.format("%ds", seconds)
            }
        }
        
        fun formatDistance(distance: Double): String {
            val distanceKm = distance / 1000
            return String.format("%.2f km", distanceKm)
        }
    }
} 