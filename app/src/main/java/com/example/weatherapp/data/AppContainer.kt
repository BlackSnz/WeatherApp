package com.example.weatherapp.data

import com.example.weatherapp.data.weather.NetworkWeatherDataRepository
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.network.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val weatherDataRepository: WeatherDataRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "https://api.openweathermap.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    override val weatherDataRepository: WeatherDataRepository by lazy {
        NetworkWeatherDataRepository(retrofitService)
    }
}