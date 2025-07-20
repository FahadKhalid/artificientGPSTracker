package com.fahad.artificientgpstracker.data.local

import androidx.room.*
import com.fahad.artificientgpstracker.data.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?
    
    @Insert
    suspend fun insertTrip(trip: Trip): Long
    
    @Update
    suspend fun updateTrip(trip: Trip)
    
    @Delete
    suspend fun deleteTrip(trip: Trip)
    
    @Query("SELECT * FROM trips WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveTrip(): Trip?
} 