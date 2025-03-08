package hu.bme.aut.android.sporttracker.ui.screens.Settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.ui.components.RadioButtonSingleSelection
import hu.bme.aut.android.sporttracker.ui.components.TransportButton
import hu.bme.aut.android.sporttracker.ui.components.UserInputTextField

@Composable
fun TourSettingsScreen(
    locationRepository: LocationRepository,
    onStartTour: (String) -> Unit,
    onStopTour: () -> Unit
){
    var selectedTransportMode by remember { mutableStateOf<String?>(null) }
    var isTourStarted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Túra beállításai", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            TransportButton(
                transportType = "Gyalog",
                iconRes = R.drawable.baseline_hiking_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { selectedTransportMode = it }
            )

            TransportButton(
                transportType = "Bicikli",
                iconRes = R.drawable.baseline_directions_bike_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { selectedTransportMode = it }
            )

            TransportButton(
                transportType = "Autó",
                iconRes = R.drawable.baseline_directions_car_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { selectedTransportMode = it }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        RadioButtonSingleSelection(Modifier)

        UserInputTextField()

        Spacer(modifier = Modifier.height(30.dp))


        Button(
            onClick = {
                if (isTourStarted) {
                    locationRepository.stopLocationUpdates()
                    onStopTour()
                } else {
                    if (selectedTransportMode != null) {
                        locationRepository.startLocationUpdates()
                        onStartTour(selectedTransportMode!!)
                    } else {
                        Log.w("TourSettingsScreen", "Nem választottál közlekedési módot!")
                    }
                }
                isTourStarted = !isTourStarted
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isTourStarted) Color(red = 210, green = 210, blue = 210, alpha = 255) else Color(red = 210, green = 50, blue = 50, alpha = 255)
            )
        ) {
            Text(
                text = if (isTourStarted) "Túra leállítása" else "Túra indítása",
                color = Color.Black
               )
        }
    }
}