package com.example.weatherapp.ui.vm

import android.content.Context
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.data.weather.WeatherResponse
import com.example.weatherapp.data.weather.WeatherUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

sealed interface WeatherDataUiState {
    object Loading : WeatherDataUiState
    data class Success(val data: WeatherUiData) : WeatherDataUiState
    data class Error(val errorMessage: String) : WeatherDataUiState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    private val _weatherDataUiState = MutableLiveData<WeatherDataUiState>()
    val weatherDataUiState: LiveData<WeatherDataUiState> = _weatherDataUiState

    fun getWeatherData(latitude: Double, longitude: Double) {
        _weatherDataUiState.postValue(WeatherDataUiState.Loading)
        weatherDataRepository.getWeatherData(latitude, longitude) { weatherResponse ->
            if (weatherResponse != null) {
                val weatherUiData = WeatherUiData(
                    currentTemperature = weatherResponse.main.temp.toString(),
                    minTemperatureToday = weatherResponse.main.temp_min.toString(),
                    maxTemperatureToday = weatherResponse.main.temp_max.toString(),
                    feelsLikeTemperature = weatherResponse.main.feels_like.toString(),
                    weatherDescription = weatherResponse.weather[0].description.replaceFirstChar { char -> char.uppercase() },
                    humidity = weatherResponse.main.humidity.toString(),
                    windSpeed = weatherResponse.wind.speed.roundToInt().toString(),
                    pressure = weatherResponse.main.pressure.toString(),
                    windDegrees = getWindDirectionCode(weatherResponse.wind.deg) ?: "n",
                    windGust = weatherResponse.wind.gust.toString(),
                    iconCode = weatherResponse.weather[0].icon
                )
                _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherUiData))
            } else {
                _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get response from server"))
            }
        }
    }

    private fun getWindDirectionCode(windDegrees: Int): String? {
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
}



