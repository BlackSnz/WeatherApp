package com.example.weatherapp.data.weather

import android.graphics.drawable.Drawable

data class WeatherUi(
    val temperature: String,
    val icon: Drawable,
    val humidity: String,
    val wind: String,
    val pressure: String
)
