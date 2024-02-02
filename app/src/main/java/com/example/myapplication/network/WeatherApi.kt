package com.example.myapplication.network

import com.example.myapplication.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

private const val GET_DIV_TEMP_ERROR = "Can't parse temperature div text"

interface WeatherApi {
    suspend fun getCurrentDayWeather(): WeatherData
}

class WeatherApiService: WeatherApi {

    private val baseUrl = "http://www.meteo.nw.ru/"

     override suspend fun getCurrentDayWeather(): WeatherData {
         val currentDayTemperature: String
         withContext(Dispatchers.IO) {
             // Parse main page from baseUrl
             val doc = Jsoup.connect(baseUrl).get()
             // Parse current temperature from <div class = "wtemp">
             currentDayTemperature =
                doc.select("#wtemp").first()?.text() ?: GET_DIV_TEMP_ERROR
         }
         return WeatherData(currentDayTemperature)
    }
}