package com.fahad.artificientgpstracker.data.local

import androidx.room.*
import com.fahad.artificientgpstracker.data.model.LocationPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {
    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getLocationPointsForTrip(tripId: Long): List<LocationPoint>
    
    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getLocationPointsForTripFlow(tripId: Long): Flow<List<LocationPoint>>
    
    @Insert
    suspend fun insertLocationPoint(locationPoint: LocationPoint): Long
    
    @Update
    suspend fun updateLocationPoint(locationPoint: LocationPoint)
    
    @Delete
    suspend fun deleteLocationPoint(locationPoint: LocationPoint)
    
    @Query("DELETE FROM location_points WHERE tripId = :tripId")
    suspend fun deleteLocationPointsForTrip(tripId: Long)
    
    @Query("SELECT COUNT(*) FROM location_points WHERE tripId = :tripId")
    suspend fun getLocationPointCountForTrip(tripId: Long): Int
} 