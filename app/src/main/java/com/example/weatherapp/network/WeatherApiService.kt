package com.example.weatherapp.network

import com.example.weatherapp.data.weather.CurrentWeatherResponse
import com.example.weatherapp.data.weather.WeatherForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "1fac1074796fe012e2203d0cc101aca6"

interface WeatherApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Call<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    fun getHourlyWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Call<WeatherForecastResponse>
}