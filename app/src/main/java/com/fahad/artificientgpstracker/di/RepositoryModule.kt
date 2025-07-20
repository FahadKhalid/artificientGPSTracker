package com.fahad.artificientgpstracker.di

import com.fahad.artificientgpstracker.data.repository.TripRepositoryImpl
import com.fahad.artificientgpstracker.data.repository.LocationRepositoryImpl
import com.fahad.artificientgpstracker.data.repository.SettingsRepositoryImpl
import com.fahad.artificientgpstracker.domain.repository.TripRepository
import com.fahad.artificientgpstracker.domain.repository.LocationRepository
import com.fahad.artificientgpstracker.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
} 