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
import android.content.Intent
import androidx.core.content.ContextCompat
//import hu.bme.aut.android.sporttracker.data.service.LocationService

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
) {

    private val _locations = MutableStateFlow<List<LatLng>>(emptyList())
    val locations: StateFlow<List<LatLng>> = _locations.asStateFlow()

    fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationRepository", "Location permission not granted")
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        // Start the foreground service
//        val serviceIntent = Intent(context, LocationService::class.java)
//        ContextCompat.startForegroundService(context, serviceIntent)

        Log.w("LocationRepository", "Location updates started")
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val newLocations = result.locations.map { LatLng(it.latitude, it.longitude) }
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
}
