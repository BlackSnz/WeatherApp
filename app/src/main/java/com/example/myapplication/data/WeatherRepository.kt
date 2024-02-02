package com.example.myapplication.data

import com.example.myapplication.model.WeatherData
import com.example.myapplication.network.WeatherApi

interface WeatherRepository {
    suspend fun getWeatherData(): WeatherData
}
class NetworkWeatherRepository(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherData(): WeatherData = weatherApi.getCurrentDayWeather()
}