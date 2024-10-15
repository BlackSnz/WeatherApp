package com.example.weatherapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.location.LocationInfo
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class LocationViewModel : ViewModel() {

    private val _currentLocation = MutableLiveData<LocationInfo?>()
    val currentLocation: LiveData<LocationInfo?> = _currentLocation

    fun getLastKnownLocation(activity: Activity, requestCode: Int) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity.applicationContext)

        // Checking whether we have access to ACCESS_COARSE_LOCATION permission
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Add listeners to the Task from lastLocation
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val locationNamesInfo = getCurrentLocationName(latitude, longitude, activity.applicationContext)
                    if(_currentLocation.value == null) {
                        _currentLocation.value = LocationInfo(
                            latitude,
                            longitude,
                            locationNamesInfo.first,
                            locationNamesInfo.second)
                    } else {
                        _currentLocation.value = _currentLocation.value!!.copy(
                            latitude = latitude,
                            longitude = longitude,
                            city = locationNamesInfo.first,
                            country = locationNamesInfo.second
                        )
                    }
                } else {
                    getCurrentLocation(activity)
                }
            }
            fusedLocationClient.lastLocation.addOnFailureListener { exception: Exception ->
                Log.e("LocationService", "Error getting last location: ${exception.message}")
                getCurrentLocation(activity)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
        }
    }

    fun getCurrentLocation(activity: Activity) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity.applicationContext)

        // Checking whether we have access to ACCESS_COARSE_LOCATION permission
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Add listeners to the Task from lastLocation
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val locationNamesInfo =
                        getCurrentLocationName(latitude, longitude, activity.applicationContext)
                    if (_currentLocation.value == null) {
                        _currentLocation.value = LocationInfo(
                            latitude,
                            longitude,
                            locationNamesInfo.first,
                            locationNamesInfo.second
                        )
                    } else {
                        _currentLocation.value = _currentLocation.value!!.copy(
                            latitude = latitude,
                            longitude = longitude,
                            city = locationNamesInfo.first,
                            country = locationNamesInfo.second
                        )
                    }
                } else {
                    Toast.makeText(activity.applicationContext, "Не смогли получить ваше местоположение. Попробуйте ещё раз.", Toast.LENGTH_SHORT).show()
                }
            }
            fusedLocationClient.lastLocation.addOnFailureListener { exception: Exception ->
                Log.e("LocationService", "Error getting last location: ${exception.message}")
                Toast.makeText(activity.applicationContext, "Не смогли получить ваше местоположение. Попробуйте ещё раз.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getCurrentLocationName(
        latitude: Double,
        longitude: Double,
        context: Context
    ): Pair<String, String> {
        val geocoder = Geocoder(context, Locale.getDefault())
        var result = Pair("", "")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        result = Pair(addresses[0].locality, addresses[0].countryName)
                    } else {
                        Log.d("LocationTest", "No address found")
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("LocationTest", "Geocoding error: $errorMessage")
                }
            })
            return result
        } else {
            // Для устройств с версией Android ниже API 33, используйте синхронный метод (не рекомендуется).
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    result = Pair(addresses[0].locality, addresses[0].countryName)
                } else {
                    Log.d("LocationTest", "No address found")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }
    }
}