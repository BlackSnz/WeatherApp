package com.example.weatherapp.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


interface LocationDataRepository {
    fun getCurrentLocation(callback: (LocationInfo?) -> Unit)
    fun getCurrentLocationName(latitude: Double, longitude: Double): Pair<String, String>
}

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geocoder: Geocoder,
): LocationDataRepository {

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun getCurrentLocation(callback: (LocationInfo?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude.toBigDecimal()
                        .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                    val longitude = location.longitude.toBigDecimal()
                        .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                    val locationNamesInfo = getCurrentLocationName(latitude, longitude)
                    callback(LocationInfo(
                        latitude,
                        longitude,
                        locationNamesInfo.first,
                        locationNamesInfo.second
                    ))
                } else {
                    callback(null)
                }
            }.addOnFailureListener { exception: Exception ->
                Log.e("LocationService", "Error getting location: ${exception.message}")
                callback(null)
            }
        } else {
            // Нет разрешения на доступ к местоположению
            callback(null)
        }
    }

    override fun getCurrentLocationName(latitude: Double, longitude: Double): Pair<String, String> {
        var result: Pair<String, String> = Pair("?", "?")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        result = Pair(addresses[0].locality, addresses[0].countryName)
                    } else {
                        Log.d("CurrentLocation", "Can't get location in getCurrentLocationName()")
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("CurrentLocation", "Geocoding error: $errorMessage")
                }
            })
            return result
        } else {
            // For devices with API lower then 33 use synchronous method.
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