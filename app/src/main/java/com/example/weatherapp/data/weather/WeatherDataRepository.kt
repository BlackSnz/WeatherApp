package com.example.weatherapp.data.weather

import android.util.Log
import com.example.weatherapp.network.WeatherApiService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface WeatherDataRepository {
    suspend fun getWeatherData(latitude: Double, longitude: Double): CurrentWeatherResponse?
    suspend fun getWeatherHourlyForecast(latitude: Double, longitude: Double) : WeatherForecastResponse?
}

class NetworkWeatherDataRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherDataRepository {

    override suspend fun getWeatherData(latitude: Double, longitude: Double): CurrentWeatherResponse? {
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
                        continuation.resume(response.body())
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

    override suspend fun getWeatherHourlyForecast(latitude: Double, longitude: Double): WeatherForecastResponse? {
        Log.d("LoadingDebug", "invoke getWeatherHourlyForecast in Repository")
        val result = CompletableDeferred<WeatherForecastResponse?>()
        val call = weatherApiService.getHourlyWeatherData(latitude, longitude)
        call.enqueue(object : Callback<WeatherForecastResponse> {
            override fun onResponse(
                call: Call<WeatherForecastResponse>,
                response: Response<WeatherForecastResponse>
            ) {
                if(response.isSuccessful){
                    result.complete(response.body())
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