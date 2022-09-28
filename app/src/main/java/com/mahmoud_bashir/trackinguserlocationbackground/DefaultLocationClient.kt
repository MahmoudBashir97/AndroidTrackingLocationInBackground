package com.mahmoud_bashir.trackinguserlocationbackground

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DefaultLocationClient(
    private val context:Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
): LocationClient{
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


        }
    }

}