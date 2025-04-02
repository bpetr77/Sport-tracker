package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import hu.bme.aut.android.sporttracker.ui.components.TourElement
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel

@Composable
fun TourMenuScreen(tourRepository: TourRepository) {
    val tours = tourRepository.getAllTours()
    Log.w("AAAAAAAAAAAAAAAAAAAAAAAA", "Tours: $tours")
    LazyColumn {
        items(tours) { tour ->
            TourElement(tour)
        }
    }
    Text("Tours")
}