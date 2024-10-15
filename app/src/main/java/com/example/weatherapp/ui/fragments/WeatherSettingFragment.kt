package com.example.weatherapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R

class WeatherSettingFragment : PreferenceFragmentCompat(),  SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        // Устанавливаем текущее значение в summary при первом запуске
        val temperatureUnitPreference = findPreference<ListPreference>("temperature_unit")
        temperatureUnitPreference?.summary = temperatureUnitPreference?.entry

        val preasureUnitPreference = findPreference<ListPreference>("pressure_unit")
        preasureUnitPreference?.summary = preasureUnitPreference?.entry
    }

    override fun onResume() {
        super.onResume()
        // Регистрируем слушатель изменений
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Отменяем регистрацию слушателя изменений
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val temperatureUnitPreference = findPreference<ListPreference>(key!!)
        temperatureUnitPreference?.summary = temperatureUnitPreference?.entry
    }
}