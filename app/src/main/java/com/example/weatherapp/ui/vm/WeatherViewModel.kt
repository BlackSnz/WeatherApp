package com.example.weatherapp.ui.vm

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
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.data.weather.WeatherResponse
import com.example.weatherapp.data.weather.WeatherUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.roundToInt

private const val BASE_ICON_URL = "https://openweathermap.org/img/wn/"
private const val ICON_URL_POSTFIX = "@2x.png"

sealed interface WeatherUpdateUi {
    object Success : WeatherUpdateUi
    object Error : WeatherUpdateUi
    object Loading : WeatherUpdateUi
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

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

            val currentTemperature = getCurrentTemperature(_weatherData.value?.main?.temp, context)
            val minCurrentTemperature =
                getCurrentTemperature(_weatherData.value?.main?.temp_min, context)
            val maxCurrentTemperature =
                getCurrentTemperature(_weatherData.value?.main?.temp_max, context)
            val feelsLikeTemperature =
                getCurrentTemperature(_weatherData.value?.main?.feels_like, context)
            val weatherDescription =
                _weatherData.value?.weather?.get(0)?.description?.replaceFirstChar { it.uppercase() }
                    ?: ""
            val humidity = _weatherData.value?.main?.humidity.toString()
            val windSpeed = _weatherData.value?.wind?.speed?.roundToInt().toString()
            val icon = _currentWeatherIcon.value ?: AppCompatResources.getDrawable(
                context,
                R.drawable.weather_icon
            )!!
            val windDegrees = getWindDirectionCode() ?: "n"
            val windGust = _weatherData.value?.wind?.gust.toString()

            val pressure = getCurrentPressure(context)

            _weatherUi.value = WeatherUi(
                currentTemperature = currentTemperature,
                minTemperatureToday = minCurrentTemperature,
                maxTemperatureToday = maxCurrentTemperature,
                feelsLikeTemperature = feelsLikeTemperature,
                weatherDescription = weatherDescription,
                icon = icon,
                humidity = humidity,
                windSpeed = windSpeed,
                pressure = pressure,
                windDegrees = windDegrees,
                windGust = windGust
            )
            weatherUpdateUi.value = WeatherUpdateUi.Success
        }
    }

    private fun getWindDirectionCode(): String? {
        val windDegrees = _weatherData.value?.wind?.deg
        Log.d("WindDirectionCode", "$windDegrees")
        return when (windDegrees) {
            in 0..44 -> "n"
            in 45..89 -> "ne"
            in 90..134 -> "e"
            in 135..179 -> "se"
            in 180..224 -> "s"
            in 225..269 -> "sw"
            in 270..314 -> "w"
            in 315..359 -> "nw"
            360 -> "n"
            else -> {
                Log.d("WeatherViewModel", "Unknown wind direction: $windDegrees")
                return null
            }
        }
    }

    // Function to receive data from the OpenWeatherApi server
    fun getWeatherData(latitude: Double, longitude: Double, context: Context) {
        val call = weatherDataRepository.getWeatherData(latitude, longitude)
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

    private fun getCurrentTemperature(temperature: Double?, context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (sharedPreferences.getString("temperature_unit", "Celsius")) {
            "Celsius" -> {
                temperature?.minus(272.15)?.roundToInt()?.toString() ?: return "?"
            }

            "Fahrenheit" -> {
                temperature?.minus(272.15)?.times(9 / 5)?.plus(32)?.roundToInt()?.toString()
                    ?: return "?"
            }

            else -> throw IllegalArgumentException("Unsupported temperature unit")
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



