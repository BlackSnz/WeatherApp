package com.example.weatherapp.data.weather

import android.graphics.drawable.Drawable

data class WeatherUiData(
    // Temperature values
    val currentTemperature: String,
    val minTemperatureToday: String,
    val maxTemperatureToday: String,
    val feelsLikeTemperature: String,
    val weatherDescription: String,
    val iconCode: String,
    val humidity: String,
    // Wind values
    val windSpeed: String,
    val windDegrees: String,
    val windGust: String,
    val pressure: String
)
