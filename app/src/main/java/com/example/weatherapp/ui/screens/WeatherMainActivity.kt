package com.example.weatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherapp.databinding.WeatherMainBinding
import com.example.weatherapp.ui.LocationViewModel
import com.example.weatherapp.ui.WeatherUpdateUi
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.screens.SettingsActivity

class WeatherMainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var binding: WeatherMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private val locationViewModel: LocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        weatherViewModel = ViewModelProvider(this, WeatherViewModel.Factory)[WeatherViewModel::class.java]

        // Values for the current location
        val cityText = binding.tvCity

        // Values for the current weather
        val weatherTemperatureText = binding.tvTemperature
        val weatherIcon = binding.ivWeatherIcon
        val humidityText = binding.tvHumidityInfo
        val windText = binding.tvWindInfo
        val pressureText = binding.tvPressureInfo
        val minWeatherTemperatureText = binding.tvMinTemperatureToday
        val maxWeatherTemperatureText = binding.tvMaxTemperatureToday
        val feelsLikeTemperatureText = binding.tvFeelsLikeToday
        val weatherDescriptionText = binding.tvWeatherDescription

        // Values for the wind information
        val windCard = binding.windCardView

        // Values for the settings
        val settingsIcon = binding.ivSettings
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Get current location
        locationViewModel.getCurrentLocation(this)

        // Observers for the locationViewModel
        locationViewModel.currentLocation.observe(this) { location ->
            weatherViewModel.getWeatherData(
                locationViewModel.currentLocation.value!!.latitude,
                locationViewModel.currentLocation.value!!.longitude,
                this
            )

            location?.let {
                cityText.text = it.city
            }
        }

        // Observers for the weatherViewModel
        weatherViewModel.weatherUpdateUi.observe(this) {
            when(weatherViewModel.weatherUpdateUi.value) {
                WeatherUpdateUi.Loading -> {
                }
                WeatherUpdateUi.Success -> {
                    weatherViewModel.weatherUi.value.let {
                        weatherIcon.setImageDrawable(it?.icon)
                        humidityText.text = this.getString(R.string.current_humidity, it?.humidity)
                        windText.text = this.getString(R.string.current_wind, it?.windSpeed)
                        weatherDescriptionText.text = it?.weatherDescription
                        // Checking the temperature unit and set text for the temperature
                        val temperatureUnit =
                            sharedPreferences.getString("temperature_unit", "Celsius")
                        when (temperatureUnit) {
                            "Celsius" -> {
                                weatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_temperature_celcius,
                                        it?.currentTemperature
                                    )
                                minWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_min_temperature_celcuis,
                                        it?.minTemperatureToday
                                    )
                                maxWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_max_temperature_celcuis,
                                        it?.maxTemperatureToday
                                    )
                                feelsLikeTemperatureText.text =
                                    this.getString(
                                        R.string.current_feels_like_tempertature_celcius, it?.feelsLikeTemperature
                                    )
                            }

                            "Fahrenheit" -> {
                                weatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_temperature_fahrenheit,
                                        it?.currentTemperature
                                    )
                                minWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_min_temperature_fahrenheit,
                                        it?.minTemperatureToday
                                    )
                                maxWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_max_temperature_fahrenheit,
                                        it?.maxTemperatureToday
                                    )
                                feelsLikeTemperatureText.text =
                                    this.getString(
                                        R.string.current_feels_like_tempertature_fahrenheit, it?.feelsLikeTemperature
                                    )
                            }

                            else -> {}
                        }

                        // Checking the pressure unit and set text for the pressure
                        when (sharedPreferences.getString("pressure_unit", "mmHg")) {
                            "hPa" -> {
                                pressureText.text =
                                    this.getString(R.string.current_pressure_hpa, it?.pressure)
                            }

                            "mmHg" -> {
                                pressureText.text =
                                    this.getString(R.string.current_pressure_mmHg, it?.pressure)

                            }
                        }
                        // Update information about wind
                        windCard.updateWindCard(speed = it?.windSpeed.toString(), "m/s", direction = it?.windDegrees.toString())
                    }
                }
                else -> { }
            }
        }

        weatherViewModel.currentWeatherIcon.observe(this) { drawable ->
            drawable?.let {
                weatherIcon.setImageDrawable(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.updateWeatherUi(this)
    }

    override fun onPause() {
        super.onPause()

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