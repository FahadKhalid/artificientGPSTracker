package com.fahad.artificientgpstracker.di

import android.content.Context
import androidx.room.Room
import com.fahad.artificientgpstracker.data.local.AppDatabase
import com.fahad.artificientgpstracker.data.local.LocationPointDao
import com.fahad.artificientgpstracker.data.local.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gps_tracker_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }
    
    @Provides
    fun provideLocationPointDao(database: AppDatabase): LocationPointDao {
        return database.locationPointDao()
    }
} 