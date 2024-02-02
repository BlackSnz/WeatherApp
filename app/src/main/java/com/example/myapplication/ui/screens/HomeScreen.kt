package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.WeatherData

@Composable
fun HomeScreen(
    weatherUiState: WeatherUiState,
    modifier: Modifier = Modifier,
) {
    when(weatherUiState) {
        is WeatherUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is WeatherUiState.Error -> ErrorScreen()
        is WeatherUiState.Success -> WeatherScreen(weatherUiState.weatherInfo)
    }
}

@Composable
fun WeatherScreen(weatherInfo: WeatherData, modifier: Modifier) {
    val test = weatherInfo.currentDayWeatherTemperature
    Text(text = test)
}

@Composable
fun ErrorScreen(modifier: Modifier) {
    Text(text = "Can't load information about weather")
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Text(text = "Loading data")
}
