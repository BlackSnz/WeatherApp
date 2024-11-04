package com.example.weatherapp.data.weather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.location.LocationInfo

@Dao
interface WeatherDao {

    // Daily weather data
    @Query("SELECT * FROM daily_weather_data LIMIT 1")
    suspend fun getDailyWeatherData(): DailyWeatherData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeatherData(data: DailyWeatherData)

    @Query("DELETE FROM daily_weather_data")
    suspend fun clearWeatherCache()

    // Hourly weather data
    @Query("SELECT * FROM hourly_weather_data")
    suspend fun getHourlyWeatherData(): List<HourlyForecastData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeatherData(data: HourlyForecastData)

    @Query("DELETE FROM hourly_weather_data")
    suspend fun clearHourlyWeatherCache()
}