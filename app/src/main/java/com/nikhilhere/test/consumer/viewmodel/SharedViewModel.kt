package com.nikhilhere.test.consumer.viewmodel

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nikhilhere.test.consumer.data.UserLocation
import com.nikhilhere.test.consumer.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {

    /**
     * Read User Location from DataStore
     */
    val userLocation = dataStoreRepository.readUserLocationFlow.asLiveData()

    /**
     * Save User Location to DatStore
     */
    fun saveUserLocation(userLocation: UserLocation) {
        viewModelScope.launch {
            dataStoreRepository.saveUserLocation(userLocation)
        }
    }

    /**
     *
     */
    fun checkDeviceLocationSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


}

