package com.example.weatherapp.data.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherData (
    @PrimaryKey val id: Int,
    val currentTemperature: Double,
    val minTemperatureToday: Double,
    val maxTemperatureToday: Double,
    val feelsLikeTemperature: Double,
    val weatherDescription: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeed: Double,
    val windDegrees: Int,
    val windGust: Double?,
    val pressure: Int,
    val lastUpdated: Long,
    val cityName: String
)
