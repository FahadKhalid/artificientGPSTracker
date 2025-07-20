package com.fahad.artificientgpstracker

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahad.artificientgpstracker.ui.navigation.Screen
import com.fahad.artificientgpstracker.ui.screen.SettingsScreen
import com.fahad.artificientgpstracker.ui.screen.TrackingScreen
import com.fahad.artificientgpstracker.ui.screen.TripHistoryScreen
import com.fahad.artificientgpstracker.ui.theme.ArtificientGPSTrackerTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val PREF_NAME = "permission_events"
        private const val KEY_PERMISSION_GRANTED = "permission_granted"
        
        fun setPermissionGranted(context: Context) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_PERMISSION_GRANTED, true)
                .apply()
        }
        
        fun isPermissionGranted(context: Context): Boolean {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_PERMISSION_GRANTED, false)
        }
        
        fun clearPermissionGranted(context: Context) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_PERMISSION_GRANTED)
                .apply()
        }
    }
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permission granted - trigger location service refresh
                refreshLocationService()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permission granted - trigger location service refresh
                refreshLocationService()
            }
            else -> {
                // Permission denied
            }
        }
    }
    
    private fun refreshLocationService() {
        // Set a flag that the ViewModel can check
        setPermissionGranted(this)
        
        // Also trigger immediate refresh by setting a timestamp
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong("last_permission_check", System.currentTimeMillis())
            .apply()
    }
    
    private fun triggerPermissionRefresh() {
        // Trigger automatic permission refresh when app comes to foreground
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong("foreground_refresh", System.currentTimeMillis())
            .apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Add lifecycle observer for automatic refresh
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        // App came to foreground, trigger permission refresh
                        triggerPermissionRefresh()
                    }
                    else -> {}
                }
            }
        })
        
        requestLocationPermissions()
        
        setContent {
            ArtificientGPSTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GpsTrackerApp()
                }
            }
        }
    }
    
    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Composable
fun GpsTrackerApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentDestination = navController.currentDestination
                
                NavigationBarItem(
                    selected = currentDestination?.route == Screen.Tracking.route,
                    onClick = {
                        navController.navigate(Screen.Tracking.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = stringResource(R.string.cd_tracking)) },
                    label = { Text(stringResource(R.string.nav_tracking)) }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.route == Screen.TripHistory.route,
                    onClick = {
                        navController.navigate(Screen.TripHistory.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = stringResource(R.string.cd_history)) },
                    label = { Text(stringResource(R.string.nav_history)) }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.route == Screen.Settings.route,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_settings)) },
                    label = { Text(stringResource(R.string.nav_settings)) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tracking.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tracking.route) {
                TrackingScreen()
            }
            composable(Screen.TripHistory.route) {
                TripHistoryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}