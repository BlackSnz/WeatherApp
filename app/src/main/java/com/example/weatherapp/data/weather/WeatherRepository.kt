package com.example.weatherapp.data.weather

import android.util.Log
import com.example.weatherapp.data.weather.database.CurrentWeatherData
import com.example.weatherapp.data.weather.database.HourlyForecastData
import com.example.weatherapp.data.weather.database.WeatherDao
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getCurrentWeatherData(latitude: Double, longitude: Double): CurrentWeatherData?
    suspend fun getHourlyWeatherForecastData(latitude: Double, longitude: Double): List<HourlyForecastData>?
}

class WeatherRepositoryImpl @Inject constructor(
    private val localWeatherDataSource: WeatherDao,
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) : WeatherRepository {

    override suspend fun getCurrentWeatherData(latitude: Double, longitude: Double): CurrentWeatherData? {
        val localWeatherData = localWeatherDataSource.getDailyWeatherData()
        val currentTime = System.currentTimeMillis()
        return if (localWeatherData != null && currentTime - localWeatherData.lastUpdated < CACHE_EXPIRY_TIME) {
            Log.d("CacheDebug", "Get daily weather data from cache")
            localWeatherData
        } else {
            Log.d("CacheDebug", "Get daily weather data from server")
            val remoteWeatherData = remoteWeatherDataSource.fetchWeatherData(latitude, longitude)
            if (remoteWeatherData != null) {
                with(localWeatherDataSource) {
                    clearWeatherCache()
                    insertDailyWeatherData(remoteWeatherData)
                }
                remoteWeatherData
            } else {
                Log.d("LoadingDebug", "Can't find a cache for the daily weather data")
                null
            }
        }
    }

    override suspend fun getHourlyWeatherForecastData(latitude: Double, longitude: Double): List<HourlyForecastData>? {
        val localHourlyWeatherData = localWeatherDataSource.getHourlyWeatherData()
        Log.d("LoadingDebug", "localHourlyWeatherData: $localHourlyWeatherData")

        val currentTime = System.currentTimeMillis()
        return if (localHourlyWeatherData.isNotEmpty() && currentTime - localHourlyWeatherData[0].lastUpdated < CACHE_EXPIRY_TIME) {
            Log.d("LoadingDebug", "Get hourly weather data from cache")
            localHourlyWeatherData
        } else {
            Log.d("LoadingDebug", "Get hourly weather data from server")
            val remoteHourlyWeatherData = remoteWeatherDataSource.fetchWeatherHourlyForecast(latitude, longitude)
            if (remoteHourlyWeatherData != null) {
                with(localWeatherDataSource) {
                    clearWeatherCache()
                    remoteHourlyWeatherData.forEach{
                        insertHourlyWeatherData(it)
                    }
                }
                remoteHourlyWeatherData
            } else {
                Log.d("LoadingDebug", "Can't find a cache for the hourly weather data")
                null
            }
        }
    }

    companion object {
        const val CACHE_EXPIRY_TIME = 60 * 60 * 1000 // 1 hour
    }
}