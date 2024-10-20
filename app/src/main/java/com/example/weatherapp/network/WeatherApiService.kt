package com.example.weatherapp.network

import com.example.weatherapp.data.weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "1fac1074796fe012e2203d0cc101aca6"

interface WeatherApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Call<WeatherResponse>
}