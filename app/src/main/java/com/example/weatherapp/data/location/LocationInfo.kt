package com.example.weatherapp.data.location

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val city: String?,
    val country: String?,
)

object DefaultLocationInfo {
    val latitude = 55.7522
    val longitude = 37.6156
    val city = "Moscow"
    val country = "Russia"
}