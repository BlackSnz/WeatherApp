package com.example.weatherapp.data.weather.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.weather.WeatherDao

@Database(entities = [DailyWeatherData::class, HourlyForecastData::class], version = 2)
abstract class WeatherCacheDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}