package com.example.weatherapp.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationInfo
import com.example.weatherapp.data.location.LocationResult
import com.example.weatherapp.data.weather.HourlyForecastUnit
import com.example.weatherapp.data.weather.WeatherRepository
import com.example.weatherapp.data.weather.WeatherUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationDataRepository
) : ViewModel() {

    private val _weatherDataUiState = MutableLiveData<WeatherDataUiState>()
    val weatherDataUiState: LiveData<WeatherDataUiState> = _weatherDataUiState

    private val _locationData = MutableLiveData<LocationInfo>()
    val locationData: LiveData<LocationInfo> = _locationData

    private val _weatherHourlyData = MutableLiveData<MutableList<HourlyForecastUnit>?>()
    val weatherHourlyData: LiveData<MutableList<HourlyForecastUnit>?> = _weatherHourlyData

    private var weatherLoadingJob: Job? = null

    // Function for all async operation which fetch necessary information about the main weather parameters
    // First, load the information about the location, then use received information (latitude, longitude)
    // get weather data from the server.
    fun getWeatherInformation() {
        weatherLoadingJob?.cancel()
        weatherLoadingJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("LoadingDebug", "invoke getWeatherInformation in VM")
            _weatherDataUiState.postValue(WeatherDataUiState.Loading)
            Log.d("LoadingDebug", "Change weatherDataUiState to Loading")
            try {
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
                        val weatherData = getWeatherData(
                            locationResult.data.latitude,
                            locationResult.data.longitude
                        )
                        getHourlyForecastData(
                            locationResult.data.latitude,
                            locationResult.data.longitude
                        )
                        Log.d("LoadingDebug", "Weather data: ${weatherData}")
                        if (weatherData != null) {
                            Log.d("LoadingDebug", "Change weatherDataUiState to Success")
                            _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherData))
                        } else {
                            _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get weather data"))
                        }

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
                        val weatherData = getWeatherData(
                            locationResult.data.latitude,
                            locationResult.data.longitude
                        )
                        getHourlyForecastData(
                            locationResult.data.latitude,
                            locationResult.data.longitude
                        )
                        Log.d("LoadingDebug", "Weather data: ${weatherData}")
                        if (weatherData != null) {
                            Log.d("LoadingDebug", "Change weatherDataUiState to Success")
                            _weatherDataUiState.postValue(WeatherDataUiState.Success(weatherData))
                        } else {
                            _weatherDataUiState.postValue(WeatherDataUiState.Error("Can't get weather data"))
                        }
                    }
                }
            } catch (e: CancellationException) {
                Log.d("CancelLoading", "Cancellation weatherLoading Job")
            }
        }
    }

    // Transform data from the repository to the ui layer data
    private suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherUiData? {
        Log.d("LoadingDebug", "invoke getWeatherData in VM")
        val weatherData = weatherRepository.getDailyWeatherData(latitude, longitude)
        Log.d("LoadingDebug", "Get weather data from repository. Result: ${weatherRepository}")
        val weatherUiData = CompletableDeferred<WeatherUiData?>()
        if (weatherData != null) {
            Log.d("LoadingDebug", "Weather data is not null")
            weatherUiData.complete(
                WeatherUiData(
                    currentTemperature = weatherData.currentTemperature.toString(),
                    minTemperatureToday = weatherData.minTemperatureToday.toString(),
                    maxTemperatureToday = weatherData.maxTemperatureToday.toString(),
                    feelsLikeTemperature = weatherData.feelsLikeTemperature.toString(),
                    weatherDescription = weatherData.weatherDescription.replaceFirstChar { char -> char.uppercase() },
                    humidity = weatherData.humidity.toString(),
                    windSpeed = weatherData.windSpeed.roundToInt().toString(),
                    pressure = weatherData.pressure.toString(),
                    windDegrees = weatherData.windDegrees.toString(),
                    windGust = weatherData.windGust.toString(),
                    iconCode = weatherData.iconCode
                )
            )
    } else {
        Log.d("LoadingDebug", "Weather data is null")
        weatherUiData.complete(null)
    }
    return weatherUiData.await()
}

private suspend fun getHourlyForecastData(latitude: Double, longitude: Double) {
    val hourlyForecastData =
        weatherRepository.getHourlyWeatherForecastData(latitude, longitude)
    if (hourlyForecastData != null) {
        val resultList: MutableList<HourlyForecastUnit> = mutableListOf()
        hourlyForecastData.forEach { item ->
            val hourlyForecastUnit = HourlyForecastUnit(
                temperature = item.temperature.toString(),
                iconCode = item.iconCode,
                precipitation = item.precipitation.toString(),
                time = item.time.toString()
            )
            resultList.add(hourlyForecastUnit)
        }
        _weatherHourlyData.postValue(resultList)
    } else {
        _weatherHourlyData.postValue(null)
    }
}

fun setDirectionIcon(currentDirection: String): Int {
    when (currentDirection) {
        "n" -> return R.drawable.n_direction
        "s" -> return R.drawable.s_direction
        "e" -> return R.drawable.e_direction
        "w" -> return R.drawable.w_direction
        "ne" -> return R.drawable.ne_direction
        "nw" -> return R.drawable.nw_direction
        "se" -> return R.drawable.se_direction
        "sw" -> return R.drawable.sw_direction
        else -> {
            Log.e("WeatherCardView", "Unknown direction: $currentDirection")
            return R.drawable.n_direction
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



