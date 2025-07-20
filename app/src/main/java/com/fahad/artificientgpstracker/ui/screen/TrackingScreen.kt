package com.fahad.artificientgpstracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fahad.artificientgpstracker.R
import com.fahad.artificientgpstracker.domain.model.TrackingStatus
import com.fahad.artificientgpstracker.domain.model.UserMessage
import com.fahad.artificientgpstracker.ui.state.TrackingState
import com.fahad.artificientgpstracker.ui.theme.ArtificientGPSTrackerTheme
import com.fahad.artificientgpstracker.ui.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Call onScreenResume when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.onScreenResume()
    }
    
    LaunchedEffect(userMessage) {
        userMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message.toString(),
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                state.currentLatLng?.let { latLng ->
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                    
                    // Update camera position when location changes
                    LaunchedEffect(latLng) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                    
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        Marker(
                            state = MarkerState(position = latLng),
                            title = stringResource(R.string.tracking_current_location)
                        )
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.tracking_getting_location),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            if (state.currentLocation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.tracking_gps_location),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.tracking_latitude, String.format("%.4f", state.currentLocation!!.latitude)),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(R.string.tracking_longitude, String.format("%.4f", state.currentLocation!!.longitude)),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(R.string.tracking_accuracy, String.format("%.1f", state.currentLocation!!.accuracy)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Tracking metrics
                if (state.trackingStatus != TrackingStatus.IDLE) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.tracking_metrics),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MetricItem(
                                    label = stringResource(R.string.tracking_speed),
                                    value = String.format("%.1f km/h", state.speedInKmh),
                                    modifier = Modifier.weight(1f)
                                )
                                
                                MetricItem(
                                    label = stringResource(R.string.tracking_distance),
                                    value = String.format("%.2f km", state.distanceInKm),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                MetricItem(
                                    label = stringResource(R.string.tracking_time),
                                    value = state.formattedElapsedTime,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                // Control buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    when (state.trackingStatus) {
                        TrackingStatus.IDLE -> {
                            Button(
                                onClick = { viewModel.startTracking() },
                                enabled = state.hasLocationPermission && !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_start))
                            }
                        }
                        
                        TrackingStatus.PAUSED -> {
                            Button(
                                onClick = { viewModel.resumeTracking() },
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_resume))
                            }
                            
                            Button(
                                onClick = { viewModel.stopTracking() },
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_stop))
                            }
                        }
                        
                        TrackingStatus.TRACKING -> {
                            Button(
                                onClick = { viewModel.pauseTracking() },
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_pause))
                            }
                            
                            Button(
                                onClick = { viewModel.stopTracking() },
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_stop))
                            }
                        }
                        
                        TrackingStatus.STOPPED -> {
                            Button(
                                onClick = { viewModel.startTracking() },
                                enabled = state.hasLocationPermission && !isLoading
                            ) {
                                Text(stringResource(R.string.tracking_start_new))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingScreenPreview() {
    ArtificientGPSTrackerTheme {
        TrackingScreen()
    }
}

@Preview(showBackground = true, name = "Tracking Screen - Idle State")
@Composable
fun TrackingScreenIdlePreview() {
    ArtificientGPSTrackerTheme {
        // This preview shows the idle state without ViewModel dependency
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.preview_getting_location),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { },
                enabled = false
            ) {
                Text(stringResource(R.string.preview_start_tracking))
            }
        }
    }
} 