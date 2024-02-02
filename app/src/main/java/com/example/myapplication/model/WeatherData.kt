package com.example.myapplication.model

data class WeatherData(
    val currentDayWeatherTemperature: String = "-0.7 oC",
    val currentDayWindType: String = "северо-западный",
    val currentDayWindSpeed: String = "2.2 м/с",
    val currentDayAtmospherePressure: String = "748,2 мм.рт.ст.",
    val currentDayHumidity: String = "64%",
)
