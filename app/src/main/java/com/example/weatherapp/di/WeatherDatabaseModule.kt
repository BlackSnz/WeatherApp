package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.location.LocationDao
import com.example.weatherapp.data.weather.database.DataCacheDatabase
import com.example.weatherapp.data.weather.database.WeatherDao
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
    fun provideDatabase(@ApplicationContext context: Context): DataCacheDatabase {
        return Room.databaseBuilder(
            context,
            DataCacheDatabase::class.java,
            "weather_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWeatherDao(database: DataCacheDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    fun provideLocationDao(database: DataCacheDatabase): LocationDao {
        return database.locationDao()
    }
}