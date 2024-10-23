package com.example.weatherapp.data.weather

import com.example.weatherapp.network.WeatherApiService
import retrofit2.Call

interface WeatherDataRepository {
    fun getWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse>
}

class NetworkWeatherDataRepository(private val weatherApiService: WeatherApiService) : WeatherDataRepository {
    override fun getWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse> {
        return weatherApiService.getWeatherData(latitude, longitude)
    }
}