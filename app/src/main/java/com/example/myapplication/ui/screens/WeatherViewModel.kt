package com.example.myapplication.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.WeatherApplication
import com.example.myapplication.data.WeatherRepository
import com.example.myapplication.model.WeatherData
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface WeatherUiState {
    data class Success(val weatherInfo: WeatherData): WeatherUiState
    data object Loading: WeatherUiState
    data object Error: WeatherUiState
}

class WeatherViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

    var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)
        private set

    init {
        getWeatherInfo()
    }

    private fun getWeatherInfo() {
        viewModelScope.launch {
            weatherUiState = WeatherUiState.Loading
            weatherUiState = try {
                WeatherUiState.Success(weatherRepository.getWeatherData())
            } catch (e: IOException) {
                WeatherUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WeatherApplication)
                val weatherRepository = application.container.weatherRepository
                WeatherViewModel(weatherRepository = weatherRepository)
            }
        }
    }
}