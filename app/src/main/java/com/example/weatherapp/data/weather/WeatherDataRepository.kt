package com.example.weatherapp.data.weather

import android.util.Log
import com.example.weatherapp.network.WeatherApiService
import com.example.weatherapp.ui.vm.WeatherDataUiState
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

interface WeatherDataRepository {
    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherResponse?
}

class NetworkWeatherDataRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherDataRepository {

    override suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherResponse? {
        val result = CompletableDeferred<WeatherResponse?>()
        val call = weatherApiService.getWeatherData(latitude, longitude)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    result.complete(response.body())
                } else {
                    result.complete(response.body())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Weather", "Request failed: ${t.message}")
                result.complete(null)
            }
        })
        return result.await()
    }

}