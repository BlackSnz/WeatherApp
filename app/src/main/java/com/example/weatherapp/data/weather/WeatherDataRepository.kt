package com.example.weatherapp.data.weather

import com.example.weatherapp.network.WeatherApi
import retrofit2.Call

interface WeatherDataRepository {
    suspend fun getWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse>
}

class NetworkWeatherDataRepository : WeatherDataRepository {
    override suspend fun getWeatherData(latitude: Double, longitude: Double): Call<WeatherResponse> {
        return WeatherApi.retrofitService.getCurrentWeather(latitude, longitude)
    }
}