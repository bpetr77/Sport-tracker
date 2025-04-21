package hu.bme.aut.android.sporttracker.ui.screens.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier


@Composable
fun TourDetailsScreen(tourId: Long) {
    val tour = remember { TourRepository.getTourById(tourId) }
    val locations = tour?.locationHistory

    GoogleMap(modifier = Modifier.fillMaxSize()) {
        val polylinePoints = locations?.map { LatLng(it.latitude, it.longitude) } ?: emptyList()
        Polyline(
            points = polylinePoints,
            color = Color.Blue,
            width = 5f
        )
    }
}