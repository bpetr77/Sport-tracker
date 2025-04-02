package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel

@Composable
fun TourElement(tour: TourEntity) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = "Távolság: ${tour.totalDistance} km")
            Text(text = "Idő: ${tour.endTime - tour.startTime} perc")
        }
    }
}