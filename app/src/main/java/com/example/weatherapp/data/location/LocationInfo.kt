package com.example.weatherapp.data.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_data")
data class LocationInfo(
    @PrimaryKey val id: Long,
    val latitude: Double,
    val longitude: Double,
    val city: String?,
    val country: String?,
    val lastUpdated: Long,
)

object DefaultLocationInfo {
    val latitude = 55.7522
    val longitude = 37.6156
    val city = "Moscow"
    val country = "Russia"
}