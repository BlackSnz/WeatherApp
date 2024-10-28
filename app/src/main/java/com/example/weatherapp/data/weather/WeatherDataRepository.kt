package com.example.weatherapp.data.weather

import android.util.Log
import com.example.weatherapp.network.WeatherApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

interface WeatherDataRepository {
    fun getWeatherData(latitude: Double, longitude: Double, callback: (WeatherResponse?) -> Unit)
}

class NetworkWeatherDataRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherDataRepository {

    override fun getWeatherData(latitude: Double, longitude: Double, callback: (WeatherResponse?) -> Unit) {
        val call = weatherApiService.getWeatherData(latitude, longitude)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    callback(
                        response.body()
                    )
                } else {
                    callback(null)
                    Log.e("Weather", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Weather", "Request failed: ${t.message}")
                callback(null)
            }
        })
    }

}