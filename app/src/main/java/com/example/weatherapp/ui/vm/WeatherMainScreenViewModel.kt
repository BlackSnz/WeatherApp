package com.example.weatherapp.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationInfo
import com.example.weatherapp.data.location.LocationResult
import com.example.weatherapp.data.weather.HourlyForecastUnit
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.data.weather.WeatherUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
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

    private val _weatherHourlyData = MutableLiveData<MutableList<HourlyForecastUnit>?>()
    val weatherHourlyData: LiveData<MutableList<HourlyForecastUnit>?> = _weatherHourlyData

    // Function for all async operation which fetch necessary information about the main weather parameters
    // First, load the information about the location, then use received information (latitude, longitude)
    // get weather data from the server.
    fun getWeatherInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("LoadingDebug", "invoke getWeatherInformation in VM")
            _weatherDataUiState.postValue(WeatherDataUiState.Loading)
            Log.d("LoadingDebug", "Change weatherDataUiState to Loading")
            when (val locationResult = locationRepository.getCurrentLocation()) {
                is LocationResult.Error -> {
                    Log.d("LoadingDebug", "Change weatherDataUiState to Error")
                    _weatherDataUiState.postValue(WeatherDataUiState.Error(locationResult.message))
                }

                is LocationResult.OnlyCoordinates -> {
                    Log.d("LoadingDebug", "Change weatherDataUiState to Success (Only Coords)")
                    _locationData.postValue(
                        LocationInfo(
                            locationResult.data.latitude,
                            locationResult.data.longitude,
                            null,
                            null
                        )
                    )
                    val weatherData = getWeatherData(locationResult.data.latitude, locationResult.data.longitude)
                    Log.d("LoadingDebug", "Weather data: ${weatherData}")
                    if(weatherData != null) {
                        Log.d("LoadingDebug", "Change weatherDataUiState to Success")
                        _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherData))
                    } else {
                        _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get weather data"))
                    }
                    getHourlyForecastData(locationResult.data.latitude, locationResult.data.longitude)

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
                    val weatherData = getWeatherData(locationResult.data.latitude, locationResult.data.longitude)
                    Log.d("LoadingDebug", "Weather data: ${weatherData}")
                    if(weatherData != null) {
                        Log.d("LoadingDebug", "Change weatherDataUiState to Success")
                        _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherData))
                    } else {
                        _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get weather data"))
                    }
                    getHourlyForecastData(locationResult.data.latitude, locationResult.data.longitude)


                }
            }
        }
    }

    // Transform data from the repository to the ui layer data
    private suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherUiData? {
        Log.d("LoadingDebug", "invoke getWeatherData in VM")
        val result = weatherDataRepository.getWeatherData(latitude, longitude)
        Log.d("LoadingDebug", "Get weather data from repository. Result: ${result}")
        val weatherUiData = CompletableDeferred<WeatherUiData?>()
        if (result != null) {
            Log.d("LoadingDebug", "Weather data is not null")
            weatherUiData.complete(
                WeatherUiData(
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
            )
        } else {
            Log.d("LoadingDebug", "Weather data is null")
            weatherUiData.complete(null)
        }
        return weatherUiData.await()
    }

    private suspend fun getHourlyForecastData(latitude: Double, longitude: Double) {
        val hourlyForecastResponse = weatherDataRepository.getWeatherHourlyForecast(latitude, longitude)
        if (hourlyForecastResponse != null) {
        val resultList: MutableList<HourlyForecastUnit> = mutableListOf()
        hourlyForecastResponse.list.take(9).forEach { item ->
            val hourlyForecastUnit = HourlyForecastUnit(
                temperature = item.main.temp.toString(),
                iconCode = item.weather[0].icon,
                precipitation = item.pop.toString(),
                time = item.dt.toString()
            )
            resultList.add(hourlyForecastUnit)
            }
            _weatherHourlyData.postValue(resultList)
        } else {
            _weatherHourlyData.postValue(null)
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



