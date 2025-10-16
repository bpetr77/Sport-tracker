package hu.bme.aut.android.sporttracker.ui.screens.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.local.phoneData.getScreenSize
import hu.bme.aut.android.sporttracker.data.repository.location.LocationRepository
import hu.bme.aut.android.sporttracker.data.repository.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.components.RoutePlannerSheet
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.RoutePlannerViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import kotlin.div
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    userLocation: MutableState<LatLng?>,
    locationPermissionGranted: MutableState<Boolean>,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    routePlannerViewModel: RoutePlannerViewModel
) {
    val defaultLocation = LatLng(47.497913, 19.040236) // Budapest
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value ?: defaultLocation, 15f)
    }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showRoutePlanner by remember { mutableStateOf(false) }

    // TODO: viewmodel
    var showBottomSheet by remember { mutableStateOf(false) }
    val isTourStarted by tourSettingsViewModel.isTourStarted.collectAsState()
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }

    var fromMarker by remember { mutableStateOf<LatLng?>(null) }
    var toMarker by remember { mutableStateOf<LatLng?>(null) }
    var clickCount by remember { mutableStateOf(0) }

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
            ),
            onMapClick = { latLng ->
                if (showRoutePlanner) {
                    if (clickCount % 2 == 0) {
                        fromMarker = latLng
                        fromText = "${latLng.latitude}, ${latLng.longitude}"
                    } else {
                        toMarker = latLng
                        toText = "${latLng.latitude}, ${latLng.longitude}"
                    }
                    clickCount++
                }
            }
        ) {
            //TODO: maybe consider this logic, because it is not as stable as it should be
            val locations by locationRepository.locations.collectAsState()
            val isPaused by tourStartedSettingsViewModel.isPaused.collectAsState()

            val segments = mutableListOf<MutableList<LatLng>>()
            var currentSegment = mutableListOf<LatLng>()

            fromMarker?.let {
                Marker(
                    state = rememberUpdatedMarkerState(position = it),
                    title = "From"
                )
            }
            toMarker?.let {
                Marker(
                    state = rememberUpdatedMarkerState(position = it),
                    title = "To"
                )
            }
            if (locations.isNotEmpty()) {
                for (i in locations.indices) {
                    val currentLocation = locations[i]
                    val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)

                    if (i == 0) {
                        // Start the first segment
                        currentSegment.add(currentLatLng)
                    } else {
                        val previousLocation = locations[i - 1]
                        val timeDifference =
                            currentLocation.timestamp - (previousLocation.timestamp + 5000)

                        if (timeDifference <= 5000) {
                            // Add to the current segment
                            currentSegment.add(currentLatLng)
                        } else {
                            // Start a new segment
                            segments.add(currentSegment)
                            currentSegment = mutableListOf(currentLatLng)
                        }
                    }
                }

                // Add the last segment
                if (currentSegment.isNotEmpty()) {
                    segments.add(currentSegment)
                }

                // Draw the segments
                for (segment in segments) {
                    if (segment.size >= 2) {
                        Polyline(
                            points = segment,
                            color = Color.Blue,
                            width = 5f
                        )
                    }
                }
            }
        }
        DisappearingScaleBar(
            modifier = Modifier
                .padding(bottom = 17.dp, end = 5.dp)
                .align(Alignment.BottomStart),
            cameraPositionState = cameraPositionState
        )

        if (showRoutePlanner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((getScreenSize().second / 3.4).dp)
                    .align(Alignment.TopCenter)
                    .background(Color(15, 15, 15, 240))
            ) {
                RoutePlannerSheet(
                    fromText = fromText,
                    toText = toText,
                    onFromChange = { fromText = it },
                    onToChange = { toText = it },
                    onClick = {
                        if (fromMarker != null && toMarker != null) {
                            val boundingBox = routePlannerViewModel.calculateBoundingBox(fromMarker!!, toMarker!!)
                            routePlannerViewModel.loadGraphForArea(boundingBox.minLat, boundingBox.maxLat, boundingBox.minLon, boundingBox.maxLon)
                        }
                    }
                )
            }
        }
        FloatingActionButton(
            onClick = {
                showRoutePlanner = !showRoutePlanner
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 145.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_map_search_24),
                contentDescription = "Location icon",
                modifier = Modifier.size(26.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                activity.handleLocationPermission()
                if (locationPermissionGranted.value) {
                    activity.lifecycleScope.launch {
                        val newLocation = getLastKnownLocation(activity, fusedLocationClient)
                        userLocation.value = newLocation
                        newLocation?.let {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                        }
                    }
                } else {
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
    // Show the bottom sheet when the button is clicked
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
            } else {
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


