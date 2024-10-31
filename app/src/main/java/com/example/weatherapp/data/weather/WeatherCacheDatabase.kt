package com.example.weatherapp.data.weather

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class], version = 1)
abstract class WeatherCacheDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}