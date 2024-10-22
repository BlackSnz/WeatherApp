package com.example.weatherapp.data.weather

import android.graphics.drawable.Drawable

data class WeatherUi(
    // Temperature values
    val currentTemperature: String,
    val minTemperatureToday: String,
    val maxTemperatureToday: String,
    val feelsLikeTemperature: String,
    val weatherDescription: String,
    val icon: Drawable,
    val humidity: String,
    // Wind values
    val windSpeed: String,
    val windDegrees: String,
    val windGust: String,
    val pressure: String
)
