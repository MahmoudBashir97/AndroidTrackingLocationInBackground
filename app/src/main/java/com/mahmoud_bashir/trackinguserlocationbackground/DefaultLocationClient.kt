package com.mahmoud_bashir.trackinguserlocationbackground

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context:Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
): LocationClient{
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLocationUpdates(intervals: Long): Flow<Location> {
        return callbackFlow {
            if (!context.hasLocationPermissionGranted()){
                throw LocationClient.LocationException("Missing Location Permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled){
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.create()
                .setInterval(intervals)
                .setFastestInterval(intervals)

            val locationCallback = object:LocationCallback(){
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }

            }

            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback, Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

}