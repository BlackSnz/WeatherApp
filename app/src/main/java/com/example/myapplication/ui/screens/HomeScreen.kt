package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.WeatherData

@Composable
fun HomeScreen(
    weatherUiState: WeatherUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    when(weatherUiState) {
        is WeatherUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is WeatherUiState.Error -> ErrorScreen(modifier)
        is WeatherUiState.Success -> WeatherScreen(weatherUiState.weatherInfo, modifier)
    }
}

@Composable
fun WeatherScreen(weatherInfo: WeatherData, modifier: Modifier) {
    val currentDayTemperature = weatherInfo.currentDayWeatherTemperature
    val currentDayWindType = weatherInfo.currentDayWindType
    val currentDayWindSpeed = weatherInfo.currentDayWindSpeed
    val currentDayAtmospherePressure = weatherInfo.currentDayAtmospherePressure
    val currentDayHumidity = weatherInfo.currentDayHumidity

    Card(modifier = modifier.fillMaxWidth()) {
        Row(

        ) {
            Text(text = currentDayTemperature)
        }
        Spacer(modifier = modifier)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Ветер:")
                Text(text = currentDayWindType)
            }
            Spacer(modifier = modifier)
            Row {
                Text(text = "Скорость ветра:")
                Text(text = currentDayWindSpeed)
            }
            Spacer(modifier = modifier)
            Row {
                Text(text = "Давление:")
                Text(text = currentDayAtmospherePressure)
            }
            Spacer(modifier = modifier)
            Row {
                Text(text = "Влажность:")
                Text(text = currentDayHumidity)
            }

        }
    }
}

@Composable
fun ErrorScreen(modifier: Modifier) {
    Text(text = "Can't load information about weather")
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Text(text = "Loading data")
}

@Preview
@Composable
fun WeatherScreenPreview() {
    WeatherScreen(weatherInfo = WeatherData(), modifier = Modifier)
}