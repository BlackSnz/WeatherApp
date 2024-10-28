package com.example.weatherapp.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationInfo
import com.example.weatherapp.data.location.LocationResult
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.data.weather.WeatherUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

sealed interface WeatherDataUiState {
    object Loading : WeatherDataUiState
    data class Success(val data: WeatherUiData) : WeatherDataUiState
    data class Error(val errorMessage: String) : WeatherDataUiState
}

@HiltViewModel
class WeatherMainScreenViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
    private val locationRepository: LocationDataRepository
) : ViewModel() {

    private val _weatherDataUiState = MutableLiveData<WeatherDataUiState>()
    val weatherDataUiState: LiveData<WeatherDataUiState> = _weatherDataUiState

    private val _locationData = MutableLiveData<LocationInfo>()
    val locationData: LiveData<LocationInfo> = _locationData

    fun getWeatherInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherDataUiState.postValue(WeatherDataUiState.Loading)
            when(val locationResult = locationRepository.getCurrentLocation()) {
                is LocationResult.Error -> {
                    _weatherDataUiState.postValue(WeatherDataUiState.Error(locationResult.message))
                }
                is LocationResult.OnlyCoordinates -> {
                    _locationData.postValue(
                        LocationInfo(
                            locationResult.data.latitude,
                            locationResult.data.longitude,
                            null,
                            null
                        )
                    )
                    getWeatherData(locationResult.data.latitude, locationResult.data.longitude)
                }
                is LocationResult.Success -> {
                    _locationData.postValue(
                        LocationInfo(
                            locationResult.data.latitude,
                            locationResult.data.longitude,
                            locationResult.data.city,
                            locationResult.data.country
                        )
                    )
                    getWeatherData(locationResult.data.latitude, locationResult.data.longitude)
                }
            }
        }
    }

    private suspend fun getWeatherData(latitude: Double, longitude: Double) {
        val result = weatherDataRepository.getWeatherData(latitude, longitude)
        if (result != null) {
            val weatherUiData = WeatherUiData(
                currentTemperature = result.main.temp.toString(),
                minTemperatureToday = result.main.temp_min.toString(),
                maxTemperatureToday = result.main.temp_max.toString(),
                feelsLikeTemperature = result.main.feels_like.toString(),
                weatherDescription = result.weather[0].description.replaceFirstChar { char -> char.uppercase() },
                humidity = result.main.humidity.toString(),
                windSpeed = result.wind.speed.roundToInt().toString(),
                pressure = result.main.pressure.toString(),
                windDegrees = getWindDirectionCode(result.wind.deg) ?: "n",
                windGust = result.wind.gust.toString(),
                iconCode = result.weather[0].icon
            )
            _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherUiData))
        } else {
            _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get response from server"))
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



