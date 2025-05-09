package hu.bme.aut.android.sporttracker.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel


@Composable
fun TourElement(tour: TourEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tour.transportationMode?.let { mode: String ->
            val imageRes = when (mode) {
                "Gyalog" -> R.drawable.baseline_hiking_24
                "Bicikli" -> R.drawable.baseline_directions_bike_24
                "Autó" -> R.drawable.baseline_directions_car_24
                else -> R.drawable.ic_launcher_foreground // Default image
            }
            val imageColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Selected transport mode: $mode",
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(imageColor)
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(text = "Időtartam: ${(tour.endTime - tour.startTime) / 60000} perc")
            Text(text = "Távolság: ${tour.totalDistance} m")
        }
    }
}