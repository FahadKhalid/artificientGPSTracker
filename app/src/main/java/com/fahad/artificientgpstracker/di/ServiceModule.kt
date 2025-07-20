package com.fahad.artificientgpstracker.di

import android.content.Context
import com.fahad.artificientgpstracker.data.location.LocationService
import com.fahad.artificientgpstracker.data.settings.SettingsDataStore
import com.fahad.artificientgpstracker.util.NetworkUtil
import com.fahad.artificientgpstracker.util.SecurityUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    
    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context, securityUtil: SecurityUtil): LocationService {
        return LocationService(context, securityUtil)
    }
    
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
    
    @Provides
    @Singleton
    fun provideNetworkUtil(@ApplicationContext context: Context): NetworkUtil {
        return NetworkUtil(context)
    }
    
    @Provides
    @Singleton
    fun provideSecurityUtil(@ApplicationContext context: Context): SecurityUtil {
        return SecurityUtil(context)
    }
} 