package com.example.weatherapp.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.HourlyForecastUnit
import com.example.weatherapp.utils.TemperatureUtils.calculateTemperatureByUnit
import com.example.weatherapp.utils.WeatherIconUtils.getWeatherIconId
import java.util.Locale
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
        // Prepare text for hourly temperature
        val hourlyTemperatureInCelsius = calculateTemperatureByUnit(
            hourlyForecastData[position].temperature.toDouble(),
            "Celsius"
        )
        val hourlyTemperatureText = holder.itemView.context.getString(
            R.string.current_temperature_celcius,
            hourlyTemperatureInCelsius
        )
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

    private fun unixTimeToIsoTime(unixTime: String): String {
        val unixTimestamp = unixTime.toLong()
        val date = java.util.Date(unixTimestamp * 1000L)
        val sdf = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    private fun shouldDisplayPrecipitation(precipitation: String): Boolean {
        return precipitation.toDouble() >= 0.05
    }
}