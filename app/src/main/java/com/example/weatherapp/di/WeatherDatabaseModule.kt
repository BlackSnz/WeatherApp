package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.weather.database.WeatherCacheDatabase
import com.example.weatherapp.data.weather.WeatherDao
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
    fun provideDatabase(@ApplicationContext context: Context): WeatherCacheDatabase {
        return Room.databaseBuilder(
            context,
            WeatherCacheDatabase::class.java,
            "weather_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWeatherDao(database: WeatherCacheDatabase): WeatherDao {
        return database.weatherDao()
    }
}