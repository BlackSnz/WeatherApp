package com.example.myapplication.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.WeatherViewModel

@Composable
fun WeatherApp() {
    Scaffold(modifier = Modifier) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val weatherViewModel: WeatherViewModel =
                viewModel(factory = WeatherViewModel.Factory)
            HomeScreen(weatherUiState = weatherViewModel.weatherUiState, contentPadding = it)
        }
    }
}