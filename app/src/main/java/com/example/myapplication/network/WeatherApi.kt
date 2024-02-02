package com.example.myapplication.network

import com.example.myapplication.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

private const val GET_DIV_TEMP_ERROR = "Can't parse temperature div text"
private const val GET_DIV_WINDTYPE_ERROR = "Can't parse windtype div"
private const val GET_DIV_WINDSPEED_ERROR = "Can't parse windspeed div "
private const val GET_DIV_ATMOSPHERE_PRESSURE_ERROR = "Can't parse atmosphere preasure div"
private const val GET_DIV_HUMIDITY_ERROR = "Can't parse humidity div"

interface WeatherApi {
    suspend fun getCurrentDayWeather(): WeatherData
}

class WeatherApiService: WeatherApi {

    private val baseUrl = "http://www.meteo.nw.ru/"

     override suspend fun getCurrentDayWeather(): WeatherData {

         val currentDayTemperature: String
         val currentDayWindType: String
         val currentDayWindSpeed: String
         val currentDayAtmospherePressure: String
         val currentDayHumidity: String

         withContext(Dispatchers.IO) {
             // Parse main page from baseUrl
             val doc = Jsoup.connect(baseUrl).get()
             // Parse current temperature from <div id = "wtemp">
             currentDayTemperature =
                 doc.select("#wtemp").first()?.text() ?: GET_DIV_TEMP_ERROR
             // Parse current wind type from first <div class = "wttdr">
             currentDayWindType =
                 doc.select("div.wttdr")[0].text() ?: GET_DIV_WINDTYPE_ERROR
             // Parse current wind speed from second <div class = "wttdr">
             currentDayWindSpeed =
                 doc.select("div.wttdr")[1].text() ?: GET_DIV_WINDSPEED_ERROR
             // Parse current atmosphere pressure from third <div class = "wttdr">
             currentDayAtmospherePressure =
                 doc.select("div.wttdr")[2].text() ?: GET_DIV_ATMOSPHERE_PRESSURE_ERROR
             // Parse current humidity from fourth <div class = "wttdr">
             currentDayHumidity =
                 doc.select("div.wttdr")[3].text() ?: GET_DIV_HUMIDITY_ERROR
         }

         return WeatherData(
             currentDayTemperature,
             currentDayWindType,
             currentDayWindSpeed,
             currentDayAtmospherePressure,
             currentDayHumidity
         )
    }
}