package hu.bme.aut.android.sporttracker.data.location

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.tasks.await


suspend fun getLastKnownLocation(activity: ComponentActivity, fusedLocationClient: FusedLocationProviderClient): LatLng? {
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
        Log.w("Location", "Location permission not granted")
        return null
    }

    return try {
        val location = fusedLocationClient.lastLocation.await()
        location?.let { LatLng(it.latitude, it.longitude) }
    } catch (e: Exception) {
        Log.e("Location", "Failed to get location: ${e.message}")
        null
    }
}