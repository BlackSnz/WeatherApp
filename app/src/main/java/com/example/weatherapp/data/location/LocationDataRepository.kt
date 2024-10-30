package com.example.weatherapp.data.location

import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface LocationDataRepository {
    suspend fun getCurrentLocation() : LocationResult
}

sealed interface LocationResult {
    data class Success(val data: LocationInfo) : LocationResult
    data class OnlyCoordinates(val data: LocationInfo) : LocationResult
    data class Error(val message: String) : LocationResult
}

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationDataRepository {

    override suspend fun getCurrentLocation(): LocationResult {
        Log.d("LoadingDebug", "invoke getCurrentLocation in Repository")
        return try {
            val location = getLocation()
            if (location != null) {
                Log.d("LoadingDebug", "Get location, not null")
                val latitude = location.latitude.toBigDecimal()
                    .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                val longitude = location.longitude.toBigDecimal()
                    .setScale(3, java.math.RoundingMode.HALF_EVEN).toDouble()
                val locationName = getCurrentLocationName(latitude, longitude)
                if (locationName != null) {
                    LocationResult.Success(
                        LocationInfo(
                            latitude,
                            longitude,
                            locationName.first,
                            locationName.second
                        )
                    )
                } else { // If can't get location name, return only coordinates
                    Log.d("LoadingDebug", "Can't get location name, return only coordinates")
                    LocationResult.OnlyCoordinates(
                        LocationInfo(
                            latitude,
                            longitude,
                            null,
                            null
                        )
                    )
                }
            } else { // If can't get location
                Log.d("LoadingDebug", "Can't get location")
                LocationResult.Error("Can't get current location")
            }
        } catch (e: SecurityException) {
            LocationResult.Error(e.message ?: "Don't have permission for location")
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown error when try to get current location")
        }
    }

    private suspend fun getLocation(): Location? = suspendCancellableCoroutine { cont ->
        try {
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).addOnSuccessListener { location ->
                Log.d("LoadingDebug", "Get location in LocationDataRepositor - Success")
                cont.resume(location)
            }.addOnFailureListener { exception ->
                Log.d("LoadingDebug", "Get location in LocationDataRepositor - Failure")
                cont.resumeWithException(exception)
            }
        } catch (e: SecurityException) {
            Log.d("LoadingDebug", "Get location in LocationDataRepositor - SecurityException")
            cont.resumeWithException(e)
        }
    }

    private suspend fun getCurrentLocationName(
        latitude: Double,
        longitude: Double,
    ): Pair<String, String>? {
        val deferredResult = CompletableDeferred<Pair<String, String>?>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        deferredResult.complete(
                            Pair(
                                addresses[0].locality,
                                addresses[0].countryName
                            )
                        )
                    } else {
                        deferredResult.complete(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    deferredResult.complete(null)
                }
            })
        } else {
            // For devices with API lower then 33 use synchronous method.
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    deferredResult.complete(Pair(addresses[0].locality, addresses[0].countryName))
                } else {
                    Log.d("LocationTest", "No address found")
                    deferredResult.complete(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                deferredResult.complete(null)
            }
        }
        return deferredResult.await()
    }
}