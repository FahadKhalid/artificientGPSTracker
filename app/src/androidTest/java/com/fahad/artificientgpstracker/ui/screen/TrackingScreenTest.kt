package com.fahad.artificientgpstracker.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fahad.artificientgpstracker.domain.model.TrackingStatus
import com.fahad.artificientgpstracker.ui.state.TrackingState
import com.fahad.artificientgpstracker.ui.viewmodel.TrackingViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TrackingScreenTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var mockViewModel: TrackingViewModel
    
    @Before
    fun setup() {
        mockViewModel = mock()
        hiltRule.inject()
    }
    
    @Test
    fun trackingScreen_displaysStartButton_whenIdle() {
        // Given
        val state = TrackingState(trackingStatus = TrackingStatus.IDLE)
        
        // When
        composeTestRule.setContent {
            TrackingScreen(viewModel = mockViewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("Start Tracking").assertExists()
    }
    
    @Test
    fun trackingScreen_displaysTrackingMetrics_whenTracking() {
        // Given
        val state = TrackingState(
            trackingStatus = TrackingStatus.TRACKING,
            currentSpeed = 10f,
            distanceTraveled = 1000.0,
            elapsedTime = 60000L
        )
        
        // When
        composeTestRule.setContent {
            TrackingScreen(viewModel = mockViewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("Speed").assertExists()
        composeTestRule.onNodeWithText("Distance").assertExists()
        composeTestRule.onNodeWithText("Time").assertExists()
    }
    
    @Test
    fun trackingScreen_displaysPauseAndStopButtons_whenTracking() {
        // Given
        val state = TrackingState(trackingStatus = TrackingStatus.TRACKING)
        
        // When
        composeTestRule.setContent {
            TrackingScreen(viewModel = mockViewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("Pause").assertExists()
        composeTestRule.onNodeWithText("Stop").assertExists()
    }
} 