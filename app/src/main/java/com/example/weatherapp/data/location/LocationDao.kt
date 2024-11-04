package com.example.weatherapp.data.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM location_data")
    suspend fun getLocationData(): LocationInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationData(data: LocationInfo)

    @Query("DELETE FROM location_data")
    suspend fun clearLocationCache()
}