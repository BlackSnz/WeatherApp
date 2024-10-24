package com.example.weatherapp.data.location

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


interface LocationDataRepository {
    fun getCurrentLocation(callback: (LocationCallbackResult) -> Unit)
}

sealed class LocationCallbackResult {
    data class Success(val data: LocationInfo) : LocationCallbackResult()
    data class OnlyCoordinates(val data: LocationInfo) : LocationCallbackResult()
    data class Error(val message: String) : LocationCallbackResult()
}

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationDataRepository {

    override fun getCurrentLocation(callback: (LocationCallbackResult) -> Unit) {
        try {
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude.toBigDecimal()
                        .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                    val longitude = location.longitude.toBigDecimal()
                        .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                    getCurrentLocationName(latitude, longitude) { locationName ->
                        if (locationName != null) {
                            callback(
                                LocationCallbackResult.Success(
                                    LocationInfo(
                                        latitude,
                                        longitude,
                                        locationName.first,
                                        locationName.second
                                    )
                                )
                            )
                        } else { // If can't get location name, return only coordinates
                            callback(
                                LocationCallbackResult.OnlyCoordinates(
                                    LocationInfo(
                                        latitude,
                                        longitude,
                                        null,
                                        null
                                    )
                                )
                            )
                        }
                    }
                } else { // If can't get location
                    callback(
                        LocationCallbackResult.Error("Can't get current location")
                    )
                }
            }.addOnFailureListener { exception: Exception ->
                callback(
                    LocationCallbackResult.Error(
                        exception.message ?: "Unknown error when try to get current location"
                    )
                )
            }
        } catch (e: SecurityException) { // When don't have location permissions
            callback(
                LocationCallbackResult.Error(
                    e.message ?: "Don't have permission for location"
                )
            )
        }
    }

    private fun getCurrentLocationName(
        latitude: Double,
        longitude: Double,
        callback: (Pair<String?, String?>?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        callback(Pair(addresses[0].locality, addresses[0].countryName))
                    } else {
                        Log.d("CurrentLocation", "Can't get location in getCurrentLocationName()")
                        callback(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("CurrentLocation", "Geocoding error: $errorMessage")
                    callback(null)
                }
            })
        } else {
            // For devices with API lower then 33 use synchronous method.
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    callback(Pair(addresses[0].locality, addresses[0].countryName))
                } else {
                    Log.d("LocationTest", "No address found")
                    callback(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}