package com.example.weatherapp.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherMainBinding
import com.example.weatherapp.ui.fragments.RequestLocationPermissionDialogFragment
import com.example.weatherapp.ui.vm.LocationState
import com.example.weatherapp.ui.vm.LocationViewModel
import com.example.weatherapp.ui.vm.WeatherUpdateUi
import com.example.weatherapp.ui.vm.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

const val LOCAL_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class WeatherMainActivity : AppCompatActivity(),
    RequestLocationPermissionDialogFragment.NoticeDialogListener {

    private lateinit var binding: WeatherMainBinding
    private val locationViewModel: LocationViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

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
        val progressBar = binding.pbWeatherDataLoading


        // Values for the wind information
        val windCard = binding.windCardView

        // Values for the settings
        val settingsIcon = binding.ivSettings
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Realise the workflow for requesting the location permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationViewModel.getCurrentLocation()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                RequestLocationPermissionDialogFragment().show(
                    supportFragmentManager,
                    "LocationPermissionAlert"
                )
            }

            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCAL_PERMISSION_REQUEST_CODE
                )
            }
        }


        // Observers for the locationViewModel
        locationViewModel.locationState.observe(this) { locationState ->
            when (locationState) {
                is LocationState.Error -> {
                    weatherViewModel.getWeatherData(
                        locationState.data.latitude,
                        locationState.data.longitude,
                        this
                    )
                    progressBar.visibility = View.INVISIBLE
                    cityText.visibility = View.VISIBLE
                    weatherTemperatureText.visibility = View.VISIBLE
                    weatherIcon.visibility = View.VISIBLE
                    minWeatherTemperatureText.visibility = View.VISIBLE
                    maxWeatherTemperatureText.visibility = View.VISIBLE
                    weatherDescriptionText.visibility = View.VISIBLE
                    feelsLikeTemperatureText.visibility = View.VISIBLE
                    windCard.visibility = View.VISIBLE


                }

                LocationState.Loading -> {
                    cityText.visibility = View.INVISIBLE
                    weatherTemperatureText.visibility = View.INVISIBLE
                    weatherIcon.visibility = View.INVISIBLE
                    minWeatherTemperatureText.visibility = View.INVISIBLE
                    maxWeatherTemperatureText.visibility = View.INVISIBLE
                    weatherDescriptionText.visibility = View.INVISIBLE
                    feelsLikeTemperatureText.visibility = View.INVISIBLE
                    windCard.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE

                }

                is LocationState.Success -> {
                    Log.d("CurrentLocation", "LocationState success")
                    cityText.text = locationState.data.city
                    weatherViewModel.getWeatherData(
                        locationState.data.latitude,
                        locationState.data.longitude,
                        this
                    )
                    cityText.visibility = View.VISIBLE
                    weatherTemperatureText.visibility = View.VISIBLE
                    weatherIcon.visibility = View.VISIBLE
                    minWeatherTemperatureText.visibility = View.VISIBLE
                    maxWeatherTemperatureText.visibility = View.VISIBLE
                    weatherDescriptionText.visibility = View.VISIBLE
                    feelsLikeTemperatureText.visibility = View.VISIBLE
                    windCard.visibility = View.VISIBLE
                }
            }
        }

        // Observers for the weatherViewModel
        weatherViewModel.weatherUpdateUi.observe(this) {
            when (weatherViewModel.weatherUpdateUi.value) {
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
                                        R.string.current_feels_like_tempertature_celcius,
                                        it?.feelsLikeTemperature
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
                                        R.string.current_feels_like_tempertature_fahrenheit,
                                        it?.feelsLikeTemperature
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
                        windCard.updateWindCard(
                            speed = it?.windSpeed.toString(),
                            "m/s",
                            direction = it?.windDegrees.toString()
                        )
                    }
                }

                else -> {}
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

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCAL_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCAL_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Log.d("PermissionTest", "OnRequestPermissionResult")
                    locationViewModel.getCurrentLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            else -> {
                // FOR UNDEFINED REQUEST CODES
            }
        }
    }
}