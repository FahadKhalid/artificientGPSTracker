package com.fahad.artificientgpstracker.ui.navigation

sealed class Screen(val route: String) {
    object Tracking : Screen("tracking")
    object TripHistory : Screen("trip_history")
    object Settings : Screen("settings")
} 