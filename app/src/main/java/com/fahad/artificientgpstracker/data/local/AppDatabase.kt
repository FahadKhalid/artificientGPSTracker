package com.fahad.artificientgpstracker.data.local

import android.content.Context
import androidx.room.*
import com.fahad.artificientgpstracker.data.model.LocationPoint
import com.fahad.artificientgpstracker.data.model.Trip
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(
    entities = [Trip::class, LocationPoint::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun locationPointDao(): LocationPointDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }
} 