package com.example.weatherapp.ui.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.APP_VERSION
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SettingsActivityBinding
import com.example.weatherapp.ui.fragments.WeatherSettingFragment

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, WeatherSettingFragment())
            .commit()

        val settingsAppVersion = binding.tvSettingsAppVersion
        settingsAppVersion.text = getString(R.string.app_version, APP_VERSION)
    }
}