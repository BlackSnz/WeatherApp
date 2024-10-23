package com.example.weatherapp.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.location.LocationInfo
import com.example.weatherapp.data.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface LocationState {
    object Loading : LocationState
    data class Success(val data: LocationInfo) : LocationState
    data class Error(val message: String) : LocationState
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel() {

    private val _locationState = MutableLiveData<LocationState>(LocationState.Loading)
    val locationState: LiveData<LocationState> = _locationState

    fun getCurrentLocation() {
        _locationState.value = LocationState.Loading
        locationRepository.getCurrentLocation { location ->
            if(location != null) {
                _locationState.value = LocationState.Success(location)
            } else {
                _locationState.value = LocationState.Error("Can't loading current location")
            }
        }
    }
}