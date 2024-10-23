package com.example.weatherapp.di

import android.content.Context
import android.location.Geocoder
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.weather.NetworkWeatherDataRepository
import com.example.weatherapp.data.weather.WeatherDataRepository
import com.example.weatherapp.network.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    // Provide WeatherDataRepository
    @Provides
    @Singleton
    fun provideWeatherDataRepository(apiService: WeatherApiService): WeatherDataRepository {
        return NetworkWeatherDataRepository(apiService)
    }

    // Provide LocationDataRepository
    @Provides
    @Singleton
    fun provideLocationDataRepository(@ApplicationContext context: Context, geocoder: Geocoder): LocationDataRepository {
        return LocationRepository(context, geocoder)
    }
}