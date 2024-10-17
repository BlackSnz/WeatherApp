package com.example.weatherapp.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.WeatherSettings
import com.example.weatherapp.data.weather.WeatherResponse
import com.example.weatherapp.data.weather.WeatherUi
import com.example.weatherapp.network.WeatherApi
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

private const val BASE_ICON_URL = "https://openweathermap.org/img/wn/"
private const val ICON_URL_POSTFIX = "@2x.png"

sealed interface WeatherUpdateUi {
    object Success : WeatherUpdateUi
    object Error : WeatherUpdateUi
    object Loading : WeatherUpdateUi
}

sealed interface WeatherDetailsStatus {
    object Red : WeatherDetailsStatus
    object Yellow : WeatherDetailsStatus
    object Green : WeatherDetailsStatus
}

class WeatherViewModel : ViewModel() {

    var weatherUpdateUi = MutableLiveData<WeatherUpdateUi>(WeatherUpdateUi.Loading)
        private set

    // Properties for saving weatherData state
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    // Properties for saving currentWeatherIcon
    private val _currentWeatherIcon = MutableLiveData<Drawable?>()
    val currentWeatherIcon: LiveData<Drawable?> = _currentWeatherIcon

    // Properties for saving weather main screen Ui elements state
    private val _weatherUi = MutableLiveData<WeatherUi>()
    val weatherUi: LiveData<WeatherUi> = _weatherUi

    fun updateWeatherUi(context: Context) {
        weatherUpdateUi.value = WeatherUpdateUi.Loading
        viewModelScope.launch {
            val getWeatherIconJob = launch {
                getWeatherIcon(context)
            }
            getWeatherIconJob.join()

            val temperature = getCurrentTemperature(context)
            val pressure = getCurrentPressure(context)

            _weatherUi.value = WeatherUi(
                temperature = temperature,
                icon = _currentWeatherIcon.value ?: AppCompatResources.getDrawable(
                    context,
                    R.drawable.weather_icon
                )!!,
                humidity = _weatherData.value?.main?.humidity.toString(),
                wind = _weatherData.value?.wind?.speed.toString(),
                pressure = pressure
            )
            weatherUpdateUi.value = WeatherUpdateUi.Success
        }
    }

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
                        updateWeatherUi(context)
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

    private fun getCurrentTemperature(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (sharedPreferences.getString("temperature_unit", "Celcius")) {
            "Celcius" -> _weatherData.value?.main?.temp?.minus(272.15)?.roundToInt().toString()
            "Fahrenheit" -> _weatherData.value?.main?.temp?.minus(272.15)?.times(9 / 5)?.plus(32)
                ?.roundToInt().toString()

            else -> {
                _weatherData.value?.main?.temp?.minus(272.15)?.roundToInt().toString()
            }
        }
    }

    private fun getCurrentPressure(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (sharedPreferences.getString("pressure_unit", "mmHg")) {
            "mmHg" -> _weatherData.value?.main?.pressure?.times(0.75006)?.roundToInt().toString()
            else -> {
                _weatherData.value?.main?.pressure.toString()
            }
        }
    }
}
