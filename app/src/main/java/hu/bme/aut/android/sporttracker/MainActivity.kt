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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.data.location.repository.getLastKnownLocation
import hu.bme.aut.android.sporttracker.data.service.LocationService
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.theme.SportTrackerTheme
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModelFactory
import hu.bme.aut.android.sporttracker.ui.navigation.NavGraph
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel

//TODO: should put the viewmodels into the NavGraph
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userLocation = mutableStateOf<LatLng?>(null)
    //private var locationPermissionGranted = remember { mutableStateOf(false) }
    private lateinit var locationRepository: LocationRepository

    private val tourSettingsViewModel: TourSettingsViewModel by viewModels()
    private val tourStartedSettingsViewModel: TourStartedSettingsViewModel by viewModels {
        TourStartedSettingsViewModelFactory(locationRepository)
    }
    private val locationViewModel: LocationViewmodel by viewModels()

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
                true
            }

        if (fineLocationGranted && coarseLocationGranted && foregroundServiceGranted) {
            //locationPermissionGranted.value = true
            //tourSettingsViewModel.updatePermissionGranted(true)
            locationViewModel.updatePermissionGranted(true)
            //startLocationService() // Foreground service indítása
            lifecycleScope.launch {
                val location = getLastKnownLocation(this@MainActivity, fusedLocationClient)
                Log.w("Location", "Location: $location")
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
        handleLocationPermission()
        setContent {
            SportTrackerTheme(dynamicColor = true) {
                Surface {
//                    MapScreen(
//                        this,
//                        fusedLocationClient,
//                        userLocation,
//                        locationPermissionGranted,
//                        locationRepository,
//                        tourSettingsViewModel,
//                        tourStartedSettingsViewModel
//                    )
//                    MainScreen(
//                        this,
//                        fusedLocationClient,
//                        locationRepository,
//                        tourSettingsViewModel,
//                        tourStartedSettingsViewModel
//                    )
                    NavGraph(
                        activity = this,
                        fusedLocationClient = fusedLocationClient,
                        locationRepository = locationRepository,
                        tourSettingsViewModel = tourSettingsViewModel,
                        tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                        locationViewmodel = locationViewModel
                    )
                }
            }
        }

        // Engedélyek kérése
        //handleLocationPermission()
    }

    fun handleLocationPermission() {
        Log.d("handleLocationPermission", "Checking location permissions...")

        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.d("handleLocationPermission", "Adding FOREGROUND_SERVICE_LOCATION permission")
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        // Ellenőrizzük, hogy minden szükséges engedély megvan-e
        val allPermissionsGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            Log.d("handleLocationPermission", "All permissions granted. Updating state and starting location service.")
            //locationPermissionGranted.value = true
            //tourSettingsViewModel.updatePermissionGranted(true)
            locationViewModel.updatePermissionGranted(true)
            // TODO: delete this line
            Log.d("handleLocationPermission", "Starting foreground location service...")
            startLocationService() // Foreground service elindítása

            lifecycleScope.launch {
                Log.d("handleLocationPermission", "Fetching last known location...")
                val location = getLastKnownLocation(this@MainActivity, fusedLocationClient)
                userLocation.value = location
                Log.d("handleLocationPermission", "Last known location received: $location")
            }
        } else {
            Log.d("handleLocationPermission", "Permissions not granted. Requesting permissions...")
            locationPermissionLauncher.launch(permissions.toTypedArray())
        }
    }


    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationRepository.stopLocationUpdates()
    }
}