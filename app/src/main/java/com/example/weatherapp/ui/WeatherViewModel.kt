package com.example.weatherapp.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.network.WeatherApi
import com.example.weatherapp.network.WeatherApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.math.roundToInt

private const val BASE_ICON_URL = "https://openweathermap.org/img/wn/"
private const val ICON_URL_POSTFIX = "@2x.png"

class WeatherViewModel : ViewModel() {

    // Properties for saving weatherData state
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    // Properties for saving currentWeatherIcon
    private val _currentWeatherIcon = MutableLiveData<Drawable?>()
    val currentWeatherIcon: LiveData<Drawable?> = _currentWeatherIcon

    // Function to receive data from the OpenWeatherApi server with:
    // @param latitude - as latitude coordinates
    // @param longitude - as longitude coordinates
    // @param appid - your api key in the OpenWeatherApi, which as default is const val in WeatherApiService
    fun getWeatherData(latitude: Double, longitude: Double, context: Context) {
        viewModelScope.launch {
            val call = WeatherApi.retrofitService.getCurrentWeather(latitude, longitude)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        _weatherData.value = response.body()
                    } else {
                        Log.e("Weather", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Weather", "Request failed: ${t.message}")
                }
            })
        }
    }

    // Function to receive the icon image from the OpenWeatherApi server using the Glide library
    fun getWeatherIcon(context: Context) {
        // Get the icon code from the LiveData - weatherData
        val iconCode = weatherData.value?.weather?.get(0)?.icon
        if(iconCode != null) {
            val iconUrl = "$BASE_ICON_URL$iconCode$ICON_URL_POSTFIX"

            Glide.with(context)
                .load(iconUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        _currentWeatherIcon.value = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    fun getCurrentTemperature(): String {
        val currentTemperatureInCelcius = _weatherData.value?.main?.temp?.minus(272.15)?.roundToInt()
        return currentTemperatureInCelcius.toString()
    }
}