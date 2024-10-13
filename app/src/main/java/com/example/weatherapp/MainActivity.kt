package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.weatherapp.ui.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        weatherViewModel.getWeatherData(55.7522, 37.6156, this)
        weatherViewModel.getWeatherIcon(this)

        val weatherTemperature = findViewById<TextView>(R.id.temperature)
        val weatherIcon = findViewById<ImageView>(R.id.weatherIcon)

        weatherViewModel.weatherData.observe(this, Observer { weather ->
            weather?.let {
                weatherTemperature.text = this.getString(
                    R.string.current_temperature,
                    weatherViewModel.getCurrentTemperature()
                )
            }
            weatherViewModel.getWeatherIcon(this)
        })

        weatherViewModel.currentWeatherIcon.observe(this, Observer { drawable ->
            drawable?.let {
                weatherIcon.setImageDrawable(it)
            }
        })
    }
}