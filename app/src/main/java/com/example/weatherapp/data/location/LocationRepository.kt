package com.example.weatherapp.data.location

import android.util.Log
import javax.inject.Inject

sealed interface LocationResult {
    data class Success(val data: LocationInfo) : LocationResult
    data class Error(val message: String) : LocationResult
}

interface LocationRepository {
    suspend fun getLocationData(): LocationResult
}

class LocationRepositoryImpl @Inject constructor(
    private val localLocationDataSource: LocationDao,
    private val remoteLocationDataSource: LocationDataSource
) : LocationRepository {
    override suspend fun getLocationData(): LocationResult {
        val locationData = localLocationDataSource.getLocationData()
        val currentTime = System.currentTimeMillis()
        return if (locationData != null && currentTime - locationData.lastUpdated < CACHE_EXPIRY_TIME) {
            Log.d("LoadingDebug", "Loading the location data from cache")
            LocationResult.Success(locationData)
        } else {
            val remoteLocationData = remoteLocationDataSource.getCurrentLocation()
            Log.d("LoadingDebug", "Loading the location from server")
            if (remoteLocationData != null) {
                with(localLocationDataSource) {
                    clearLocationCache()
                    insertLocationData(remoteLocationData)
                }
                LocationResult.Success(remoteLocationData)
            } else {
                val error = "Can't find a cache for the location data"
                Log.d("LoadingDebug", error)
                LocationResult.Error(error)
            }
        }
    }

    companion object {
        const val CACHE_EXPIRY_TIME = 60 * 60 * 1000
    }
}