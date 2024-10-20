package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.AppContainer
import com.example.weatherapp.data.DefaultAppContainer

class WeatherApplication: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}