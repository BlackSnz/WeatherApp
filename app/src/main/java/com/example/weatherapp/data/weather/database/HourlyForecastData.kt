package com.example.weatherapp.data.weather.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_weather_data")
data class HourlyForecastData (
    @PrimaryKey val id: Int,
    val temperature: Double,
    val iconCode: String,
    val precipitation: Double,
    val time: Long,
    val lastUpdated: Long
)
