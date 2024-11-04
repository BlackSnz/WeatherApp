package com.example.weatherapp.ui.test

import com.example.weatherapp.data.location.LocationDataSource
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.weather.WeatherRepository
import com.example.weatherapp.ui.vm.WeatherMainScreenViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class WeatherMainScreenViewModelTest {
    private lateinit var viewModel: WeatherMainScreenViewModel
    private val mockWeatherRepository = mock(WeatherRepository::class.java)
    private val mockLocationRepository = mock(LocationRepository::class.java)

    @Before
    fun setUp(){
        viewModel = WeatherMainScreenViewModel(mockWeatherRepository, mockLocationRepository)
    }

    @Test
    fun weatherMainScreenViewModel_getWeatherData_WeatherDataError(){

    }
}