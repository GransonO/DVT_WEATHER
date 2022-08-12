package com.granson.dvtweather.presentation

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.granson.dvtweather.utils.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
): ViewModel() {

    var hasPermissions = mutableStateOf(false)
    var isLocationAcquired = mutableStateOf(false)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = Common.UPDATE_INTERVAL
            fastestInterval = Common.FASTEST_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    trySend(location)
                    cancel()
                }
            }
        }
        locationClient.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())

        awaitClose {
            locationClient.removeLocationUpdates(callBack)
        }
    }
}