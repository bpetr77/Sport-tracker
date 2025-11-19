package hu.bme.aut.android.sporttracker.ui.screens.map

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.constans.PredefinedObjects
import hu.bme.aut.android.sporttracker.data.local.phoneData.getScreenSize
import hu.bme.aut.android.sporttracker.data.repository.impl.LocationRepositoryImpl
import hu.bme.aut.android.sporttracker.data.repository.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.domain.util.location.getAddressFromLatLng
import hu.bme.aut.android.sporttracker.domain.util.location.isLocationEnabled
import hu.bme.aut.android.sporttracker.ui.components.RoutePlannerSheet
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsScreen
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.RoutePlannerViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import kotlinx.coroutines.launch


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    userLocation: MutableState<LatLng?>,
    locationPermissionGranted: MutableState<Boolean>,
    locationRepositoryImpl: LocationRepositoryImpl,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    routePlannerViewModel: RoutePlannerViewModel
) {
    val context = LocalContext.current
    // TODO: GET THIS CODE TO STATES AND VIEWMODELS
    var isLocationEnabled by remember { mutableStateOf(isLocationEnabled(context)) }

    DisposableEffect(Unit) {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                isLocationEnabled = isLocationEnabled(context)
            }
        }
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val defaultLocation = LatLng(47.497913, 19.040236) // Budapest
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value ?: defaultLocation, 15f)
    }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showRoutePlanner by remember { mutableStateOf(false) }
    var showCancelButton by remember { mutableStateOf(false) }

    // TODO: viewmodel
    var showBottomSheet by remember { mutableStateOf(false) }
    val isTourStarted by tourSettingsViewModel.isTourStarted.collectAsState()
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }

    var boundingBoxPoints by remember { mutableStateOf<List<LatLng>?>(null) }

    val routePoints by routePlannerViewModel.routePoints.collectAsState()

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
                        fromText = getAddressFromLatLng(activity, latLng)
                    } else {
                        toMarker = latLng
                        toText = getAddressFromLatLng(activity, latLng)
                    }
                    clickCount++
                }
            }
        ) {
            //TODO: maybe consider this logic, because it is not as stable as it should be
            val locations by locationRepositoryImpl.locations.collectAsState()

            val segments = mutableListOf<MutableList<LatLng>>()
            var currentSegment = mutableListOf<LatLng>()

            if (showRoutePlanner) {
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
                Polyline(
                    points = PredefinedObjects.BUDAPEST_COORDINATES,
                    color = Color.Magenta,
                    width = 8f
                )
                boundingBoxPoints?.let { points ->
                    if (points.size >= 2) {
                        Polyline(
                            points = points,
                            color = Color.Red,
                            width = 4f
                        )
                    }
                }
            } else {
                fromMarker = null
                toMarker = null
                toText = ""
                fromText = ""
                clickCount = 0
            }


            routePoints.let { points ->
                if (points.size >= 2) {
                    Polyline(
                        points = points,
                        color = Color.Red,
                        width = 4f
                    )
                }
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
                            boundingBoxPoints = boundingBox.corners + boundingBox.corners.first()

                            routePlannerViewModel.prepareAndPlanRoute(fromMarker!!, toMarker!!)
                            Log.d("MapScreen", "Route planned")
                        }
                    },
                    onUseCurrentLocation = {
                        activity.handleLocationPermission()
                        if (locationPermissionGranted.value) {
                            activity.checkLocationEnabledAndRequestIfNeeded()
                            activity.lifecycleScope.launch {
                                val newLocation = getLastKnownLocation(activity, fusedLocationClient)
                                userLocation.value = newLocation
                                newLocation?.let {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                                }
                                fromMarker = newLocation
                                fromText = getAddressFromLatLng(activity, newLocation)
                                clickCount = 1
                            }
                        } else {
                            Log.w("MapScreen", "Location permission not granted")
                        }
                    },
                    onSwap = {
                        val tempMarker = fromMarker
                        fromMarker = toMarker
                        toMarker = tempMarker

                        val tempText = fromText
                        fromText = toText
                        toText = tempText

                        clickCount = 0
                    }
                )
            }
        }
        FloatingActionButton(
            onClick = {
                if(showCancelButton){
                    routePlannerViewModel.clearPoints()
                    showCancelButton = false
                }else {
                    showRoutePlanner = !showRoutePlanner
                }
                if (!showRoutePlanner && routePlannerViewModel.routePoints.value.isNotEmpty()){
                    showCancelButton = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 145.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Image(
                painter = if(!showCancelButton)painterResource(id = R.drawable.outline_map_search_24)else(painterResource(id = R.drawable.outline_cancel_24)),
                contentDescription = "Location icon",
                modifier = Modifier.size(26.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                activity.handleLocationPermission()
                if (locationPermissionGranted.value) {
                    activity.checkLocationEnabledAndRequestIfNeeded()
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
                painter = if(isLocationEnabled){painterResource(id = R.drawable.baseline_my_location_24)}else(painterResource(id = R.drawable.baseline_location_searching_24)),
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
                    locationRepositoryImpl = locationRepositoryImpl,
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
                        locationRepositoryImpl.stopLocationUpdates()
                        tourSettingsViewModel.toggleTourState()
                    },
                    pauseLocationUpdates = { locationRepositoryImpl.pauseLocationUpdates() },
                    resumeLocationUpdates = { locationRepositoryImpl.resumeLocationUpdates() },
                    tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                    tourSettingsViewModel = tourSettingsViewModel
                )
            }
        }
    }
}


