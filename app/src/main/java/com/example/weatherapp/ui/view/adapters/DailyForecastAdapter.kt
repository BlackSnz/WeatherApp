package com.example.weatherapp.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.data.weather.DailyForecastUnit
import com.example.weatherapp.utils.TemperatureUtils.calculateTemperatureByUnit
import com.example.weatherapp.utils.TimeUtils.unixToDayOfWeek
import com.example.weatherapp.utils.WeatherIconUtils.getWeatherIconId
import kotlin.math.roundToInt

class DailyForecastAdapter(private val dailyForecastData: List<DailyForecastUnit>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    inner class DailyForecastViewHolder(view: View) : ViewHolder(view) {
        val maxTemperature: TextView =
            itemView.findViewById(R.id.tvMaxTemperatureForecastUnitCardView)
        val minTemperature: TextView =
            itemView.findViewById(R.id.tvMinTemperatureForecastUnitCardView)
        val weatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIconDailyForecastCardView)
        val precipitation: TextView =
            itemView.findViewById(R.id.tvPrecipitationForecastUnitCardView)
        val dayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeekTemperatureForecastCardView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyForecastAdapter.DailyForecastViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.daily_forecast_item, parent, false)
        return DailyForecastViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DailyForecastAdapter.DailyForecastViewHolder,
        position: Int
    ) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(holder.itemView.context)
        val temperatureUnit =
            sharedPreferences.getString("temperature_unit", "Celsius")
        var dailyMaxTemperatureText = ""
        var dailyMinTemperatureText = ""
        when (temperatureUnit) {
            "Celsius" -> {
                val maxTemperature = calculateTemperatureByUnit(
                    dailyForecastData[position].maxTemperature.toDouble(),
                    "Celsius"
                )
                val minTemperature = calculateTemperatureByUnit(
                    dailyForecastData[position].minTemperature.toDouble(),
                    "Celsius"
                )
                dailyMaxTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_celcius,
                    maxTemperature
                )
                dailyMinTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_celcius,
                    minTemperature
                )
            }

            "Fahrenheit" -> {
                val maxTemperature = calculateTemperatureByUnit(
                    dailyForecastData[position].maxTemperature.toDouble(),
                    "Fahrenheit"
                )
                val minTemperature = calculateTemperatureByUnit(
                    dailyForecastData[position].minTemperature.toDouble(),
                    "Fahrenheit"
                )
                dailyMaxTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_fahrenheit,
                    maxTemperature
                )
                dailyMinTemperatureText = holder.itemView.context.getString(
                    R.string.current_temperature_fahrenheit,
                    minTemperature
                )
            }
        }
        holder.maxTemperature.text = dailyMaxTemperatureText
        holder.minTemperature.text = dailyMinTemperatureText

        val precipitation = dailyForecastData[position].precipitation
        if (shouldDisplayPrecipitation(precipitation)) {
            holder.precipitation.text = holder.itemView.context.getString(
                R.string.precipitation_in_percent,
                precipitation.toDouble().times(100).roundToInt().toString()
            )
            holder.precipitation.visibility = View.VISIBLE
        } else {
            holder.precipitation.visibility = View.GONE
        }
        holder.dayOfWeek.text = dailyForecastData[position].dayOfWeek
        holder.weatherIcon.setImageResource(getWeatherIconId(dailyForecastData[position].iconCode))
    }

    override fun getItemCount(): Int = dailyForecastData.size

    private fun shouldDisplayPrecipitation(precipitation: String): Boolean {
        return precipitation.toDouble() >= 0.05
    }
}



