package com.jdefey.smartalarmclock.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.NoSuchElementException

private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10_000L
private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2_000L

class PeriodicLocationProvider constructor(private val context: Context) {

    private var locationCallback: LocationCallback? = null

    fun startLocationUpdates(callback: (Result<Location>) -> Unit) {
        locationCallback = createLocationCallback(callback)
        val locationRequest = createLocationRequest()
        subscribeToLocationUpdates(locationCallback!!, locationRequest)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun createLocationCallback(callback: (Result<Location>) -> Unit): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    callback.invoke(Result.success(location))
                } else {
                    callback.invoke(Result.failure(NoSuchElementException("No location found")))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates(callback: LocationCallback, request: LocationRequest) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}
