package com.example.weatherapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.weatherapp.R

class WeatherSettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val temperatureUnitPreference = findPreference<ListPreference>("temperature_unit")
        val pressureUnitPreference = findPreference<ListPreference>("pressure_unit")
        when (key) {
            "temperature_unit" -> {
                PreferenceManager.getDefaultSharedPreferences(this.requireContext()).edit()
                    ?.putString(key.toString(), temperatureUnitPreference?.value)?.apply()
            }

            "pressure_unit" -> {
                PreferenceManager.getDefaultSharedPreferences(this.requireContext()).edit()
                    ?.putString(key.toString(), pressureUnitPreference?.value)?.apply()
            }
        }
    }

}