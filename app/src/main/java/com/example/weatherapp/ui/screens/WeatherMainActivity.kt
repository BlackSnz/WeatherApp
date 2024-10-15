package com.example.weatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.weatherapp.ui.LocationViewModel
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.screens.SettingsActivity

class WeatherMainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.weather_main)

        // Get last known location from the Google Play Service
        locationViewModel.getLastKnownLocation(this, LOCATION_PERMISSION_REQUEST_CODE)
        locationViewModel.currentLocation.observe(this, Observer { location ->
            weatherViewModel.getWeatherData(
                locationViewModel.currentLocation.value!!.latitude.toBigDecimal()
                    .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble(),
                locationViewModel.currentLocation.value!!.longitude.toBigDecimal()
                    .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble(),
                this
            )
        })

        // Values for the current weather
        val weatherTemperatureText = findViewById<TextView>(R.id.tvTemperature)
        val weatherIcon = findViewById<ImageView>(R.id.ivWeatherIcon)
        val humidityText = findViewById<TextView>(R.id.tvHumidityInfo)
        val windText = findViewById<TextView>(R.id.tvWindInfo)
        val pressureText = findViewById<TextView>(R.id.tvPressureInfo)

        // Values for the current location
        val cityText = findViewById<TextView>(R.id.tvCity)

        // Values for the settings
        val settingsIcon = findViewById<ImageView>(R.id.ivSettings)
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        // Observers for the weatherViewModel
        weatherViewModel.weatherData.observe(this, Observer {
            weatherViewModel.updateWeatherUi(this)
        })

        weatherViewModel.weatherUi.observe(this, Observer { weather ->
            weather?.let {
                weatherTemperatureText.text = this.getString(
                    R.string.current_temperature,
                    it.temperature
                )
                weatherIcon.setImageDrawable(it.icon)
                humidityText.text = this.getString(R.string.current_humidity, it.humidity)
                windText.text = this.getString(R.string.current_wind, it.wind)
                pressureText.text = this.getString(R.string.current_pressure, it.pressure)

            }
        })

        weatherViewModel.currentWeatherIcon.observe(this, Observer { drawable ->
            drawable?.let {
                weatherIcon.setImageDrawable(it)
            }
        })

        // Observers for the locationViewModel
        locationViewModel.currentLocation.observe(this, Observer { location ->
            location?.let {
                cityText.text = it.city
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationViewModel.getCurrentLocation(this)
            } else {
                Toast.makeText(
                    this,
                    "Для загрузки погоды необходимо разрешить доступ к местоположению",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}