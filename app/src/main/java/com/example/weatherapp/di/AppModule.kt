package com.example.weatherapp.di

import android.location.Geocoder
import com.example.weatherapp.data.location.LocationDao
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationDataSource
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.location.LocationRepositoryImpl
import com.example.weatherapp.data.weather.RemoteWeatherDataSourceImpl
import com.example.weatherapp.data.weather.RemoteWeatherDataSource
import com.example.weatherapp.data.weather.database.WeatherDao
import com.example.weatherapp.data.weather.WeatherRepository
import com.example.weatherapp.data.weather.WeatherRepositoryImpl
import com.example.weatherapp.network.WeatherApiService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    // Provide Retrofit instance
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provide WeatherApiService
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    // Provide RemoteWeatherDataSource
    @Provides
    @Singleton
    fun provideRemoteWeatherDataSource(apiService: WeatherApiService): RemoteWeatherDataSource {
        return RemoteWeatherDataSourceImpl(apiService)
    }

    // Provide WeatherDataRepository
    @Provides
    @Singleton
    fun provideWeatherDataRepository(
        localWeatherDataSource: WeatherDao,
        remoteDataSource: RemoteWeatherDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(localWeatherDataSource, remoteDataSource)
    }

    // Provide LocationDataRepository
    @Provides
    @Singleton
    fun provideLocationDataSource(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ): LocationDataRepository {
        return LocationDataSource(fusedLocationProviderClient, geocoder)
    }

    // Provide location repository
    @Provides
    @Singleton
    fun provideLocationRepository(
        localLocationDataSource: LocationDao,
        remoteLocationDataSource: LocationDataSource
    ): LocationRepository {
        return LocationRepositoryImpl(localLocationDataSource, remoteLocationDataSource)
    }
}