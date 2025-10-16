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
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import hu.bme.aut.android.sporttracker.data.local.graph.database.GraphDatabaseProvider
import hu.bme.aut.android.sporttracker.data.local.tour.di.provideTourDao
import hu.bme.aut.android.sporttracker.data.remote.firebase.di.provideFirebaseAuth
import hu.bme.aut.android.sporttracker.data.remote.firebase.di.provideFirestore
import hu.bme.aut.android.sporttracker.data.repository.impl.TourRepositoryImpl
import hu.bme.aut.android.sporttracker.data.repository.location.LocationRepository
import hu.bme.aut.android.sporttracker.data.repository.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.data.routePlanner.repository.GraphRepository
import hu.bme.aut.android.sporttracker.data.routePlanner.repository.OSMRepository
import hu.bme.aut.android.sporttracker.data.service.LocationService
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.theme.SportTrackerTheme
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModelFactory
import hu.bme.aut.android.sporttracker.ui.navigation.NavGraph
import hu.bme.aut.android.sporttracker.ui.sign_in.GoogleAuthUiClient
import hu.bme.aut.android.sporttracker.ui.sign_in.SignInViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import hu.bme.aut.android.sporttracker.ui.viewModels.RoutePlannerViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.SettingsViewModel

//TODO: should put the viewmodels into the NavGraph
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userLocation = mutableStateOf<LatLng?>(null)
    private lateinit var locationRepository: LocationRepository
    private lateinit var routeRepository: OSMRepository

    private val tourSettingsViewModel: TourSettingsViewModel by viewModels()
//    private val tourStartedSettingsViewModel: TourStartedSettingsViewModel by viewModels {
//        TourStartedSettingsViewModelFactory(locationRepository, TourUseCase, tourRepositoryImpl)
//    }
    private val locationViewModel: LocationViewmodel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
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

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebase inicializálás
        FirebaseApp.initializeApp(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRepository = LocationRepository(fusedLocationClient, this)
        routeRepository = OSMRepository(context = applicationContext)
        val graphDb = GraphDatabaseProvider.getDatabase(applicationContext)
        val graphRepository = GraphRepository(graphDb)

        // tourRepositoryImpl példányosítás
        val tourRepositoryImpl = TourRepositoryImpl(
            dao = provideTourDao(context = applicationContext),
            firestore = provideFirestore(),
            auth = provideFirebaseAuth()
        )

        // ViewModel factory
        val tourStartedSettingsViewModel: TourStartedSettingsViewModel by viewModels {
            TourStartedSettingsViewModelFactory(locationRepository, TourUseCase(), tourRepositoryImpl)
        }

        val routePlannerViewModel = RoutePlannerViewModel(routeRepository, graphRepository)

        handleLocationPermission()
        setContent {
            SportTrackerTheme(dynamicColor = true) {
                Surface {
                    NavGraph(
                        activity = this,
                        fusedLocationClient = fusedLocationClient,
                        locationRepository = locationRepository,
                        tourSettingsViewModel = tourSettingsViewModel,
                        tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                        routePlannerViewModel = routePlannerViewModel,
                        locationViewmodel = locationViewModel,
                        signInViewModel = signInViewModel,
                        settingsViewModel = settingsViewModel,
                        googleAuthUiClient = googleAuthUiClient,
                    )
                }
            }
        }
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