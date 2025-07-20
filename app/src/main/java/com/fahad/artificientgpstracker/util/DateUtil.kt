package com.fahad.artificientgpstracker.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateTime(dateTime: java.time.LocalDateTime): String {
        return try {
            dateTimeFormatter.format(Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()))
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateTime: java.time.LocalDateTime): String {
        return try {
            dateFormatter.format(Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()))
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(dateTime: java.time.LocalDateTime): String {
        return try {
            timeFormatter.format(Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()))
        } catch (e: Exception) {
            "Invalid Time"
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun durationBetween(start: java.time.LocalDateTime, end: java.time.LocalDateTime): Long {
        return try {
            val startMillis = Date.from(start.atZone(java.time.ZoneId.systemDefault()).toInstant()).time
            val endMillis = Date.from(end.atZone(java.time.ZoneId.systemDefault()).toInstant()).time
            endMillis - startMillis
        } catch (e: Exception) {
            0L
        }
    }
} 