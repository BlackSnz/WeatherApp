package com.example.weatherapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherMainBinding
import com.example.weatherapp.ui.fragments.RequestLocationPermissionDialogFragment
import com.example.weatherapp.ui.vm.LocationState
import com.example.weatherapp.ui.vm.LocationViewModel
import com.example.weatherapp.ui.vm.WeatherDataUiState
import com.example.weatherapp.ui.vm.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

const val LOCAL_PERMISSION_REQUEST_CODE = 1
private const val BASE_ICON_URL = "https://openweathermap.org/img/wn/"
private const val ICON_URL_POSTFIX = "@2x.png"

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

        fun showProgressBar() {
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

        fun hideProgressBar() {
            progressBar.visibility = View.GONE
            cityText.visibility = View.VISIBLE
            weatherTemperatureText.visibility = View.VISIBLE
            weatherIcon.visibility = View.VISIBLE
            minWeatherTemperatureText.visibility = View.VISIBLE
            maxWeatherTemperatureText.visibility = View.VISIBLE
            weatherDescriptionText.visibility = View.VISIBLE
            feelsLikeTemperatureText.visibility = View.VISIBLE
            windCard.visibility = View.VISIBLE
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

        // Observers
        locationViewModel.locationState.observe(this) { locationState ->
            when (locationState) {
                is LocationState.Error -> {
                    weatherViewModel.getWeatherData(
                        locationState.data.latitude,
                        locationState.data.longitude
                    )
                }

                LocationState.Loading -> {
                    showProgressBar()
                }

                is LocationState.Success -> {
                    cityText.text = locationState.data.city
                    weatherViewModel.getWeatherData(
                        locationState.data.latitude,
                        locationState.data.longitude
                    )
                }
            }
        }

        weatherViewModel.weatherDataUiState.observe(this) { state ->
            when (state) {
                is WeatherDataUiState.Loading -> {
                    showProgressBar()
                }

                is WeatherDataUiState.Success -> {
                    hideProgressBar()
                    state.data.let {
                        getWeatherIcon(it.iconCode, this) { drawable ->
                            weatherIcon.setImageDrawable(drawable)
                        }
                        humidityText.text = this.getString(R.string.current_humidity, it.humidity)
                        windText.text = this.getString(R.string.current_wind, it.windSpeed)
                        weatherDescriptionText.text = it.weatherDescription
                        // Checking the temperature unit and set text for the temperature
                        val temperatureUnit =
                            sharedPreferences.getString("temperature_unit", "Celsius")
                        when (temperatureUnit) {
                            "Celsius" -> {
                                weatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_temperature_celcius,
                                        calculateTemperatureByUnit(
                                            it.currentTemperature.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                minWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_min_temperature_celcuis,
                                        calculateTemperatureByUnit(
                                            it.minTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                maxWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_max_temperature_celcuis,
                                        calculateTemperatureByUnit(
                                            it.maxTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                feelsLikeTemperatureText.text =
                                    this.getString(
                                        R.string.current_feels_like_tempertature_celcius,
                                        calculateTemperatureByUnit(
                                            it.feelsLikeTemperature.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                            }

                            "Fahrenheit" -> {
                                weatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_temperature_fahrenheit,
                                        calculateTemperatureByUnit(
                                            it.currentTemperature.toDouble(),
                                            temperatureUnit
                                        )

                                    )
                                minWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_min_temperature_fahrenheit,
                                        calculateTemperatureByUnit(
                                            it.minTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )

                                    )
                                maxWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_max_temperature_fahrenheit,
                                        calculateTemperatureByUnit(
                                            it.maxTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )

                                    )
                                feelsLikeTemperatureText.text =
                                    this.getString(
                                        R.string.current_feels_like_tempertature_fahrenheit,
                                        calculateTemperatureByUnit(
                                            it.feelsLikeTemperature.toDouble(),
                                            temperatureUnit
                                        )

                                    )
                            }

                            else -> {}
                        }

                        // Checking the pressure unit and set text for the pressure
                        val pressureUnit = sharedPreferences.getString("pressure_unit", "mmHg")
                        when (pressureUnit) {
                            "hPa" -> {
                                pressureText.text =
                                    this.getString(
                                        R.string.current_pressure_hpa,
                                        calculatePressureByUnit(
                                            it.pressure.toDouble(),
                                            pressureUnit
                                        )
                                    )
                            }

                            "mmHg" -> {
                                pressureText.text =
                                    this.getString(
                                        R.string.current_pressure_mmHg,
                                        calculatePressureByUnit(
                                            it.pressure.toDouble(),
                                            pressureUnit
                                        )
                                    )
                            }
                        }
                        // Update information about wind
                        windCard.updateWindCard(
                            speed = it.windSpeed,
                            "m/s",
                            direction = it.windDegrees
                        )
                    }
                }

                else -> {}
            }
        }
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

    private fun getWeatherIcon(iconCode: String, context: Context, callback: (Drawable?) -> Unit) {
        val iconUrl = "$BASE_ICON_URL$iconCode$ICON_URL_POSTFIX"

        Glide.with(context)
            .load(iconUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    callback(
                        AppCompatResources.getDrawable(context, R.drawable.weather_icon)
                    )
                }
            })
        }

    private fun calculateTemperatureByUnit(temperature: Double, temperatureUnit: String): String {
        return when (temperatureUnit) {
            "Celsius" -> {
                temperature.minus(272.15).roundToInt().toString()
            }

            "Fahrenheit" -> {
                temperature.minus(272.15).times(9 / 5).plus(32).roundToInt().toString()
            }

            else -> throw IllegalArgumentException("Unsupported temperature unit")
        }
    }

    private fun calculatePressureByUnit(pressure: Double, pressureUnit: String): String {
        return when (pressureUnit) {
            "mmHg" -> pressure.times(0.75006).roundToInt().toString()
            else -> {
                pressure.roundToInt().toString()
            }
        }
    }
}