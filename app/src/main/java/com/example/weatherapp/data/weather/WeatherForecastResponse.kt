package com.example.weatherapp.data.weather

data class WeatherForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<Forecast>
)

data class Forecast(
    val dt: Long,
    val main: ForecastMain,
    val weather: List<ForecastWeather>,
    val clouds: ForecastClouds,
    val wind: ForecastWind,
    val rain: ForecastRain?,
    val snow: ForecastSnow?,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String
)

data class ForecastMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int?,
    val grnd_level: Int?,
    val humidity: Int,
    val temp_kf: Double?
)

data class ForecastWeather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
    val city: City
)

data class ForecastClouds(
    val all: Int
)

data class ForecastWind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

data class ForecastRain(
    val `1h`: Double?
)

data class ForecastSnow(
    val `1h`: Double?
)

data class ForecastSys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)