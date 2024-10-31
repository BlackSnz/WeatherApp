package com.example.weatherapp.data.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_data LIMIT 1")
    suspend fun getWeatherData(): WeatherData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(data: WeatherData)

    @Query("DELETE FROM weather_data")
    suspend fun clearWeatherCache()
}