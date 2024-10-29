package com.example.weatherapp.data.weather

import android.graphics.drawable.Drawable
import android.health.connect.datatypes.units.Temperature
import androidx.annotation.DrawableRes

data class HourlyForecastUnit (
    val temperature: String,
    val iconCode: String,
    val precipitation: String,
    val time: String
)