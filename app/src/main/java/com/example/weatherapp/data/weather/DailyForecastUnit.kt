package com.example.weatherapp.data.weather

data class DailyForecastUnit(
    val maxTemperature: String,
    val minTemperature: String,
    val iconCode: String,
    val precipitation: String,
    val dayOfWeek: String
)
