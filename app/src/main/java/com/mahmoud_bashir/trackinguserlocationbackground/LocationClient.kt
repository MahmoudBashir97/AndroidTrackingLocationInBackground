package com.mahmoud_bashir.trackinguserlocationbackground

import android.location.Location
import kotlinx.coroutines.flow.Flow


interface LocationClient {
    fun getLocationUpdates(intervals:Long):Flow<Location>

    class LocationException(message:String):Exception()
}