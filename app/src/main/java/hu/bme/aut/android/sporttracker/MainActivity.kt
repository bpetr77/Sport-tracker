package hu.bme.aut.android.sporttracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.permissions.requestLocationPermission
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userLocation = mutableStateOf<LatLng?>(null)
    private val locationPermissionGranted = mutableStateOf(false)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationPermissionGranted.value = true
            lifecycleScope.launch {
                val location = getLastKnownLocation(this@MainActivity, fusedLocationClient)
                userLocation.value = location
            }
        } else {
            Log.w("Location", "Permission denied by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            MapScreen(this, fusedLocationClient, userLocation, locationPermissionGranted)
        }
    }

    fun handleLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                locationPermissionGranted.value = true
                lifecycleScope.launch {
                    val location = getLastKnownLocation(this@MainActivity, fusedLocationClient)
                    userLocation.value = location
                }
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}