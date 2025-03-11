package hu.bme.aut.android.sporttracker.data.location.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import androidx.core.content.ContextCompat
import hu.bme.aut.android.sporttracker.data.service.LocationService
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import hu.bme.aut.android.sporttracker.domain.location.LocationTracker

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
): LocationTracker {

    private val _locations = MutableStateFlow<List<LocationPoint>>(emptyList())
    override val locations: StateFlow<List<LocationPoint>> = _locations.asStateFlow()
    private var isUpdating = false

    override fun startLocationUpdates() {
        _locations.value = emptyList()
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationRepository", "Location permission not granted")
            return
        }

        if (isUpdating) {
            Log.w("LocationRepository", "Location updates already started, skipping...")
            return
        }
        isUpdating = true

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        if (!isServiceRunning(LocationService::class.java)) {
            val serviceIntent = Intent(context, LocationService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.d("LocationRepository", "Foreground service started")
        } else {
            Log.d("LocationRepository", "Foreground service already running")
        }

        Log.w("LocationRepository", "Location updates started")
    }

    override fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        isUpdating = false
        Log.w("LocationRepository", "Location updates stopped")


        // TODO: Stop foreground service

        // TODO: save to database
    }

    override fun pauseLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        isUpdating = false
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val newLocations = result.locations.map {
                LocationPoint(it.latitude, it.longitude, it.time)
            }
            _locations.value = _locations.value + newLocations
            logAllLocations()
        }
    }

    private fun logAllLocations() {
        _locations.value.forEach { location ->
            Log.d("LocationRepository", "Recorded location: $location")
        }
        Log.w("LocationRepository", "Total locations: ${_locations.value.size}")
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
