package com.example.myapplication.data

import com.example.myapplication.network.WeatherApiService

interface AppContainer {
    val weatherRepository: WeatherRepository
}

class DefaultAppContainer : AppContainer {

    override val weatherRepository: WeatherRepository by lazy {
        NetworkWeatherRepository(WeatherApiService())
    }
}