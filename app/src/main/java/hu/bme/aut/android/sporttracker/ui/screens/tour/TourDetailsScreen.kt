package hu.bme.aut.android.sporttracker.ui.screens.tour

import android.view.View
import android.widget.RelativeLayout
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import com.google.maps.android.compose.widgets.ScaleBar

@Composable
fun TourDetailsScreen(tourId: Long,
                      tourRepository: TourRepository,
                      drawerState: DrawerState,
                      onMenuClick: () -> Unit,
                      onToursClick: () -> Unit,
                      onMapClick: () -> Unit,
                        onAllToursClick: () -> Unit
){
    // Fetch the tour asynchronously
    val tour = produceState<TourEntity?>(initialValue = null, tourId) {
        value = tourRepository.getTourById(tourId)
    }.value

    val locations = tour?.locationHistory ?: emptyList()
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
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick
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