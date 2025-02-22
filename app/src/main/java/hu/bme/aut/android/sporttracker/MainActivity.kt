package hu.bme.aut.android.sporttracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.permissions.requestLocationPermission
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            MapScreen(this, fusedLocationClient)
        }
    }

    suspend fun handleLocationPermission() {
        val permissionGranted = requestLocationPermission(this, fusedLocationClient, 1)
        if (permissionGranted) {
            // Permission was already granted, update the map location
            val userLocation = getLastKnownLocation(this, fusedLocationClient)
            setContent {
                MapScreen(this, fusedLocationClient, userLocation)
            }
        }

    }
}