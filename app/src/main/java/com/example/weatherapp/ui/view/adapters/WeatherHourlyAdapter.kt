package com.example.weatherapp.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.HourlyForecastUnit
import com.example.weatherapp.utils.TemperatureUtils.calculateTemperatureByUnit
import com.example.weatherapp.utils.TimeUtils.unixTimeToIsoTime
import com.example.weatherapp.utils.WeatherIconUtils.getWeatherIconId
import kotlin.math.roundToInt

class WeatherHourlyAdapter(private val hourlyForecastData: List<HourlyForecastUnit>) :
    RecyclerView.Adapter<WeatherHourlyAdapter.WeatherHourlyViewHolder>() {

    inner class WeatherHourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourlyTemperature: TextView = itemView.findViewById(R.id.tvHourlyTemperature)
        val hourlyPrecipitation: TextView = itemView.findViewById(R.id.tvHourlyPrecipitation)
        val hourlyTime: TextView = itemView.findViewById(R.id.tvHourlyTime)
        val hourlyWeatherIcon: ImageView = itemView.findViewById(R.id.ivHourlyWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHourlyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_item, parent, false)
        return WeatherHourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherHourlyViewHolder, position: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.context)
        val temperatureUnit =
            sharedPreferences.getString("temperature_unit", "Celsius")
        var hourlyTemperatureText =  ""
        when(temperatureUnit) {
            "Celsius" -> {
                val hourlyTemperature = calculateTemperatureByUnit(
                    hourlyForecastData[position].temperature.toDouble(),
                    "Celsius"
                )
                hourlyTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_celcius,
                    hourlyTemperature
                )
            }

            "Fahrenheit" -> {
                val hourlyTemperature = calculateTemperatureByUnit(
                    hourlyForecastData[position].temperature.toDouble(),
                    "Fahrenheit"
                )
                hourlyTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_fahrenheit,
                    hourlyTemperature
                )
            }
        }
        holder.hourlyTemperature.text = hourlyTemperatureText

        // Prepare text for hourly precipitation
        val precipitation = hourlyForecastData[position].precipitation
        if (shouldDisplayPrecipitation(precipitation)) {
            holder.hourlyPrecipitation.text = holder.itemView.context.getString(
                R.string.precipitation_in_percent,
                precipitation.toDouble().times(100).roundToInt().toString()
            )
            holder.hourlyPrecipitation.visibility = View.VISIBLE
        } else {
            holder.hourlyPrecipitation.visibility = View.GONE
        }
        // Prepare text for hourly time
        holder.hourlyTime.text = unixTimeToIsoTime(hourlyForecastData[position].time)
        // Weather icon
        holder.hourlyWeatherIcon.setImageResource(getWeatherIconId(hourlyForecastData[position].iconCode))
    }

    override fun getItemCount(): Int = hourlyForecastData.size

    private fun shouldDisplayPrecipitation(precipitation: String): Boolean {
        return precipitation.toDouble() >= 0.05
    }
}