package com.fahad.artificientgpstracker.ui.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahad.artificientgpstracker.MainActivity
import com.fahad.artificientgpstracker.R
import com.fahad.artificientgpstracker.data.location.LocationService
import com.fahad.artificientgpstracker.domain.model.*
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import com.fahad.artificientgpstracker.data.settings.SettingsDataStore
import com.fahad.artificientgpstracker.ui.state.TrackingState
import com.fahad.artificientgpstracker.util.NetworkUtil
import com.fahad.artificientgpstracker.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.withTimeout

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val locationService: LocationService,
    private val settingsDataStore: SettingsDataStore,
    private val networkUtil: NetworkUtil
) : ViewModel() {
    
    private val _state = MutableStateFlow(TrackingState())
    val state: StateFlow<TrackingState> = _state.asStateFlow()
    
    private val _userMessage = MutableStateFlow<UserMessage?>(null)
    val userMessage: StateFlow<UserMessage?> = _userMessage.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var locationUpdateJob: Job? = null
    private var timerJob: Job? = null
    private var startTime: LocalDateTime? = null
    private var pausedTime: Long = 0L
    private var previousLocation: Location? = null
    private val _mutex = Mutex()
    
    init {
        viewModelScope.launch {
            settingsDataStore.locationUpdateInterval.collect { interval ->
                val locationInterval = LocationUpdateInterval.fromInterval(interval)
                _state.update { it.copy(locationUpdateInterval = locationInterval) }
            }
        }
        
        checkLocationPermission()
        checkNetworkConnection()
        getInitialLocation()
    }
    
    fun refreshPermissions() {
        checkLocationPermission()
        if (_state.value.hasLocationPermission) {
            getInitialLocation()
        }
    }
    
    private fun checkLocationPermission() {
        val hasPermission = locationService.hasLocationPermission()
        val permissionStatus = if (hasPermission) PermissionStatus.GRANTED else PermissionStatus.DENIED
        _state.update { it.copy(permissionStatus = permissionStatus) }
        
        if (!hasPermission) {
            _userMessage.value = UserMessage.warning("Location permission required for GPS tracking")
        } else {
            // Clear any previous permission warnings when permission is granted
            if (_userMessage.value?.toString()?.contains("permission") == true) {
                _userMessage.value = null
            }
        }
    }
    
    private fun checkNetworkConnection() {
        viewModelScope.launch {
            networkUtil.observeNetworkState().collect { isConnected ->
                val networkStatus = if (isConnected) NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED
                _state.update { it.copy(networkStatus = networkStatus) }
                
                if (!isConnected) {
                    _userMessage.value = UserMessage.warning("No internet connection. Maps may not load properly.")
                }
            }
        }
    }
    
    private fun getInitialLocation() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                withTimeout(AppConstants.LOCATION_TIMEOUT_MS) {
                    locationService.getCurrentLocation()
                        .catch { exception ->
                            val errorMessage = when (exception) {
                                is AppError.LocationError -> exception.message
                                is AppError.PermissionError -> exception.message
                                else -> "Failed to get initial location: ${exception.message}"
                            }
                            _state.update { it.copy(errorMessage = errorMessage) }
                            _userMessage.value = UserMessage.error("Failed to get location. Please check GPS settings.")
                            _isLoading.value = false
                        }
                        .collect { location ->
                            updateLocation(location)
                            _isLoading.value = false
                            _userMessage.value = UserMessage.success("Location acquired successfully")
                        }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is AppError.LocationError -> e.message
                    is AppError.PermissionError -> e.message
                    else -> "Failed to get initial location: ${e.message}"
                }
                _state.update { it.copy(errorMessage = errorMessage) }
                _userMessage.value = UserMessage.error("Failed to get location. Please check GPS settings.")
                _isLoading.value = false
            }
        }
    }
    
    fun startTracking() {
        // Re-check permissions before starting
        checkLocationPermission()
        
        if (!_state.value.hasLocationPermission) {
            _userMessage.value = UserMessage.error("Location permission required. Please grant location permissions in settings.")
            return
        }
        
        if (!locationService.isLocationEnabled()) {
            _userMessage.value = UserMessage.error("Location services are disabled. Please enable GPS.")
            return
        }
        
        if (!networkUtil.isInternetAvailable()) {
            _userMessage.value = UserMessage.warning("No internet connection. Maps may not display properly.")
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                withTimeout(AppConstants.TRACKING_START_TIMEOUT_MS) {
                    _mutex.withLock {
                        val tripId = tripRepository.startNewTrip()
                        startTime = LocalDateTime.now()
                        pausedTime = 0L
                        previousLocation = null
                        
                        _state.update { 
                            it.copy(
                                trackingStatus = TrackingStatus.TRACKING,
                                activeTripId = tripId,
                                distanceTraveled = 0.0,
                                elapsedTime = 0L,
                                errorMessage = null
                            )
                        }
                        
                        startLocationUpdates()
                        startTimer()
                        _isLoading.value = false
                        _userMessage.value = UserMessage.success("Tracking started")
                    }
                }
                
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is AppError.DatabaseError -> e.message
                    is AppError.ValidationError -> e.message
                    else -> "Failed to start tracking: ${e.message}"
                }
                _state.update { it.copy(errorMessage = errorMessage) }
                _userMessage.value = UserMessage.error("Failed to start tracking")
                _isLoading.value = false
            }
        }
    }
    
    fun pauseTracking() {
        _state.update { it.copy(trackingStatus = TrackingStatus.PAUSED) }
        locationUpdateJob?.cancel()
        timerJob?.cancel()
        
        startTime?.let { start ->
            val currentElapsed = DateUtil.durationBetween(start, LocalDateTime.now())
            pausedTime += currentElapsed - _state.value.elapsedTime
        }
        
        _userMessage.value = UserMessage.info("Tracking paused")
    }
    
    fun resumeTracking() {
        _state.update { it.copy(trackingStatus = TrackingStatus.TRACKING) }
        startLocationUpdates()
        startTimer()
        _userMessage.value = UserMessage.success("Tracking resumed")
    }
    
    fun stopTracking() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _mutex.withLock {
                    _state.value.activeTripId?.let { tripId ->
                        val finalDistance = _state.value.distanceTraveled
                        val finalDuration = _state.value.elapsedTime
                        
                        tripRepository.stopTrip(tripId, finalDistance, finalDuration)
                    }
                    
                    locationUpdateJob?.cancel()
                    timerJob?.cancel()
                    
                    _state.update { 
                        it.copy(
                            trackingStatus = TrackingStatus.STOPPED,
                            activeTripId = null,
                            currentSpeed = 0f,
                            distanceTraveled = 0.0,
                            elapsedTime = 0L
                        )
                    }
                    
                    previousLocation = null
                    startTime = null
                    pausedTime = 0L
                    
                    _isLoading.value = false
                    _userMessage.value = UserMessage.success("Trip saved successfully")
                }
                
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is AppError.DatabaseError -> e.message
                    is AppError.ValidationError -> e.message
                    else -> "Failed to stop tracking: ${e.message}"
                }
                _state.update { it.copy(errorMessage = errorMessage) }
                _userMessage.value = UserMessage.error("Failed to save trip")
                _isLoading.value = false
            }
        }
    }
    
    private fun startLocationUpdates() {
        locationUpdateJob?.cancel()
        locationUpdateJob = viewModelScope.launch {
            locationService.getLocationUpdates(_state.value.locationUpdateInterval.intervalMs)
                .catch { exception ->
                    val errorMessage = when (exception) {
                        is AppError.LocationError -> exception.message
                        is AppError.PermissionError -> exception.message
                        else -> "Location update failed: ${exception.message}"
                    }
                    _state.update { it.copy(errorMessage = errorMessage) }
                    _userMessage.value = UserMessage.error("Location update failed")
                }
                .collect { location ->
                    updateLocation(location)
                }
        }
    }
    
    private fun updateLocation(location: Location) {
        val previousLocation = this.previousLocation
        
        if (previousLocation != null && _state.value.trackingStatus == TrackingStatus.TRACKING) {
            val distance = calculateDistance(
                previousLocation.latitude, previousLocation.longitude,
                location.latitude, location.longitude
            )
            
            _state.update { 
                it.copy(
                    distanceTraveled = it.distanceTraveled + distance
                )
            }
        }
        
        _state.update { 
            it.copy(
                currentLocation = location,
                currentSpeed = location.speed
            )
        }
        
        this.previousLocation = location
        
        _state.value.activeTripId?.let { tripId ->
            viewModelScope.launch {
                try {
                    tripRepository.addLocationPoint(
                        tripId = tripId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed,
                        accuracy = location.accuracy
                    )
                    
                    // Distance is updated when trip is stopped, no need to update continuously
                } catch (e: Exception) {
                    val errorMessage = when (e) {
                        is AppError.DatabaseError -> e.message
                        is AppError.ValidationError -> e.message
                        else -> "Failed to save location: ${e.message}"
                    }
                    _state.update { it.copy(errorMessage = errorMessage) }
                }
            }
        }
    }
    
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = AppConstants.EARTH_RADIUS_METERS
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(AppConstants.MILLISECONDS_PER_SECOND)
                if (_state.value.trackingStatus == TrackingStatus.TRACKING) {
                    startTime?.let { start ->
                        val totalElapsed = DateUtil.durationBetween(start, LocalDateTime.now())
                        val actualElapsed = totalElapsed - pausedTime
                        _state.update { it.copy(elapsedTime = actualElapsed) }
                    }
                }
            }
        }
    }
    
    fun clearUserMessage() {
        _userMessage.value = null
    }
    
    fun onScreenResume() {
        // Re-check permissions and location when screen is resumed
        checkLocationPermission()
        
        // Check if permissions were recently granted
        if (MainActivity.isPermissionGranted(locationService.getContext())) {
            MainActivity.clearPermissionGranted(locationService.getContext())
            onPermissionGranted()
        } else if (_state.value.hasLocationPermission && _state.value.currentLocation == null) {
            getInitialLocation()
        }
        
        // Check if app came to foreground and trigger refresh
        checkForegroundRefresh()
        
        // Start automatic permission monitoring
        startPermissionMonitoring()
    }
    
    fun onPermissionGranted() {
        // Called when permissions are granted from MainActivity
        checkLocationPermission()
        if (_state.value.hasLocationPermission) {
            getInitialLocation()
        }
    }
    
    private fun startPermissionMonitoring() {
        viewModelScope.launch {
            // Check permissions every 2 seconds when screen is active
            while (true) {
                kotlinx.coroutines.delay(2000) // Check every 2 seconds
                
                val currentPermissionStatus = if (locationService.hasLocationPermission()) {
                    PermissionStatus.GRANTED
                } else {
                    PermissionStatus.DENIED
                }
                
                // If permission status changed, update state and refresh location
                if (_state.value.permissionStatus != currentPermissionStatus) {
                    _state.update { it.copy(permissionStatus = currentPermissionStatus) }
                    
                    if (currentPermissionStatus == PermissionStatus.GRANTED) {
                        // Permission was just granted, get location
                        getInitialLocation()
                        _userMessage.value = UserMessage.success("Location permission granted")
                    } else {
                        // Permission was revoked
                        _userMessage.value = UserMessage.warning("Location permission required")
                    }
                }
            }
        }
    }
    
    private fun checkForegroundRefresh() {
        val prefs = locationService.getContext().getSharedPreferences("permission_events", Context.MODE_PRIVATE)
        val lastForegroundRefresh = prefs.getLong("foreground_refresh", 0L)
        val currentTime = System.currentTimeMillis()
        
        // If app came to foreground in the last 5 seconds, refresh permissions
        if (currentTime - lastForegroundRefresh < 5000) {
            prefs.edit().remove("foreground_refresh").apply()
            checkLocationPermission()
            if (_state.value.hasLocationPermission && _state.value.currentLocation == null) {
                getInitialLocation()
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        locationUpdateJob?.cancel()
        timerJob?.cancel()
        // Cleanup location service
        locationService.cleanup()
    }


} 