package com.example.weatherapp.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.location.DefaultLocationInfo
import com.example.weatherapp.data.location.LocationCallbackResult
import com.example.weatherapp.data.location.LocationDataRepository
import com.example.weatherapp.data.location.LocationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface LocationState {
    object Loading : LocationState
    data class Success(val data: LocationInfo) : LocationState
    data class Error(val message: String, val data: DefaultLocationInfo) : LocationState
}

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var locationRepository: LocationDataRepository

    private val _locationState = MutableLiveData<LocationState>(LocationState.Loading)
    val locationState: LiveData<LocationState> = _locationState

    fun getCurrentLocation() {
        Log.d("CurrentLocation", "I'm in getCurrentLocation in LocationViewModel")
        _locationState.value = LocationState.Loading
        locationRepository.getCurrentLocation { locationCallbackResult ->
            when (locationCallbackResult) {
                is LocationCallbackResult.Error -> _locationState.value =
                    LocationState.Error("Can't loading current location", DefaultLocationInfo)

                is LocationCallbackResult.OnlyCoordinates -> TODO()
                is LocationCallbackResult.Success -> {
                    _locationState.postValue(LocationState.Success(locationCallbackResult.data))
                }
            }
        }
    }
}