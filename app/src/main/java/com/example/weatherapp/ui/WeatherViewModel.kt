package com.example.weatherapp.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.data.WeatherUi
import com.example.weatherapp.network.WeatherApi
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

private const val BASE_ICON_URL = "https://openweathermap.org/img/wn/"
private const val ICON_URL_POSTFIX = "@2x.png"

sealed interface WeatherDataLoadState {
    object Success : WeatherDataLoadState
    object Error : WeatherDataLoadState
    object Loading : WeatherDataLoadState
}

sealed interface WeatherDetailsStatus {
    object Red : WeatherDetailsStatus
    object Yellow : WeatherDetailsStatus
    object Green : WeatherDetailsStatus
}

class WeatherViewModel : ViewModel() {

    var weatherDataLoadState = MutableLiveData<WeatherDataLoadState>(WeatherDataLoadState.Loading)
        private set

    // Properties for saving weatherData state
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    // Properties for saving currentWeatherIcon
    private val _currentWeatherIcon = MutableLiveData<Drawable?>()
    val currentWeatherIcon: LiveData<Drawable?> = _currentWeatherIcon

    private val _weatherUi = MutableLiveData<WeatherUi>()
    val weatherUi: LiveData<WeatherUi> = _weatherUi

    fun updateWeatherUi(context: Context) {
        viewModelScope.launch {
            val getWeatherIconJob = launch {
                getWeatherIcon(context)
            }
            getWeatherIconJob.join()

            if(_weatherUi.value == null) {
                _weatherUi.value = WeatherUi(
                    temperature = getCurrentTemperature(),
                    icon = _currentWeatherIcon.value ?: AppCompatResources.getDrawable(
                        context,
                        R.drawable.weather_icon
                    )!!,
                    humidity = _weatherData.value?.main?.humidity.toString(),
                    wind = _weatherData.value?.wind?.speed.toString(),
                    pressure = _weatherData.value?.main?.pressure.toString()
                )
            } else {
                _weatherUi.value = _weatherUi.value!!.copy(
                    temperature = getCurrentTemperature(),
                    icon = _currentWeatherIcon.value ?: AppCompatResources.getDrawable(
                        context,
                        R.drawable.weather_icon
                    )!!,
                    humidity = _weatherData.value?.main?.humidity.toString(),
                    wind = _weatherData.value?.wind?.speed.toString(),
                    pressure = _weatherData.value?.main?.pressure.toString()
                )
            }

        }
    }

    // Function to receive data from the OpenWeatherApi server with:
    // @param latitude - as latitude coordinates
    // @param longitude - as longitude coordinates
    // @param appid - your api key in the OpenWeatherApi, which as default is const val in WeatherApiService
    fun getWeatherData(latitude: Double, longitude: Double, context: Context) {
        viewModelScope.launch {
            weatherDataLoadState.value = WeatherDataLoadState.Loading
            val call = WeatherApi.retrofitService.getCurrentWeather(latitude, longitude)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        _weatherData.value = response.body()
                        updateWeatherUi(context)
                        WeatherDataLoadState.Success
                    } else {
                        weatherDataLoadState.value = WeatherDataLoadState.Error
                        Log.e("Weather", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    weatherDataLoadState.value = WeatherDataLoadState.Error
                    Log.e("Weather", "Request failed: ${t.message}")
                }
            })
        }
    }

    // Function to receive the icon image from the OpenWeatherApi server using the Glide library
    private fun getWeatherIcon(context: Context) {
        // Get the icon code from the LiveData - weatherData
        val iconCode = weatherData.value?.weather?.get(0)?.icon
        if (iconCode != null) {
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

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        _currentWeatherIcon.value =
                            AppCompatResources.getDrawable(context, R.drawable.weather_icon)
                    }
                })
        }
    }

    private fun getCurrentTemperature(): String {
        val currentTemperatureInCelcius =
            _weatherData.value?.main?.temp?.minus(272.15)?.roundToInt()
        return currentTemperatureInCelcius.toString()
    }
}
