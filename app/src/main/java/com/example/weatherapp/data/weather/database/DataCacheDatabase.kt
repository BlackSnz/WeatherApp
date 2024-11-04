package com.example.weatherapp.data.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.location.LocationDao
import com.example.weatherapp.data.location.LocationInfo

@Database(entities = [DailyWeatherData::class, HourlyForecastData::class, LocationInfo::class], version = 3)
abstract class DataCacheDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun locationDao(): LocationDao
}