package hu.bme.aut.android.sporttracker.ui.screens.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout


@Composable
fun TourDetailsScreen(tourId: Long,
                      tourRepository: TourRepository,
                      drawerState: DrawerState,
                      onMenuClick: () -> Unit,
                      onToursClick: () -> Unit,
                      onMapClick: () -> Unit
){
    // Fetch the tour asynchronously
    val tour = produceState<TourEntity?>(initialValue = null, tourId) {
        value = tourRepository.getTourById(tourId)
    }.value

    val locations = tour?.locationHistory ?: emptyList()
    println(locations)
    println(tour)
    val cameraPositionState = rememberCameraPositionState()

    if (locations.isNotEmpty()) {
        val firstLocation = LatLng(locations.first().latitude, locations.first().longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(firstLocation, 15f)
    }
    MainLayout(
        iconTint = Color.Black,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick
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
            val polylinePoints = locations.map { LatLng(it.latitude, it.longitude) }
            if (locations.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = Color.Blue,
                    width = 5f
                )
            }
        }
    }
}