package com.example.weatherapp.data.weather

import com.example.weatherapp.network.WeatherApiService
import retrofit2.Call

interface WeatherDataRepository {
    suspend fun getNetworkWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse>
}

class NetworkWeatherDataRepository(private val weatherApiService: WeatherApiService) : WeatherDataRepository {
    override suspend fun getNetworkWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse> {
        return weatherApiService.getCurrentWeather(latitude, longitude)
    }
}