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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.WeatherCondition
import com.example.weatherapp.databinding.WeatherMainBinding
import com.example.weatherapp.ui.fragments.RequestLocationPermissionDialogFragment
import com.example.weatherapp.ui.view.adapters.DailyForecastAdapter
import com.example.weatherapp.ui.view.adapters.WeatherHourlyAdapter
import com.example.weatherapp.ui.vm.WeatherDataUiState
import com.example.weatherapp.ui.vm.WeatherMainScreenViewModel
import com.example.weatherapp.utils.TemperatureUtils.calculateTemperatureByUnit
import com.example.weatherapp.utils.WeatherIconUtils.getWeatherIconId
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

const val LOCAL_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class WeatherMainActivity : AppCompatActivity(),
    RequestLocationPermissionDialogFragment.NoticeDialogListener {

    private lateinit var binding: WeatherMainBinding
    private lateinit var weatherHourlyAdapter: WeatherHourlyAdapter
    private lateinit var weatherDailyAdapter: DailyForecastAdapter
    private val weatherMainScreenViewModel: WeatherMainScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("DebugOnCreate", "OnCreate invoke()")

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val weatherLoadingAnimation = findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        // Values for the current location
        val cityText = binding.tvCity

        // Values for the current weather
        val weatherTemperatureText = binding.tvTemperature
        val weatherIcon = binding.ivWeatherIcon
        val minWeatherTemperatureText = binding.tvMinTemperatureToday
        val maxWeatherTemperatureText = binding.tvMaxTemperatureToday
        val feelsLikeTemperatureText = binding.tvFeelsLikeToday
        val weatherDescriptionText = binding.tvWeatherDescription


        // CARD VIEWS
        // Values for the wind card information
        val windCard = binding.cvWindInformation
        val textSpeedWindCard = binding.tvWindCardSpeedValue
        val iconDirectionWindCard = binding.ivWindCardIcon
        // Values for the humidity card information
        val humidityCard = binding.cvHumidityInformation
        val textHumidityValue = binding.tvHumidityValue
        // Values for the pressure card information
        val pressureCard = binding.cvPressureInformation
        val pressureValue = binding.tvPressureValue
        val pressureUnitCard = binding.tvPressureUnit

        // Settings icon
        val settingsIcon = binding.ivSettings
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Refresh icon
        val refreshIcon = binding.ivRefresh
        refreshIcon.setOnClickListener {
            weatherMainScreenViewModel.getWeatherInformation()
        }

        // CardView for Hourly Forecast
        val hourlyForecastCard = binding.cvHourlyForecast
        // RecyclerView for Hourly Forecast
        val hourlyForecastRecyclerView = findViewById<RecyclerView>(R.id.rvHourlyForecast)
        hourlyForecastRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // CardView for daily forecast
        val dailyForecastCard = binding.cvDailyForecast
        // RecyclerView for daily forecast
        val dailyForecastRecyclerView = findViewById<RecyclerView>(R.id.rvDailyForecast)
        dailyForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Error message
        val errorMessage = binding.includedErrorLayout.clLoadingError
        val taButton = binding.includedErrorLayout.buttonTryAgain
        val loadDefaultButton = binding.includedErrorLayout.buttonLoadDefault

        fun showProgressBar() {
            pressureCard.visibility = View.GONE
            humidityCard.visibility = View.GONE
            hourlyForecastCard.visibility = View.GONE
            errorMessage.visibility = View.GONE
            cityText.visibility = View.GONE
            weatherTemperatureText.visibility = View.GONE
            weatherIcon.visibility = View.GONE
            minWeatherTemperatureText.visibility = View.GONE
            maxWeatherTemperatureText.visibility = View.GONE
            weatherDescriptionText.visibility = View.GONE
            feelsLikeTemperatureText.visibility = View.GONE
            windCard.visibility = View.GONE
            dailyForecastCard.visibility = View.GONE

            weatherLoadingAnimation.visibility = View.VISIBLE
            weatherLoadingAnimation.playAnimation()
        }

        fun hideProgressBar() {
            weatherLoadingAnimation.visibility = View.INVISIBLE
            pressureCard.visibility = View.VISIBLE
            weatherLoadingAnimation.playAnimation()
            humidityCard.visibility = View.VISIBLE
            hourlyForecastCard.visibility = View.VISIBLE
            cityText.visibility = View.VISIBLE
            weatherTemperatureText.visibility = View.VISIBLE
            weatherIcon.visibility = View.VISIBLE
            minWeatherTemperatureText.visibility = View.VISIBLE
            maxWeatherTemperatureText.visibility = View.VISIBLE
            weatherDescriptionText.visibility = View.VISIBLE
            feelsLikeTemperatureText.visibility = View.VISIBLE
            windCard.visibility = View.VISIBLE
            dailyForecastCard.visibility = View.VISIBLE
        }

        // Realise the workflow for requesting the location permission
        fun requestWeatherInfoWithPermission() {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("LoadingDebug", "Permission: ACCESS_COARSE_LOCATION granted")
                    weatherMainScreenViewModel.getWeatherInformation()
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
        }

        requestWeatherInfoWithPermission()

        // Error buttons onClickListeners
        taButton.setOnClickListener {
            requestWeatherInfoWithPermission()
        }
        loadDefaultButton.setOnClickListener {
            weatherMainScreenViewModel.getDefaultCityWeatherInfo()
        }

        // Observers
        weatherMainScreenViewModel.weatherDataUiState.observe(this) { state ->
            Log.d(
                "LoadingDebug",
                "(MainActivity. Observer weatherDataUiState) -> Change in weatherDataUiState"
            )
            when (state) {
                is WeatherDataUiState.Loading -> {
                    Log.d("LoadingDebug", "Activate showProgressBar")
                    showProgressBar()
                }

                is WeatherDataUiState.Error -> {
                    weatherLoadingAnimation.visibility = View.INVISIBLE
                    errorMessage.visibility = View.VISIBLE
                }
                is WeatherDataUiState.Success -> {
                    val weatherHourlyData = weatherMainScreenViewModel.weatherHourlyData.value
                    val weatherDailyData = weatherMainScreenViewModel.weatherDailyForecast.value
                    Log.d("WeatherDataDebug", "Weather daily forecast: $weatherDailyData")
                    Log.d("WeatherDataDebug", "Weather hourly forecast: $weatherHourlyData")

                    if(weatherHourlyData != null) {
                        weatherHourlyAdapter = WeatherHourlyAdapter(weatherHourlyData)
                        weatherDailyAdapter = DailyForecastAdapter(weatherDailyData!!)
                        hourlyForecastRecyclerView.adapter = weatherHourlyAdapter
                        dailyForecastRecyclerView.adapter = weatherDailyAdapter
                    } else {
                        hourlyForecastRecyclerView.adapter = null
                        dailyForecastRecyclerView.adapter = null
                        dailyForecastCard.visibility = View.GONE
                        hourlyForecastCard.visibility = View.GONE
                    }

                    hideProgressBar()
                    state.data.let {
                        weatherIcon.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this,
                                getWeatherIconId(it.iconCode)
                            )
                        )
                        cityText.text = weatherMainScreenViewModel.locationData.value?.city
                        textHumidityValue.text = this.getString(R.string.current_humidity, it.humidity)
                        // Set the weather description language by current locale
                        val currentLocale = resources.configuration.locales.get(0)
                        if (currentLocale.language == "ru" && currentLocale.country == "RU") {
                            weatherDescriptionText.text = WeatherCondition.byCode(it.weatherId)?.description
                        } else {
                            weatherDescriptionText.text = it.weatherDescription
                        }
                        // Checking the temperature unit and set text for the temperature
                        val temperatureUnit =
                            sharedPreferences.getString("temperature_unit", "Celsius")
                        when (temperatureUnit) {
                            "Celsius" -> {
                                weatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_temperature_celsius,
                                        calculateTemperatureByUnit(
                                            it.currentTemperature.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                minWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_min_temperature_celsius,
                                        calculateTemperatureByUnit(
                                            it.minTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                maxWeatherTemperatureText.text =
                                    this.getString(
                                        R.string.current_max_temperature_celsius,
                                        calculateTemperatureByUnit(
                                            it.maxTemperatureToday.toDouble(),
                                            temperatureUnit
                                        )
                                    )
                                feelsLikeTemperatureText.text =
                                    this.getString(
                                        R.string.current_feels_like_temperature_celsius,
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
                                        R.string.current_feels_like_temperature_fahrenheit,
                                        calculateTemperatureByUnit(
                                            it.feelsLikeTemperature.toDouble(),
                                            temperatureUnit
                                        )

                                    )
                            }

                        }

                        // Checking the pressure unit and set text for the pressure
                        val pressureUnit = sharedPreferences.getString("pressure_unit", "mmHg")
                        when (pressureUnit) {
                            "hPa" -> {
                                pressureUnitCard.text = getString(R.string.pressure_unit_card_hpa)
                                pressureValue.text =
                                    this.getString(
                                        R.string.current_pressure_hpa,
                                        calculatePressureByUnit(
                                            it.pressure.toDouble(),
                                            pressureUnit
                                        )
                                    )
                            }

                            "mmHg" -> {
                                pressureUnitCard.text = getString(R.string.pressure_unit_card_mmHg)
                                pressureValue.text =
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
                        textSpeedWindCard.text = it.windSpeed
                        iconDirectionWindCard.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this,
                                weatherMainScreenViewModel.setDirectionIcon(it.windDegrees)
                            )
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
                    weatherMainScreenViewModel.getWeatherInformation()
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

    private fun calculatePressureByUnit(pressure: Double, pressureUnit: String): String {
        return when (pressureUnit) {
            "mmHg" -> pressure.times(0.75006).roundToInt().toString()
            else -> {
                pressure.roundToInt().toString()
            }
        }
    }
}