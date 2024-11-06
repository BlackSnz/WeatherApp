package com.example.weatherapp

import android.app.Application

import dagger.hilt.android.HiltAndroidApp

const val APP_VERSION = "1.0.0"

@HiltAndroidApp
class WeatherApplication: Application()