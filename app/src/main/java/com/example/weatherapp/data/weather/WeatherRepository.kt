package com.example.weatherapp.data.weather

import android.util.Log
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherData?
    suspend fun getHourlyWeatherForecast(latitude: Double, longitude: Double): WeatherForecastResponse?
}

class WeatherRepositoryImpl @Inject constructor(
    private val localWeatherDataSource: WeatherDao,
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) : WeatherRepository {

    override suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherData? {
        val localWeatherData = localWeatherDataSource.getWeatherData()
        val currentTime = System.currentTimeMillis()
        return if (localWeatherData != null && currentTime - localWeatherData.lastUpdated < CACHE_EXPIRY_TIME) {
            Log.d("CacheDebug", "Get weather data from cache")
            localWeatherData
        } else {
            Log.d("CacheDebug", "Get weather data from server")
            val remoteWeatherData = remoteWeatherDataSource.fetchWeatherData(latitude, longitude)
            if (remoteWeatherData != null) {
                with(localWeatherDataSource) {
                    clearWeatherCache()
                    insertWeatherData(remoteWeatherData)
                }
                remoteWeatherData
            } else {
                return null
            }
        }
    }

    override suspend fun getHourlyWeatherForecast(
        latitude: Double,
        longitude: Double
    ): WeatherForecastResponse? {
        return remoteWeatherDataSource.getWeatherHourlyForecast(latitude, longitude)
    }

    companion object {
        const val CACHE_EXPIRY_TIME = 60 * 60 * 1000 // 1 hour
    }
}