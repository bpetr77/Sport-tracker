package hu.bme.aut.android.sporttracker.ui.screens.tour

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import hu.bme.aut.android.sporttracker.data.repository.location.LocationTourDataSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout

import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import hu.bme.aut.android.sporttracker.ui.sign_in.UserData

@Composable
fun AllToursScreen(
    locationTourDataSource: LocationTourDataSource,
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onAllToursClick: () -> Unit,
    userData: UserData?,
    onSignOut: () -> Unit
) {
    // Fetch all tours asynchronously
    val tours = produceState<List<TourEntity>>(initialValue = emptyList()) {
        value = locationTourDataSource.getAllTours()
    }.value

    // Combine all locations from all tours
    val allLocations = tours.flatMap { it.locationHistory }
    val cameraPositionState = rememberCameraPositionState()

    if (allLocations.isNotEmpty()) {
        val firstLocation = LatLng(allLocations.first().latitude, allLocations.first().longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(firstLocation, 10f)
    }

    val modeColors = mapOf(
        "Gyalog" to Color(0xFF008D00),
        "Bicikli" to Color(0xFF051E9F),
        "Autó" to Color(0xFFBE2222)
    )

    MainLayout(
        iconTint = Color.Black,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick,
        userData = userData,
        onSignOut = onSignOut
    ) {
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
            )
        ) {
            tours.forEach { tour ->
                val polylinePoints = tour.locationHistory.map { LatLng(it.latitude, it.longitude) }
                if (polylinePoints.isNotEmpty()) {
                    Polyline(
                        points = polylinePoints,
                        color = Color(0x5234CECA),
                        width = 60f
                    )
                }
            }
            tours.forEach { tour ->
                val polylinePoints = tour.locationHistory.map { LatLng(it.latitude, it.longitude) }
                val polylineColor = modeColors[tour.transportationMode] ?: Color.Gray
                if (polylinePoints.isNotEmpty()) {
                    Polyline(
                        points = polylinePoints,
                        color = polylineColor,
                        width = 8f
                    )
                }
            }

        }
        Box(modifier = Modifier.fillMaxSize()) {
            DisappearingScaleBar(
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 5.dp)
                    .align(Alignment.BottomEnd),
                cameraPositionState = cameraPositionState
            )
        }
    }
}