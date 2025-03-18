package hu.bme.aut.android.sporttracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.data.location.repository.getLastKnownLocation
import hu.bme.aut.android.sporttracker.data.service.LocationService
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.theme.SportTrackerTheme
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModelFactory


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userLocation = mutableStateOf<LatLng?>(null)
    private val locationPermissionGranted = mutableStateOf(false)
    private lateinit var locationRepository: LocationRepository

    private val tourSettingsViewModel: TourSettingsViewModel by viewModels()
    private val tourStartedSettingsViewModel: TourStartedSettingsViewModel by viewModels {
        TourStartedSettingsViewModelFactory(locationRepository)
    }

    // Engedélykérés indítása az új API-val
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val foregroundServiceGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissions[Manifest.permission.FOREGROUND_SERVICE_LOCATION] ?: false
            } else {
                true // Nem kell az engedély Android 14 alatt
            }

        if (fineLocationGranted && coarseLocationGranted && foregroundServiceGranted) {
            locationPermissionGranted.value = true
            //startLocationService() // Foreground service indítása
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
        locationRepository = LocationRepository(fusedLocationClient, this)

        setContent {
            SportTrackerTheme(dynamicColor = true) {
                Surface {
                    MapScreen(
                        this,
                        fusedLocationClient,
                        userLocation,
                        locationPermissionGranted,
                        locationRepository,
                        tourSettingsViewModel,
                        tourStartedSettingsViewModel
                    )
                }
            }
        }

        // Engedélyek kérése
        handleLocationPermission()
    }

    fun handleLocationPermission() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        when {
            permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED } -> {
                locationPermissionGranted.value = true
                // TODO: delete this line
                startLocationService() // Foreground service elindítása
                lifecycleScope.launch {
                    val location = getLastKnownLocation(this@MainActivity, fusedLocationClient)
                    userLocation.value = location
                }
            }
            else -> {
                locationPermissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}