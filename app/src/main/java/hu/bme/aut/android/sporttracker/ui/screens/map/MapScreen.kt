package hu.bme.aut.android.sporttracker.ui.screens.map

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.data.location.repository.getLastKnownLocation
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    userLocation: MutableState<LatLng?>,
    locationPermissionGranted: MutableState<Boolean>,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel
) {
    val defaultLocation = LatLng(47.497913, 19.040236) // Budapest
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value ?: defaultLocation, 15f)
    }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val isTourStarted by tourSettingsViewModel.isTourStarted.collectAsState()
    var locations = locationRepository.locations.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = true
            ),
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted.value
            )
        ) {
            val locations by locationRepository.locations.collectAsState()

            if (locations.isNotEmpty()) {
                Polyline(
                    points = locations.map { LatLng(it.latitude, it.longitude) },
                    color = Color.Blue,
                    width = 5f
                )
            }
        }

        FloatingActionButton(
            onClick = {
                Log.w("MapScreen", "Location button clicked")
                activity.handleLocationPermission()

                Log.d("MapScreen", "Location permission granted value: ${locationPermissionGranted.value}")

                if (locationPermissionGranted.value) {
                    Log.w("MapScreen", "Location permission granted")
                    activity.lifecycleScope.launch {
                        val newLocation = getLastKnownLocation(activity, fusedLocationClient)
                        userLocation.value = newLocation
                        newLocation?.let {
                            Log.d("MapScreen", "Animating camera to new location: $it")
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                        }
                    }
                }else {
                    Log.w("MapScreen", "Location permission not granted")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_my_location_24),
                contentDescription = "Location icon",
                modifier = Modifier.size(26.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    showBottomSheet = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.White
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_start_24),
                contentDescription = "Record start icon",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            if (!isTourStarted) {
                TourSettingsScreen(
                    locationRepository = locationRepository,
                    tourSettingsViewModel = tourSettingsViewModel,
                    onStartTour = {
                        Log.d("MapScreen", "Túra indítása...")
                    },
                    onStopTour = {
                        Log.d("MapScreen", "Túra leállítása...")
                    }
                )
            }else {
                TourStartedSettingsScreen(
                    stopLocationUpdates = {
                        locationRepository.stopLocationUpdates()
                        tourSettingsViewModel.toggleTourState()
                    },
                    pauseLocationUpdates = { locationRepository.pauseLocationUpdates() },
                    resumeLocationUpdates = { locationRepository.resumeLocationUpdates() },
                    tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                    tourSettingsViewModel = tourSettingsViewModel
                )
            }
        }
    }
}


