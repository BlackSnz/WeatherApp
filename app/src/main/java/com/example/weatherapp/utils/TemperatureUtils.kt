package com.example.weatherapp.utils

import kotlin.math.roundToInt

object TemperatureUtils {
    fun calculateTemperatureByUnit(temperature: Double, temperatureUnit: String): String {
        return when (temperatureUnit) {
            "Celsius" -> {
                temperature.minus(272.15).roundToInt().toString()
            }

            "Fahrenheit" -> {
                temperature.minus(272.15).times(9 / 5).plus(32).roundToInt().toString()
            }

            else -> throw IllegalArgumentException("Unsupported temperature unit")
        }
    }
}