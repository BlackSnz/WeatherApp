package com.example.weatherapp.data.weather

import android.util.Log
import com.example.weatherapp.data.weather.database.DailyWeatherData
import com.example.weatherapp.data.weather.database.HourlyForecastData
import com.example.weatherapp.data.weather.responses.CurrentWeatherResponse
import com.example.weatherapp.data.weather.responses.WeatherForecastResponse
import com.example.weatherapp.network.WeatherApiService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface RemoteWeatherDataSource {
    suspend fun fetchWeatherData(latitude: Double, longitude: Double): DailyWeatherData?
    suspend fun fetchWeatherHourlyForecast(latitude: Double, longitude: Double): List<HourlyForecastData>?
}

class RemoteWeatherDataSourceImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : RemoteWeatherDataSource {

    override suspend fun fetchWeatherData(latitude: Double, longitude: Double): DailyWeatherData? {
        Log.d("LoadingDebug", "invoke getWeatherData in Repository")
        return suspendCancellableCoroutine { continuation ->
            val call = weatherApiService.getCurrentWeatherData(latitude, longitude)
            call.enqueue(object : Callback<CurrentWeatherResponse> {
                override fun onResponse(
                    call: Call<CurrentWeatherResponse>,
                    response: Response<CurrentWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("LoadingDebug", "Response is successful")
                        continuation.resume(
                            response.body()?.let {

                                DailyWeatherData(
                                    id = 0,
                                    currentTemperature = it.main.temp,
                                    minTemperatureToday = it.main.temp_min,
                                    maxTemperatureToday = it.main.temp_max,
                                    feelsLikeTemperature = it.main.feels_like,
                                    weatherDescription = it.weather[0].description,
                                    iconCode = it.weather[0].icon,
                                    humidity = it.main.humidity,
                                    windSpeed = it.wind.speed,
                                    windDegrees = it.wind.deg,
                                    windGust = it.wind.gust,
                                    pressure = it.main.pressure,
                                    lastUpdated = System.currentTimeMillis(),
                                    cityName = it.name
                                )
                            }
                        )
                    } else {
                        Log.d("LoadingDebug", "Response is not successful")
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                    Log.d("LoadingDebug", "Response is failed")
                    if (continuation.isActive) {
                        continuation.resumeWithException(t)
                    }
                }
            })

            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }

    override suspend fun fetchWeatherHourlyForecast(
        latitude: Double,
        longitude: Double
    ): List<HourlyForecastData>? {
        Log.d("LoadingDebug", "invoke getWeatherHourlyForecast in Repository")
        val result = CompletableDeferred<List<HourlyForecastData>?>()
        val call = weatherApiService.getHourlyWeatherData(latitude, longitude)
        call.enqueue(object : Callback<WeatherForecastResponse> {
            override fun onResponse(
                call: Call<WeatherForecastResponse>,
                response: Response<WeatherForecastResponse>
            ) {
                if (response.isSuccessful) {
                    val hourlyForecastList = mutableListOf<HourlyForecastData>()
                    with(response.body()) {
                        this?.list?.take(9)?.forEachIndexed { index, item ->
                            hourlyForecastList.add(
                                HourlyForecastData(
                                    id = index,
                                    temperature = item.main.temp,
                                    iconCode = item.weather[0].icon,
                                    precipitation = item.pop,
                                    time = item.dt,
                                    lastUpdated = System.currentTimeMillis()
                                )
                            )
                        }
                        result.complete(hourlyForecastList.toList())
                    }
                } else {
                    result.complete(null)
                }
            }

            override fun onFailure(call: Call<WeatherForecastResponse>, t: Throwable) {
                result.complete(null)
            }
        })
        return result.await()
    }
}